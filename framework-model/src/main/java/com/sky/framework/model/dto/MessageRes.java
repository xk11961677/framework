package com.sky.framework.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.sky.framework.model.enums.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 外部接口响应结果定义
 *
 * @param <T> 成功时，返回业务数据
 * @author hutao
 */
@Data
@ApiModel(value = "通用返回结果")
public class MessageRes<T> {

    @ApiModelProperty("返回码")
    private Integer code;

    @ApiModelProperty("返回码描述")
    private String msg;

    @ApiModelProperty("业务数据")
    private T data;

    public MessageRes() {

    }

    public MessageRes(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MessageRes(Integer msgCode, String msgDesc, T data) {
        this.code = msgCode;
        this.msg = msgDesc;
        this.data = data;
    }

    public static MessageRes fail(Integer msgCode, String failureMessage) {
        return new MessageRes(msgCode, failureMessage);
    }

    public static <T> MessageRes success(Integer msgCode, String msgDesc, T data) {
        return new MessageRes(msgCode, msgDesc, data);
    }

    public static <T> MessageRes success(T data) {
        return new MessageRes(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), data);
    }

    @ApiModelProperty(value = "是否成功",hidden = true)
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return (this.code.intValue() == ResultCodeEnum.SUCCESS.getCode());
    }

}
