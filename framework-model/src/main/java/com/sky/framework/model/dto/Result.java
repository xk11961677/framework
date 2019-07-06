package com.sky.framework.model.dto;


import com.sky.framework.model.enums.ResultCodeEnum;

import java.io.Serializable;

/**
 * 通用返回实体
 *
 * @author
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer resultCode;

    private Integer failureCode;

    private String failureMessage;

    private T data;

    public Result() {
    }

    /**
     * 获取成功返回结果
     *
     * @param data
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Result success(T data) {
        Result result = new Result();
        result.setResultCode(ResultCodeEnum.SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    /**
     * 不用new result的方法
     *
     * @param data
     * @param result
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Result success(T data, Result result) {
        result.setResultCode(ResultCodeEnum.SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    /**
     * 获取成功返回结果
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Result success() {
        Result result = new Result();
        result.setResultCode(ResultCodeEnum.SUCCESS.getCode());
        return result;
    }

    /**
     * 获取失败返回结果
     *
     * @param failureCode
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Result fail(Integer failureCode) {
        Result result = new Result();
        result.setResultCode(500);
//        result.setResultCode(ResultCodeEnum.FAILURE.getCode());
        result.setFailureCode(failureCode);
        return result;
    }

    /**
     * 获取失败返回结果
     *
     * @param failureCode
     * @param failureMessage
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Result fail(Integer failureCode, String failureMessage) {
        Result result = new Result();
        result.setResultCode(500);
//        result.setResultCode(ResultCodeEnum.FAILURE.getCode());
        result.setFailureCode(failureCode);
        result.setFailureMessage(failureMessage);
        return result;
    }

    /**
     * 返回接口处理结果
     */
    public T getData() {
        return data;
    }

    /**
     * 返回失败编码
     */
    public Integer getFailureCode() {
        return failureCode;
    }

    /**
     * 返回失败原因
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureCode(Integer failureCode) {
        this.failureCode = failureCode;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result [resultCode=" + resultCode + ", failureCode="
                + failureCode + ", failureMessage=" + failureMessage
                + ", data=" + data + "]";
    }

}
