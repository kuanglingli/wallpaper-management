package com.wallpaper.management.common;

/**
 * 返回状态码枚举类
 */
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "系统错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(4001, "用户名或密码错误"),

    /**
     * 用户已存在
     */
    USER_EXIST(4002, "用户已存在"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(4003, "用户不存在"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(4004, "验证码错误"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(5001, "文件上传失败"),

    /**
     * 文件类型错误
     */
    FILE_TYPE_ERROR(5002, "文件类型错误"),

    /**
     * 文件大小超过限制
     */
    FILE_SIZE_ERROR(5003, "文件大小超过限制");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
} 