package com.fruitfresh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.dto.OrderCreateDTO;
import com.fruitfresh.entity.Orders;
import com.fruitfresh.vo.OrderDetailVO;

/**
 * 订单服务接口：下单 / 支付 / 取消 / 发货 / 收货 / 超时关单
 */
public interface OrderService {

    /** 创建订单（购物车勾选商品结算） */
    Orders create(Long userId, OrderCreateDTO dto);

    /** 模拟支付：待付款 -> 待发货 */
    void pay(Long userId, Long orderId);

    /** 取消订单（仅待付款）：恢复库存 */
    void cancel(Long userId, Long orderId);

    /** 确认收货：待收货 -> 已完成 */
    void confirm(Long userId, Long orderId);

    /** 我的订单分页查询（可按状态筛选） */
    Page<Orders> list(Long userId, int pageNum, int pageSize, Integer status);

    /** 订单详情（含明细） */
    OrderDetailVO detail(Long userId, Long orderId);

    /** 管理端：订单分页查询 */
    Page<Orders> adminPage(int pageNum, int pageSize, String orderNo, Integer status);

    /** 管理端：订单详情 */
    OrderDetailVO adminDetail(Long orderId);

    /** 管理端：发货（待发货 -> 待收货） */
    void deliver(Long orderId);

    /** 超时自动取消（RabbitMQ 消费者调用） */
    void timeoutCancel(String orderNo);
}
