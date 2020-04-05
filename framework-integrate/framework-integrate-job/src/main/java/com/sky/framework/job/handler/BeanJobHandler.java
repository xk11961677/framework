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
package com.sky.framework.job.handler;

import com.alibaba.fastjson.JSON;
import com.sky.framework.job.util.LogComposeUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <T>
 * @author
 */
@SuppressWarnings("all")
@Slf4j
public abstract class BeanJobHandler<T> extends AbstractJobHandler {

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        String result = "";
        try {
            LogComposeUtils.log(log, "skycloud job begin:{} param:{}" + param);
            Object exec = this.exec(param);
            if (exec != null) {
                result = JSON.toJSONString(exec);
            }
        } catch (Exception e) {
            XxlJobLogger.log(e);
            FAIL.setContent(result);
            return FAIL;
        } finally {
            long end = System.currentTimeMillis();
            LogComposeUtils.log(log, "skycloud job end:{} cost time:{}" + (end - begin) + "ms");
        }
        SUCCESS.setContent(result);
        return SUCCESS;
    }
}
