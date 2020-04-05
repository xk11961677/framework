/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.sky.framework.model.enums.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 外部接口响应结果定义
 *
 * @param <T> 成功时，返回业务数据
 * @author hutao
 */
@Data
@ApiModel(value = "通用返回结果")
public class MessageRes<T> implements Serializable {

    private static final long serialVersionUID = -6788609721062977943L;

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

    public MessageRes(Integer code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }

    public static MessageRes fail(Integer msgCode, String failureMessage) {
        return new MessageRes(msgCode, failureMessage);
    }

    public static <T> MessageRes success(Integer msgCode, String msgDesc, T data) {
        return new MessageRes(msgCode, msgDesc, data);
    }

    public static <T> MessageRes success(T data) {
        return success(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), data);
    }

    public static <T> MessageRes success() {
        return success(null);
    }

    @ApiModelProperty(value = "是否成功", hidden = true)
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return (this.code.intValue() == ResultCodeEnum.SUCCESS.getCode().intValue());
    }

}
