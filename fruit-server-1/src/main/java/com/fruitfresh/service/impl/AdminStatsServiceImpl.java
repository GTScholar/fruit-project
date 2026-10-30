package com.fruitfresh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruitfresh.entity.Orders;
import com.fruitfresh.entity.User;
import com.fruitfresh.enums.UserType;
import com.fruitfresh.mapper.OrdersMapper;
import com.fruitfresh.mapper.ProductMapper;
import com.fruitfresh.mapper.UserMapper;
import com.fruitfresh.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端数据统计服务实现（仪表盘）
 */
@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrdersMapper ordersMapper;
    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    /** 核心指标：用户数 / 商品数 / 订单数 / 今日订单 / 销售总额 */
    @Override
    public Map<String, Object> overview() {
        Map<String, Object> data = new HashMap<>();
        // 普通用户数
        data.put("userCount", userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, UserType.NORMAL.getCode())));
        // 上架商品数
        data.put("productCount", productMapper.selectCount(null));
        // 订单总数
        data.put("orderCount", ordersMapper.selectCount(null));
        // 今日新增订单
        data.put("todayOrderCount", ordersMapper.selectCount(new LambdaQueryWrapper<Orders>()
                .ge(Orders::getCreateTime, LocalDate.now().atStartOfDay())));
        // 销售总额（已支付订单）
        data.put("totalSales", ordersMapper.selectTotalSales());
        return data;
    }

    /** 近7天每日销售额趋势 */
    @Override
    public List<Map<String, Object>> salesTrend() {
        return ordersMapper.selectDailySales(7);
    }

    /** 分类销售占比 */
    @Override
    public List<Map<String, Object>> categorySales() {
        return ordersMapper.selectCategorySales();
    }

    /** 系统运行状态：MySQL / Redis / RabbitMQ 连通性探测 */
    @Override
    public Map<String, Object> systemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("mysql", checkMysql());
        status.put("redis", checkRedis());
        status.put("rabbitmq", checkRabbit());
        status.put("serverTime", LocalDateTime.now());
        return status;
    }

    /** 探测 MySQL：拿一个真实连接并校验有效性 */
    private boolean checkMysql() {
        try (java.sql.Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /** 探测 Redis：发送 PING 命令 */
    private boolean checkRedis() {
        RedisConnection conn = null;
        try {
            conn = redisTemplate.getConnectionFactory().getConnection();
            return "PONG".equalsIgnoreCase(conn.ping());
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ignored) {
                    // 关闭失败不影响探测结果
                }
            }
        }
    }

    /** 探测 RabbitMQ：创建并校验连接是否打开 */
    private boolean checkRabbit() {
        Connection conn = null;
        try {
            conn = rabbitTemplate.getConnectionFactory().createConnection();
            return conn != null && conn.isOpen();
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ignored) {
                    // 关闭失败不影响探测结果
                }
            }
        }
    }
}
