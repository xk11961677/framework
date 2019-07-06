package com.sky.framework.job.handler;


import com.alibaba.fastjson.JSON;
import com.sky.framework.job.util.LogComposeUtil;
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
            LogComposeUtil.log(log, "skycloud job begin:{} param:{}" + param);
            LogComposeUtil.log(log, "分片参数：当前分片序号 = {}, 总分片数 = {}", shardingVO.getIndex(), shardingVO.getTotal());
            for (int i = 0; i < shardingVO.getTotal(); i++) {
                if (i == shardingVO.getIndex()) {
                    LogComposeUtil.log(log, "第 {} 片, 命中分片开始处理", i);
                    Object exec = this.exec(param);
                    if (exec != null) {
                        result = JSON.toJSONString(exec);
                    }
                } else {
                    LogComposeUtil.log(log, "第 {} 片, 忽略", i);
                }
            }
        } catch (Exception e) {
            XxlJobLogger.log(e);
            FAIL.setContent(result);
            return FAIL;
        } finally {
            long end = System.currentTimeMillis();
            LogComposeUtil.log(log, "skycloud job end:{} cost time:{}" + (end - begin) + "ms");
        }
        SUCCESS.setContent(result);
        return SUCCESS;
    }

}
