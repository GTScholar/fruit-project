package com.fruitfresh.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体
 */
@Data
@TableName("t_orders")
public class Orders implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 订单状态 0待付款 1待发货 2待收货 3已完成 4已取消 */
    private Integer status;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人电话 */
    private String receiverPhone;

    /** 收货地址（省市区+详细） */
    private String receiverAddress;

    /** 订单备注 */
    private String remark;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 发货时间 */
    private LocalDateTime deliveryTime;

    /** 完成时间 */
    private LocalDateTime finishTime;

    /** 订单明细（非表字段，列表/详情接口返回） */
    @TableField(exist = false)
    private List<OrderItem> items;

    @TableLogic
    @JsonIgnore
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
