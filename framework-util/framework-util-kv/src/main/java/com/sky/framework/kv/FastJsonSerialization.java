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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;


/**
 * json转换KV
 *
 * @author
 */
@Slf4j
public final class FastJsonSerialization extends BaseJsonSerialization<Object> {

    /**
     * 创建
     *
     * @param json the JSON string
     */
    public FastJsonSerialization(String json) {
        try {
            this.source = JSONObject.parseObject(json);
        } catch (Exception e) {
            throw new KVException("( " + json + " ) is not json string ", e);
        }
    }

    /**
     * 处理jsonValue
     *
     * @param val
     */
    @Override
    protected void reduce(Object val) {
        FastJsonObject jsonObject;
        JSONArray jsonArray;
        if (val instanceof JSONObject && (jsonObject = asObject(val)).iterator().hasNext()) {
            deque.add(IndexedPeekIterator.newIndexedPeekIterator(jsonObject));
        } else if (val instanceof JSONArray && (jsonArray = (JSONArray) val).iterator().hasNext()) {
            deque.add(IndexedPeekIterator.newIndexedPeekIterator(jsonArray));
        } else {
            addObject(val);
        }
    }

    /**
     * 将json value转成对象
     *
     * @param val
     * @return
     */
    @Override
    protected Pair<String, Object> jsonVal2Obj(Object val) {
        if (val instanceof String) {
            return Pair.of(DataTypeEnum.TEXT.getKey(), val.toString());
        } else if (val instanceof Number) {
            return Pair.of(DataTypeEnum.NUMBER.getKey(), new BigDecimal(val.toString()));
        } else if (val instanceof JSONArray) {
            return Pair.of(DataTypeEnum.ARRAY.getKey(), newJsonifyArrayList());
        } else if (val instanceof JSONObject) {
            if (val == null || ((JSONObject) val).isEmpty()) {
                return Pair.of(DataTypeEnum.OBJECT_NULL.getKey(), null);
            }
            return Pair.of(DataTypeEnum.OBJECT.getKey(), newJsonifyLinkedHashMap());
        }
        log.error("jsonVal2Obj [" + DataTypeEnum.UNKNOWN.getKey() + "]");
        return Pair.of(DataTypeEnum.UNKNOWN.getKey(), null);
    }

    /**
     * FastJsonObject
     *
     * @param val
     * @return
     */
    private FastJsonObject asObject(Object val) {
        return new FastJsonObject((JSONObject) val);
    }

}
