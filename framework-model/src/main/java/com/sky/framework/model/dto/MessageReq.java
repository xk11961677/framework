package com.sky.framework.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 外部接口请求参数定义
 *
 * @param <T> 业务请求参数
 * @author
 */
@Data
public class MessageReq<T> {

    @NotBlank(message = "clientId不能为空")
    @ApiModelProperty(value = "客户端ID，服务端分配", required = true)
    private String clientId;

    @NotNull(message = "timestamp不能为空")
    @ApiModelProperty(value = "当前时间戳", required = true)
    private String timestamp;

    @NotBlank(message = "sign不能为空")
    @ApiModelProperty(value = "业务参数签名", required = true)
    private String sign;

    @NotNull(message = "param不能为空")
    @Valid
    @ApiModelProperty(value = "业务参数", required = true)
    private T param;

    public MessageReq() {
    }

}
