package com.sky.framework.job.util;

import com.sky.framework.common.LogUtil;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;

/**
 * 组合日志输出
 *
 * @author
 */
public class LogComposeUtil {

    public static void log(Logger log, String info,Object... args) {
        XxlJobLogger.log(info,args);
        LogUtil.info(log, info,args);
    }
}
