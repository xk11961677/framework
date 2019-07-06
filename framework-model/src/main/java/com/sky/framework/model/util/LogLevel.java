package com.sky.framework.model.util;

/**
 * @author
 */
public enum LogLevel {
    Fatal(0),

    Error(1),

    Warn(2),

    Info(3),

    Debug(4),

    Trace(5);

    private int intValue;

    private LogLevel(int value) {
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
