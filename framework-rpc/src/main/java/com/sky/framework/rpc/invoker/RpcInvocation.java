package com.sky.framework.rpc.invoker;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author
 */
@Data
public class RpcInvocation implements Invocation, Serializable {

    private String clazzName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private Map<String, String> attachments;


    @Override
    public void setAttachment(String key, String value) {
        attachments.put(key, value);
    }

    @Override
    public void setAttachmentIfAbsent(String key, String value) {
        if (attachments.get(key) == null) {
            attachments.put(key, value);
        }
    }

    @Override
    public String getAttachment(String key) {
        return attachments.get(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        String value = attachments.get(key);
        return value == null ? defaultValue : value;
    }
}
