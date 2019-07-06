package com.sky.framework.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础类
 *
 * @author
 */
@Data
public class BaseDto implements Serializable {

    /**
     * 响应编码
     */
    private String resultCode;
    /**
     * 响应描述
     */
    private String resultInfo;
    /**
     * 错误编码 当（resultCode=500时，才有值）
     */
    private String failureCode;
    /**
     * 错误描述 当（resultCode=500时，才有值）
     */
    private String failureMessage;
    /**
     * 扩展字段
     */
    private String extParam;

    /**
     * 排序字段
     */
    private String orderBy;

}
