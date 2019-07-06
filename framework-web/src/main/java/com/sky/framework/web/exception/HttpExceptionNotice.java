package com.sky.framework.web.exception;

import lombok.Data;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * http 请求异常
 *
 * @author
 */
@Data
@SuppressWarnings("all")
public class HttpExceptionNotice extends ExceptionNotice {

    protected String url;

    protected Map<String, String[]> paramInfo;

    protected String requestBody;


    public HttpExceptionNotice(Exception exception, String filter, String url, Map<String, String[]> param,
                               String requestBody) {
        super(exception, filter, null);
        this.url = url;
        this.paramInfo = param;
        this.requestBody = requestBody;
    }

    @Override
    public String createText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("工程信息：").append(project).append("\n\n");
        stringBuilder.append("接口地址：").append(url).append("\n\n");
        if (paramInfo != null && paramInfo.size() > 0) {
            stringBuilder.append("接口参数：").append("\n\n")
                    .append(String.join("", paramInfo.entrySet().stream()
                            .map(x -> String.format("%s:%s", x.getKey(), Arrays.toString(x.getValue()))).collect(toList())))
                    .append("\n\n");
        }
        if (requestBody != null) {
            stringBuilder.append("请求体数据：").append(requestBody).append("\n\n");
        }
        stringBuilder.append("类路径：").append(classPath).append("\n\n");
        stringBuilder.append("方法名：").append(methodName).append("\n\n");
        if (parames != null && parames.size() > 0) {
            stringBuilder.append("参数信息：")
                    .append(String.join(",", parames.stream().map(x -> x.toString()).collect(toList()))).append("\n\n");
        }
        stringBuilder.append("异常信息：").append(exceptionMessage).append("\n\n");
        stringBuilder.append("异常追踪：").append("\n\n").append(String.join("\n", traceInfo)).append("\n\n");
        return stringBuilder.toString();
    }
}