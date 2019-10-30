## 自定义logback appender
将日志封装统一格式,并发送到MQ

## 发送MQ或其它处理方式请自行实现 //TODO


## 使用方式
```
1. 项目增加logback配置文件增加appender (详情请查看test)

<appender name="logCustomAppender" class="com.sky.framework.logback.appender.LogCustomAppender">
    <!-- 项目配置 -->
    <appName>framework-integrate-logback</appName>
    <!--必选项 (end)-->

    <filter class="ch.qos.logback.classic.filter.ThresholdFilter"><!-- 打印WARN,ERROR级别的日志 -->
        <level>WARN</level>
    </filter>
    
    <!-- 可选项 -->
    <mdcFields>THREAD_ID,MDC_KEY</mdcFields>
</appender>


```
