package com.fruitfresh.enums;

import lombok.Getter;

/**
 * 订单状态
 * <p>
 * 状态流转：待付款 -> 待发货 -> 待收货 -> 已完成；待付款可取消。
 */
@Getter
public enum OrderStatus {

    /** 待付款 */
    UNPAID(0, "待付款"),
    /** 已付款，待发货 */
    PAID(1, "待发货"),
    /** 已发货，待收货 */
    DELIVERED(2, "待收货"),
    /** 已完成 */
    FINISHED(3, "已完成"),
    /** 已取消 */
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /** 判断给定状态码是否等于当前状态 */
    public boolean is(Integer code) {
        return this.code.equals(code);
    }

    /** 按状态码取枚举，未匹配返回 null */
    public static OrderStatus of(Integer code) {
        for (OrderStatus status : values()) {
            if (status.is(code)) {
                return status;
            }
        }
        return null;
    }

    /** 按状态码取描述，未匹配返回空串 */
    public static String descOf(Integer code) {
        OrderStatus status = of(code);
        return status == null ? "" : status.getDesc();
    }
}
