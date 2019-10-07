/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
