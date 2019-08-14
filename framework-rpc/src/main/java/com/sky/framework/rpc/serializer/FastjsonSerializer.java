package com.sky.framework.rpc.serializer;

import com.alibaba.fastjson.JSON;
import com.sky.framework.rpc.common.enums.SerializeEnum;

/**
 * @author
 */
public class FastjsonSerializer implements ObjectSerializer {

    @Override
    public byte[] serialize(Object obj) throws RuntimeException {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) throws RuntimeException {
        return JSON.parseObject(param, clazz);
    }

    @Override
    public String getScheme() {
        return SerializeEnum.FASTJSON.getSerialize();
    }
}
