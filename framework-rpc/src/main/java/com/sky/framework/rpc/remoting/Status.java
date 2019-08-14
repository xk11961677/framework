package com.sky.framework.rpc.remoting;

/**
 * @author
 */
public enum Status {

    /**
     * 正常 - 请求已完成
     */
    OK((byte) 0x20, "OK"),
    // 内部错误 — 因为意外情况, 客户端不能发送请求
    CLIENT_ERROR((byte) 0x30, "CLIENT_ERROR"),
    // 超时 - 客户端超时
    CLIENT_TIMEOUT((byte) 0x31, "CLIENT_TIMEOUT"),
    // 超时 - 服务端超时
    SERVER_TIMEOUT((byte) 0x32, "SERVER_TIMEOUT"),
    // 错误请求 — 请求中有语法问题, 或不能满足请求
    BAD_REQUEST((byte) 0x40, "BAD_REQUEST"),
    // 找不到 - 指定服务不存在
    SERVICE_NOT_FOUND((byte) 0x44, "SERVICE_NOT_FOUND"),
    // 内部错误 — 因为意外情况, 服务器不能完成请求
    SERVER_ERROR((byte) 0x50, "SERVER_ERROR"),
    // 内部错误 — 服务器太忙, 无法处理新的请求
    SERVER_BUSY((byte) 0x51, "SERVER_BUSY"),
    // 服务错误 - 服务执行时出现预期内的异常
    SERVICE_EXPECTED_ERROR((byte) 0x52, "SERVICE_EXPECTED_ERROR"),
    // 服务错误 - 服务执行意外出错
    SERVICE_UNEXPECTED_ERROR((byte) 0x53, "SERVICE_UNEXPECTED_ERROR"),
    // 服务错误 - App级别服务限流
    APP_FLOW_CONTROL((byte) 0x54, "APP_FLOW_CONTROL"),
    // 服务错误 - Provider级别服务限流
    PROVIDER_FLOW_CONTROL((byte) 0x55, "PROVIDER_FLOW_CONTROL"),
    // 客户端反序列化错误
    DESERIALIZATION_FAIL((byte) 0x60, "DESERIALIZATION_FAIL");

    Status(byte value, String description) {
        this.value = value;
        this.description = description;
    }

    private byte value;
    private String description;

    public static Status parse(byte value) {
        for (Status s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }

    public byte value() {
        return value;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return description();
    }
}
