package com.fruitfresh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruitfresh.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 统计指定天数内的每日销售额（统计已支付订单：状态1待发货/2待收货/3已完成）
     *
     * @param days 最近 N 天
     */
    @Select("""
            SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS date,
                   IFNULL(SUM(total_amount), 0) AS amount
            FROM t_orders
            WHERE deleted = 0
              AND status IN (1, 2, 3)
              AND create_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY)
            GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')
            ORDER BY date
            """)
    List<Map<String, Object>> selectDailySales(int days);

    /**
     * 按分类统计销量与销售额
     */
    @Select("""
            SELECT c.name AS categoryName,
                   IFNULL(SUM(oi.quantity), 0) AS salesCount,
                   IFNULL(SUM(oi.total_price), 0) AS salesAmount
            FROM t_order_item oi
            LEFT JOIN t_product p ON oi.product_id = p.id
            LEFT JOIN t_category c ON p.category_id = c.id
            LEFT JOIN t_orders o ON oi.order_id = o.id
            WHERE oi.deleted = 0 AND o.deleted = 0
              AND o.status IN (1, 2, 3)
            GROUP BY c.id, c.name
            ORDER BY salesCount DESC
            """)
    List<Map<String, Object>> selectCategorySales();

    /**
     * 查询全部已支付订单的销售总额
     */
    @Select("""
            SELECT IFNULL(SUM(total_amount), 0)
            FROM t_orders
            WHERE deleted = 0 AND status IN (1, 2, 3)
            """)
    BigDecimal selectTotalSales();
}
