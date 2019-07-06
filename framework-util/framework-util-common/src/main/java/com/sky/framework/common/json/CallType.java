package com.sky.framework.common.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 自定义类型
 * @author
 */
public class CallType {

    private Type[] types;

    private Class rawType;

    public CallType(Class rawType, Type[] types) {
        this.types = types;
        this.rawType = rawType;
    }

    public Type getType() {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return types;
            }

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
