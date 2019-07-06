package com.sky.framework.model.dto;

import lombok.Data;

/**
 * @author
 * 参数、返回值、错误等请放入 message
 */
@Data
public class LogSimpleDto extends LogBaseDto {

    /**
     * 全类名
     */
    private String fullClazz;

    /**
     * 方法名
     */
    private String method;

    /**
     * url
     */
    private String url;

    /**
     * remoteIp
     */
    private String remoteIp;
}
