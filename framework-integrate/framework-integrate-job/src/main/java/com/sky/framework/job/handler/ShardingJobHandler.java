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
package com.sky.framework.job.handler;


import com.alibaba.fastjson.JSON;
import com.sky.framework.job.util.LogComposeUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 分片广播任务
 *
 * @author
 */
@SuppressWarnings("all")
@Slf4j
public abstract class ShardingJobHandler extends AbstractJobHandler {

    @Override
    public ReturnT<String> execute(String param) {
        long begin = System.currentTimeMillis();
        String result = "";
        try {
            ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
            LogComposeUtils.log(log, "skycloud job begin:{} param:{}" + param);
            LogComposeUtils.log(log, "分片参数：当前分片序号 = {}, 总分片数 = {}", shardingVO.getIndex(), shardingVO.getTotal());
            for (int i = 0; i < shardingVO.getTotal(); i++) {
                if (i == shardingVO.getIndex()) {
                    LogComposeUtils.log(log, "第 {} 片, 命中分片开始处理", i);
                    Object exec = this.exec(param);
                    if (exec != null) {
                        result = JSON.toJSONString(exec);
                    }
                } else {
                    LogComposeUtils.log(log, "第 {} 片, 忽略", i);
                }
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
