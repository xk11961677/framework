package com.sky.framework.rpc.remoting;

import com.sky.framework.rpc.remoting.protocol.PayloadHolder;
import lombok.Data;

/**
 * @author
 */
@Data
public class Response extends PayloadHolder {

    private long timestamp;

    private String version;

    private byte status;

    // 用于映射 <id, request, response> 三元组
    private final long id; // request.invokeId

    public byte status() {
        return status;
    }

    public long id() {
        return id;
    }

    public void status(byte status) {
        this.status = status;
    }

}
