package com.fruitfresh.enums;

import lombok.Getter;

/**
 * 商品上下架状态（t_product.status）
 */
@Getter
public enum ProductStatus {

    /** 下架 */
    OFF_SHELF(0, "下架"),
    /** 上架 */
    ON_SHELF(1, "上架");

    private final Integer code;
    private final String desc;

    ProductStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean is(Integer code) {
        return this.code.equals(code);
    }

    public static ProductStatus of(Integer code) {
        for (ProductStatus status : values()) {
            if (status.is(code)) {
                return status;
            }
        }
        return null;
    }
}
