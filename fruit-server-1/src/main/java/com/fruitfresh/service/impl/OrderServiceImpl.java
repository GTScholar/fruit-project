package com.fruitfresh.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.config.RabbitMQConfig;
import com.fruitfresh.dto.OrderCreateDTO;
import com.fruitfresh.entity.Address;
import com.fruitfresh.entity.Cart;
import com.fruitfresh.entity.OrderItem;
import com.fruitfresh.entity.Orders;
import com.fruitfresh.entity.Product;
import com.fruitfresh.enums.OrderStatus;
import com.fruitfresh.enums.ProductStatus;
import com.fruitfresh.enums.YesNo;
import com.fruitfresh.mapper.AddressMapper;
import com.fruitfresh.mapper.CartMapper;
import com.fruitfresh.mapper.OrderItemMapper;
import com.fruitfresh.mapper.OrdersMapper;
import com.fruitfresh.mapper.ProductMapper;
import com.fruitfresh.service.OrderService;
import com.fruitfresh.service.ProductService;
import com.fruitfresh.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现：下单 / 支付 / 取消 / 发货 / 收货 / 超时关单
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final ProductService productService;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 创建订单（购物车勾选商品结算）
     * 流程：校验地址与库存 -> 创建订单+明细 -> 原子扣库存 -> 清空勾选 -> 发送超时关单消息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders create(Long userId, OrderCreateDTO dto) {
        // 1. 校验收货地址
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }

        // 2. 查询购物车勾选商品
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getChecked, YesNo.YES.getCode()));
        if (carts.isEmpty()) {
            throw new BusinessException("请先勾选要结算的商品");
        }

        // 3. 校验商品与库存、计算金额、生成明细
        Map<Long, Product> productMap = new HashMap<>();
        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || !ProductStatus.ON_SHELF.is(product.getStatus())) {
                throw new BusinessException("商品已下架，请刷新购物车");
            }
            int stock = product.getStock() == null ? 0 : product.getStock();
            if (stock < cart.getQuantity()) {
                throw new BusinessException("商品「" + product.getName() + "」库存不足");
            }
            productMap.put(product.getId(), product);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            items.add(item);

            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        // 4. 创建订单（状态：待付款）
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setRemark(dto.getRemark());
        ordersMapper.insert(order);

        // 5. 保存明细 + 原子扣减库存（数据库条件更新，避免并发超卖）
        for (int i = 0; i < carts.size(); i++) {
            Cart cart = carts.get(i);
            Product product = productMap.get(cart.getProductId());

            OrderItem item = items.get(i);
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);

            int affected = productMapper.deductStock(product.getId(), cart.getQuantity());
            if (affected == 0) {
                // 抛异常触发整个事务回滚，已写入的订单与明细一并撤销
                throw new BusinessException("商品「" + product.getName() + "」库存不足，请刷新后重试");
            }
        }

        // 6. 删除已勾选的购物车记录
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getChecked, YesNo.YES.getCode()));

        // 7. 清除商品相关缓存
        carts.forEach(cart -> productService.evictProductCache(cart.getProductId()));

        // 8. 发送延迟消息：超时未支付自动取消（MQ 故障不影响下单主流程）
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                    RabbitMQConfig.ORDER_DELAY_ROUTING_KEY, order.getOrderNo());
        } catch (AmqpException e) {
            log.warn("发送订单超时关单消息失败，订单号: {}", order.getOrderNo(), e);
        }
        return order;
    }

    /** 模拟支付：待付款 -> 待发货 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long userId, Long orderId) {
        Orders order = getOwnedOrder(userId, orderId);
        if (!OrderStatus.UNPAID.is(order.getStatus())) {
            throw new BusinessException("当前订单状态不可支付");
        }
        Orders update = new Orders();
        update.setId(order.getId());
        update.setStatus(OrderStatus.PAID.getCode());
        update.setPayTime(LocalDateTime.now());
        ordersMapper.updateById(update);
    }

    /** 取消订单（仅待付款）：恢复库存 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long orderId) {
        Orders order = getOwnedOrder(userId, orderId);
        if (!OrderStatus.UNPAID.is(order.getStatus())) {
            throw new BusinessException("仅待付款订单可以取消");
        }
        doCancel(order);
    }

    /** 确认收货：待收货 -> 已完成 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long userId, Long orderId) {
        Orders order = getOwnedOrder(userId, orderId);
        if (!OrderStatus.DELIVERED.is(order.getStatus())) {
            throw new BusinessException("当前订单状态不可确认收货");
        }
        Orders update = new Orders();
        update.setId(order.getId());
        update.setStatus(OrderStatus.FINISHED.getCode());
        update.setFinishTime(LocalDateTime.now());
        ordersMapper.updateById(update);
    }

    /** 我的订单分页查询（可按状态筛选） */
    @Override
    public Page<Orders> list(Long userId, int pageNum, int pageSize, Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        Page<Orders> result = ordersMapper.selectPage(page, new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .eq(status != null, Orders::getStatus, status)
                .orderByDesc(Orders::getCreateTime));
        // 一次查出本页所有明细，避免逐条查询
        fillItems(result.getRecords());
        return result;
    }

    /** 订单详情（含明细） */
    @Override
    public OrderDetailVO detail(Long userId, Long orderId) {
        Orders order = getOwnedOrder(userId, orderId);
        return new OrderDetailVO(order, listItems(order.getId()));
    }

    // ==================== 管理端 ====================

    /** 管理端：订单分页查询 */
    @Override
    public Page<Orders> adminPage(int pageNum, int pageSize, String orderNo, Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        Page<Orders> result = ordersMapper.selectPage(page, new LambdaQueryWrapper<Orders>()
                .like(StrUtil.isNotBlank(orderNo), Orders::getOrderNo, orderNo)
                .eq(status != null, Orders::getStatus, status)
                .orderByDesc(Orders::getCreateTime));
        fillItems(result.getRecords());
        return result;
    }

    /** 管理端：订单详情 */
    @Override
    public OrderDetailVO adminDetail(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return new OrderDetailVO(order, listItems(orderId));
    }

    /** 管理端：发货（待发货 -> 待收货） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.PAID.is(order.getStatus())) {
            throw new BusinessException("仅待发货订单可以发货");
        }
        Orders update = new Orders();
        update.setId(order.getId());
        update.setStatus(OrderStatus.DELIVERED.getCode());
        update.setDeliveryTime(LocalDateTime.now());
        ordersMapper.updateById(update);
    }

    /** 超时自动取消（RabbitMQ 消费者调用） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void timeoutCancel(String orderNo) {
        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getOrderNo, orderNo));
        if (order == null || !OrderStatus.UNPAID.is(order.getStatus())) {
            return;
        }
        log.info("订单超时未支付，自动取消: {}", orderNo);
        doCancel(order);
    }

    // ==================== 私有方法 ====================

    /**
     * 批量填充订单明细：先按订单ID一次性查出，再在内存中分组，避免 N+1 查询
     */
    private void fillItems(List<Orders> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        List<Long> orderIds = orders.stream().map(Orders::getId).collect(Collectors.toList());
        List<OrderItem> allItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderId, orderIds));
        Map<Long, List<OrderItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        orders.forEach(order -> order.setItems(itemMap.getOrDefault(order.getId(), List.of())));
    }

    /** 查询单个订单的明细 */
    private List<OrderItem> listItems(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
    }

    /** 执行取消：置为已取消 + 恢复库存 */
    private void doCancel(Orders order) {
        Orders update = new Orders();
        update.setId(order.getId());
        update.setStatus(OrderStatus.CANCELLED.getCode());
        ordersMapper.updateById(update);
        restoreStock(order.getId());
    }

    /** 恢复库存、扣减销量（原子更新） */
    private void restoreStock(Long orderId) {
        for (OrderItem item : listItems(orderId)) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
            productService.evictProductCache(item.getProductId());
        }
    }

    private Orders getOwnedOrder(Long userId, Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    /** 生成订单号：yyyyMMddHHmmss + 6位随机数 */
    private String generateOrderNo() {
        return DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
    }
}
