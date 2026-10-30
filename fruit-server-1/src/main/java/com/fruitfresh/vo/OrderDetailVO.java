package com.fruitfresh.vo;

import com.fruitfresh.entity.OrderItem;
import com.fruitfresh.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 订单详情视图对象：订单主体 + 商品明细
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO implements Serializable {

    /** 订单主体 */
    private Orders order;

    /** 订单商品明细 */
    private List<OrderItem> items;
}
