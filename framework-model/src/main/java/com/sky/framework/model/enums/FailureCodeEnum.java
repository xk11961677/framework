package com.sky.framework.model.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author
 */
public enum FailureCodeEnum implements ErrorCode {
    /**
     * Gl 990001 error code enum.
     */
    GL990001(990001, "参数错误"),
    /**
     * Gl 990002 error code enum.
     */
    GL990002(990002, "微服务不在线,或者网络超时"),
    /**
     * Gl 990003 error code enum.
     */
    GL990003(990003, "Bad Request"),
    /**
     * Gl 990004 error code enum.
     */
    GL990004(990004, "找不到指定资源"),
    /**
     * Gl 990005 error code enum.
     */
    GL990005(990005, "签名错误"),
    /**
     * Gl 100001 error code enum.
     */
    AUZ100001(100001, "未授权"),
    /**
     * Gl 100001 error code enum.
     */
    AUZ100016(100016, "token过期"),

    /**
     * Gl 999998 error code enum.
     */
    GL999998(999998, "数据库异常"),
    /**
     * Gl -1 error code enum.
     */
    GL999999(-1, "未知异常"),
    /**
     * Gl 100003 error code enum.
     */
    AUZ100003(100003, "无权访问");


    private Integer code;

    private String msg;

    FailureCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    /**
     * @param code
     * @return FailureCodeEnum
     */
    public static FailureCodeEnum getByCode(Integer code) {
        Optional<FailureCodeEnum> optional = Arrays.stream(values()).filter(e -> Objects.equals(code, e.getCode())).findFirst();
        return optional.orElse(GL999999);
    }
}
