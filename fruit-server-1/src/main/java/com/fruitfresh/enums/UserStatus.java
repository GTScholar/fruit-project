package com.fruitfresh.enums;

import lombok.Getter;

/**
 * 用户状态（t_user.status）
 */
@Getter
public enum UserStatus {

    /** 禁用 */
    DISABLED(0, "禁用"),
    /** 正常 */
    NORMAL(1, "正常");

    private final Integer code;
    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean is(Integer code) {
        return this.code.equals(code);
    }

    public static UserStatus of(Integer code) {
        for (UserStatus status : values()) {
            if (status.is(code)) {
                return status;
            }
        }
        return null;
    }
}
