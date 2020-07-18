/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
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
package com.sky.framework.kv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eclipsesource.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * KV 转换与还原工具类
 *
 * @author
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonKVUtils {

    /**
     * 将KV List 转成 JSONObject
     *
     * @param list
     * @return
     */
    @Deprecated
    public static JSONObject convertPropertiesFromKV(List<? extends KeyValue> list) {
        return deserialize(list);
    }

    /**
     * 反序列化成JSONObject
     *
     * @param list
     * @return
     */
    public static JSONObject deserialize(List<? extends KeyValue> list) {
        if (list == null || list.size() == 0) {
            return new JSONObject();
        }
        JsonDeserialization deserialization = new JsonDeserialization(KV2JsonObject(list));
        return JSON.parseObject(deserialization.deserialize());
    }

    /**
     * 将JSONObject 转成 KV List
     *
     * @param json
     * @return
     */
    @Deprecated
    public static <T> List<T> convertPropertiesToKV(JSONObject json, Class<T> clazz) {
        return serialize(json, clazz, KVModeEnum.JACKSON);
    }

    /**
     * @param json
     * @return
     */
    public static List<KeyValue> serialize(JSONObject json) {
        return serialize(json, KVModeEnum.JACKSON);
    }

    /**
     * @param json
     * @param clazz
     * @return
     */
    public static <T> List<T> serialize(JSONObject json, Class<T> clazz) {
        return serialize(json, clazz, KVModeEnum.JACKSON);
    }

    /**
     * @param json
     * @param mode
     * @return
     */
    public static List<KeyValue> serialize(JSONObject json, KVModeEnum mode) {
        return serialize(json, KeyValue.class, mode);
    }

    /**
     * 序列化成List<K,V>
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> serialize(JSONObject json, Class<T> clazz, KVModeEnum mode) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return get(mode, json.toJSONString()).serialize(clazz);
    }

    /**
     * 获取序列化对象
     *
     * @param mode
     * @param json
     * @return
     */
    private static BaseJsonSerialization get(KVModeEnum mode, String json) {
        return (mode == KVModeEnum.FASTJSON) ? new FastJsonSerialization(json) : new JacksonSerialization(json);

    }

    /**
     * 将KV转成json object
     *
     * @param list
     * @return
     */
    private static String KV2JsonObject(List<? extends KeyValue> list) {
        JsonObject object = new JsonObject();
        for (KeyValue propertyValue : list) {
            if (!StringUtils.isBlank(propertyValue.getKey())) {
                object.add(propertyValue.getKey(), propertyValue.getValue());
            }
        }
        return object.toString();
    }
}
