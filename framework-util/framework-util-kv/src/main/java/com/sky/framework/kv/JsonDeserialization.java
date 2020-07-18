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


import com.eclipsesource.json.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author
 */
@Slf4j
public final class JsonDeserialization {

    private final JsonValue root;

    private Character separator = '.';

    /**
     * 创建反序列化对象
     *
     * @param json the JSON string
     */
    public JsonDeserialization(String json) {
        root = Json.parse(json);
    }

    /**
     * 反序列化
     */
    @SuppressWarnings("squid:S3776")
    public String deserialize() {
        StringWriter sw = new StringWriter();
        if (root.isArray()) {
            try {
                deserializationArray(root.asArray()).writeTo(sw, WriterConfig.MINIMAL);
            } catch (IOException e) {
                log.warn("deserializationArray exception:{}", e.getMessage(), e);
            }
            return sw.toString();
        }
        //不是数组,也不是object , 直接toString
        if (!root.isObject()) {
            return root.toString();
        }
        //序列化后的
        JsonObject serialization = root.asObject();
        //反序列化的
        JsonValue deserialization = Json.object();

        //for循环处理当前key value
        for (String key : serialization.names()) {
            //临时记录，已序列化value
            JsonValue currentVal = deserialization;
            //key
            String objKey = null;
            //在数组中的对应下标
            Integer aryIdx = null;
            //判断现有json格式，是否是通过 separator 拼装
            Matcher matcher = keyPartPattern().matcher(key);
            while (matcher.find()) {
                String keyPart = matcher.group();
                if (objKey != null ^ aryIdx != null) {
                    //通过KEY判断原来json格式是否为数组
                    if (isJsonArray(keyPart)) {
                        currentVal = findOrCreateJsonArray(currentVal, objKey, aryIdx);
                        objKey = null;
                        aryIdx = Integer.valueOf(replace(keyPart));
                    } else {
                        currentVal = findOrCreateJsonObject(currentVal, objKey, aryIdx);
                        objKey = keyPart;
                        aryIdx = null;
                    }
                }
                if (objKey == null && aryIdx == null) {
                    if (isJsonArray(keyPart)) {
                        aryIdx = Integer.valueOf(replace(keyPart));
                        if (currentVal == null) {
                            currentVal = Json.array();
                        }
                    } else {
                        objKey = keyPart;
                        if (currentVal == null) {
                            currentVal = Json.object();
                        }
                    }
                }
                if (deserialization == null) {
                    deserialization = currentVal;
                }
            }
            setDeserializationValue(serialization, key, currentVal, objKey, aryIdx);
        }
        try {
            deserialization.writeTo(sw, WriterConfig.MINIMAL);
        } catch (Exception e) {
            log.warn("deserialize exception:{}", e.getMessage(), e);
        }
        return sw.toString();
    }

    /**
     * 反序列化数组
     *
     * @param array
     * @return
     */
    //NOSONAR
    private JsonArray deserializationArray(JsonArray array) {
        JsonArray deserializationArray = Json.array().asArray();
        for (JsonValue value : array) {
            if (value.isArray()) {
                deserializationArray.add(deserializationArray(value.asArray()));
            } else if (value.isObject()) {
                deserializationArray.add(Json.parse(new JsonDeserialization(value.toString()).deserialize()));
            } else {
                deserializationArray.add(value);
            }
        }
        return deserializationArray;
    }

    /**
     * 匹配key , 按 . 拆分
     *
     * @return
     */
    private Pattern keyPartPattern() {
        return Pattern.compile("[^" + Pattern.quote(separator.toString()) + "]+");
    }

    /**
     * 判断是否为JsonArray
     *
     * @param keyPart
     * @return
     */
    private boolean isJsonArray(String keyPart) {
        return keyPart.matches("^\\[\\d*\\]$");
    }

