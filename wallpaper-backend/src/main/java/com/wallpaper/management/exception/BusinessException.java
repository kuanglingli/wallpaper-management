package com.wallpaper.management.exception;

import com.wallpaper.management.common.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        this.code = ResultCode.ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码枚举
     */
    public BusinessException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
} 