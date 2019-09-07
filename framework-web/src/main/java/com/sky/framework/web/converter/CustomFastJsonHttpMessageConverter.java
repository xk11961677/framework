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
package com.sky.framework.web.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.model.exception.BusinessException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * 当实体入参不符合json格式时抛出自定义 参数格式错误 异常
 *
 * @author
 */
public class CustomFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        FastJsonConfig fastJsonConfig = super.getFastJsonConfig();
        InputStream in = inputMessage.getBody();
        Object result;
        try {
            result = JSON.parseObject(in, fastJsonConfig.getCharset(), type, fastJsonConfig.getFeatures());
        } catch (Exception e) {
            BusinessException businessException = new BusinessException(FailureCodeEnum.GL990006.getMsg(), e);
            businessException.setCode(FailureCodeEnum.GL990006.getCode());
            throw businessException;
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return result;
    }
}