    /**
     * 替换keyPart [ 和 ] 符号
     *
     * @param keyPart
     * @return
     */
    private String replace(String keyPart) {
        return keyPart.replace("[", "").replace("]", "");
    }

    /**
     * 查找或创建jsonArray
     * 对应key 没有创建数组时，需自动创建,否则查找对应数组添加值
     *
     * @param currentVal
     * @param objKey
     * @param aryIdx
     * @return
     */
    private JsonValue findOrCreateJsonArray(JsonValue currentVal, String objKey,
                                            Integer aryIdx) {
        if (objKey != null) {
            JsonObject jsonObj = currentVal.asObject();
            if (jsonObj.get(objKey) == null) {
                JsonValue ary = Json.array();
                jsonObj.add(objKey, ary);
                return ary;
            }
            return jsonObj.get(objKey);
        } else {
            JsonArray jsonAry = currentVal.asArray();
            if (jsonAry.size() <= aryIdx || jsonAry.get(aryIdx).equals(Json.NULL)) {
                JsonValue ary = Json.array();
                assureJsonArraySize(jsonAry, aryIdx);
                jsonAry.set(aryIdx, ary);
                return ary;
            }
            return jsonAry.get(aryIdx);
        }
    }

    /**
     * 查找或创建jsonObject
     *
     * @param currentVal
     * @param objKey
     * @param aryIdx
     * @return
     */
    private JsonValue findOrCreateJsonObject(JsonValue currentVal, String objKey,
                                             Integer aryIdx) {
        if (objKey != null) {
            JsonObject jsonObj = currentVal.asObject();
            if (jsonObj.get(objKey) == null) {
                JsonValue obj = Json.object();
                jsonObj.add(objKey, obj);
                return obj;
            }
            return jsonObj.get(objKey);
        } else {
            JsonArray jsonAry = currentVal.asArray();
            if (jsonAry.size() <= aryIdx || jsonAry.get(aryIdx).equals(Json.NULL)) {
                JsonValue obj = Json.object();
                assureJsonArraySize(jsonAry, aryIdx);
                jsonAry.set(aryIdx, obj);
                return obj;
            }
            return jsonAry.get(aryIdx);
        }
    }

    /**
     * 设置当前正在反序列化的值
     *
     * @param jsonObject
     * @param key
     * @param currentVal
     * @param objKey
     * @param aryIdx
     */
    private void setDeserializationValue(JsonObject jsonObject, String key, JsonValue currentVal, String objKey, Integer aryIdx) {
        JsonValue val = jsonObject.get(key);
        if (objKey != null) {
            if (val.isArray()) {
                JsonValue jsonArray = Json.array();
                for (JsonValue arrayVal : val.asArray()) {
                    jsonArray.asArray().add(Json.parse(newJsonDeserialization(arrayVal.toString()).deserialize()));
                }
                currentVal.asObject().add(objKey, jsonArray);
            } else {
                currentVal.asObject().add(objKey, val);
            }
        } else {
            assureJsonArraySize(currentVal.asArray(), aryIdx);
            currentVal.asArray().set(aryIdx, val);
        }
    }

    /**
     * 创建JsonDeserialization
     *
     * @param json
     * @return
     */
    private JsonDeserialization newJsonDeserialization(String json) {
        JsonDeserialization js = new JsonDeserialization(json);
        return js;
    }

    /**
     * 确定数组保证有值,可以为 null 对象
     *
     * @param jsonArray
     * @param index
     */
    private void assureJsonArraySize(JsonArray jsonArray, Integer index) {
        while (index >= jsonArray.size()) {
            jsonArray.add(Json.NULL);
        }
    }

    @Override
    public int hashCode() {
        int result = 27;
        result = 31 * result + root.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonDeserialization)) {
            return false;
        }
        return root.equals(((JsonDeserialization) o).root);
    }

    @Override
    public String toString() {
        return "JsonDeserialization{root=" + root + "}";
    }
}
