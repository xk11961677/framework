package com.sky.framework.model.util;

/**
 * @author
 */
@SuppressWarnings("unused")
public enum LogType {
    Business(0),

    Network(1),

    DB(2),

    Frame(3),

    MQ(4),

    Cache(5);

    private int intValue;

    private LogType(int value) {
        this.intValue = value;
    }

    public int getValue() {
        return this.intValue;
    }

    @Override
    public String toString() {
        return String.valueOf(this.intValue);
    }
}