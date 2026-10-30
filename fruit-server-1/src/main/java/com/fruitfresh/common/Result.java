package com.fruitfresh.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    /** 状态码 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 数据 */
    private T data;

    public Result() {
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 成功（无数据） */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /** 成功（带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /** 失败（默认业务错误） */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message, null);
    }

    /** 失败（自定义状态码） */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
