package com.fruitfresh.common;

import lombok.Getter;

/**
 * 业务异常：在 Service 层主动抛出，由全局异常处理器统一捕获
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
