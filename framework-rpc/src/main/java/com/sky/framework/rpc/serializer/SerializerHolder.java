/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.rpc.serializer;

import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.common.spi.SpiExchange;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
public class SerializerHolder {

    private static SerializerHolder instance = new SerializerHolder();

    private ConcurrentHashMap<Byte, ObjectSerializer> serializerMap = new ConcurrentHashMap();

    private SerializerHolder() {
    }

    public static SerializerHolder getInstance() {
        return instance;
    }

    /**
     * 获取序列化
     *
     * @param serializerCode
     * @return
     */
    public ObjectSerializer getSerializer(Byte serializerCode) {
        ObjectSerializer objectSerializer = serializerMap.get(serializerCode);
        if (objectSerializer == null) {
            synchronized (this) {
                objectSerializer = serializerMap.get(serializerCode);
                if (objectSerializer == null) {
                    SerializeEnum serializeEnum = SerializeEnum.acquire(serializerCode);
                    objectSerializer = SpiExchange.getInstance().loadSpiSupport(ObjectSerializer.class, serializeEnum.getSerialize());
                    serializerMap.putIfAbsent(serializerCode, objectSerializer);
                }
            }
        }
        return objectSerializer;
    }
}
