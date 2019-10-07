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
package com.sky.framework.common.encrypt;

import com.alibaba.fastjson.JSON;
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.MessageReq;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;


/**
 * @author
 */
@Slf4j
public class DefaultMd5Verifier implements Verifier {

    private static final DefaultEncrypter DEFAULT_ENCRYPTER = new DefaultEncrypter();

    @Override
    public boolean verify(MessageReq request, String secret) {
        boolean isSame = false;
        try {
            String sign = request.getSign();
            Map<String, ?> map = JSON.parseObject(JSON.toJSONString(request), Map.class);
            map.remove("sign");
            String signature = buildSignature(map, secret);
            LogUtils.info(log, "server signature :{}", signature);
            isSame = sign.equals(signature);
        } catch (Exception e) {
            LogUtils.error(log, "DefaultMd5Verifier verify exception :{}", e);
        }
        return isSame;
    }

    /**
     * 构建签名
     *
     * @param paramsMap
     * @param secret
     * @return
     * @throws IOException
     */
    public String buildSignature(Map<String, ?> paramsMap, String secret) throws IOException {
        Set<String> keySet = paramsMap.keySet();
        List<String> paramNames = new ArrayList<String>(keySet);

        Collections.sort(paramNames);

        StringBuilder paramNameValue = new StringBuilder();

        for (String paramName : paramNames) {
            paramNameValue.append(paramName).append(paramsMap.get(paramName));
        }

        String source = secret + paramNameValue.toString() + secret;
        LogUtils.info(log, "server signature before :{} " + source);
        return encrypt(source);
    }

    /**
     * 生成md5,全部大写。
     *
     * @param source
     * @return 返回MD5全部大写
     */
    public String encrypt(String source) {
        return DEFAULT_ENCRYPTER.md5(source).toUpperCase();
    }

}
