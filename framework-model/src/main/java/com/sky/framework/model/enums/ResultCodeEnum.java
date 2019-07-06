package com.sky.framework.model.enums;


/**
 * @author
 */
public enum ResultCodeEnum implements ErrorCode {
    SUCCESS(0, "成功");

//    FAILURE(500, "失败");

    private Integer code;

    private String msg;

    private ResultCodeEnum(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}
