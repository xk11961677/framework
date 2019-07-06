package com.sky.framework.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author
 */
@ConfigurationProperties(prefix = "skycloud.job")
@Data
public class JobProperties {

    private Admin admin = new Admin();

    private String accessToken;

    private Executor executor = new Executor();

    @Data
    public class Executor {

        private String appName;

        private String ip;

        private int port;

        private String logPath;

        private int logRetentionDays;


    }

    @Data
    public class Admin {
        private String addresses;
    }
}
