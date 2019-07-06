package com.sky.framework.rocketmq.exception;

/**
 * @author
 */
public class MqException extends RuntimeException {


    public MqException() {
    }

    public MqException(Throwable cause) {
        super(cause);
    }

    public MqException(String message) {
        super(message);
    }

    public MqException(String message, Throwable cause) {
        super(message, cause);
    }
}