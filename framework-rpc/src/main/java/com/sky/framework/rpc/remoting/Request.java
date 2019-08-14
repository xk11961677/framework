package com.sky.framework.rpc.remoting;

import com.sky.framework.rpc.remoting.protocol.PayloadHolder;
import lombok.Data;

/**
 * @author
 */
@Data
public class Request extends PayloadHolder {

    private long timestamp;

    private String version;

    private Long id;

    public Request(long id) {
        this.id = id;
    }


    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
