package com.fruitfresh.enums;

import lombok.Getter;

/**
 * 用户类型
 */
@Getter
public enum UserType {

    /** 普通用户 */
    NORMAL(1, "普通用户"),
    /** 管理员 */
    ADMIN(2, "管理员");

    private final Integer code;
    private final String desc;

    UserType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean is(Integer code) {
        return this.code.equals(code);
    }

    public static UserType of(Integer code) {
        for (UserType type : values()) {
            if (type.is(code)) {
                return type;
            }
        }
        return null;
    }
}
