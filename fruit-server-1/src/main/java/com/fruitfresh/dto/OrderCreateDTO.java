package com.fruitfresh.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单请求
 */
@Data
public class OrderCreateDTO {

    /** 收货地址ID */
    @NotNull(message = "请选择收货地址")
    private Long addressId;

    /** 订单备注，选填 */
    private String remark;
}
