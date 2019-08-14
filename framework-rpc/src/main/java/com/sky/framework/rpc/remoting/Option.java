package com.sky.framework.rpc.remoting;

import lombok.Data;

/**
 * @author
 * @param <T>
 */
@Data
public class Option<T> {

    private String key;

    private T value;

}
