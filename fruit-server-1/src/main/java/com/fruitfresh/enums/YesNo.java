package com.fruitfresh.enums;

import lombok.Getter;

/**
 * 通用是否标记：购物车勾选、是否热销、是否默认地址等 0/1 字段
 */
@Getter
public enum YesNo {

    /** 否 */
    NO(0, "否"),
    /** 是 */
    YES(1, "是");

    private final Integer code;
    private final String desc;

    YesNo(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean is(Integer code) {
        return this.code.equals(code);
    }

    public static YesNo of(Integer code) {
        for (YesNo value : values()) {
            if (value.is(code)) {
                return value;
            }
        }
        return null;
    }
}
