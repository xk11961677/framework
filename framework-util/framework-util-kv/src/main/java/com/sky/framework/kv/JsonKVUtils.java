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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    public static JSONObject convertPropertiesFromKV(List<? extends KeyValue> list) {
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
    public static <T> List<T> convertPropertiesToKV(JSONObject json, Class<T> tClass) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        JsonSerialization serialization = new JsonSerialization(json.toJSONString());
        return serialization.serialize(tClass);
    }


    /**
     * 将KV转成json object
     *
     * @param list
     * @return
     */
    private static String KV2JsonObject(List<? extends KeyValue> list) {
        ObjectNode object = new ObjectNode(new JsonNodeFactory(false));
        for (KeyValue propertyValue : list) {
            if (!StringUtils.isBlank(propertyValue.getKey())) {
                object.put(propertyValue.getKey(), propertyValue.getValue());
            }
        }
        return object.toString();
    }
}
