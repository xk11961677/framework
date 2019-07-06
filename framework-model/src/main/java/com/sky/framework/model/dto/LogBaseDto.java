package com.sky.framework.model.dto;

import com.sky.framework.model.util.LogLevel;
import com.sky.framework.model.util.LogType;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author
 *
 */
@Data
public class LogBaseDto {
    /**
     * pid
     */
    private static final String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    /**
     *
     */
    private static String hostName;
    /**
     *
     */
    private static String hostIp;


    /**
     * 应用名称
     */
    private String appName;

    /**
     * 日志时间
     */
    private String logTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    /**
     * 等级
     */
    private LogLevel level = LogLevel.Info;
    /**
     * 类型
     */
    private LogType type = LogType.Business;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 行号
     */
    private String num;
    /**
     * 标签
     */
    private String tag;
}
