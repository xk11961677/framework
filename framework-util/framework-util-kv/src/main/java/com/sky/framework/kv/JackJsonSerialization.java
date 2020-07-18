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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;


/**
 * json转换KV
 *
 * @author
 */
@Slf4j
public final class JackJsonSerialization extends BaseJsonSerialization<JsonNode> {
    /**
     * 创建
     *
     * @param json the JSON string
     */
    public JackJsonSerialization(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.source = mapper.readTree(json);
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
    protected void reduce(JsonNode val) {
        JacksonJsonObject jsonObject;
        if (val.isObject() && (jsonObject = asObject(val)).iterator().hasNext()) {
            deque.add(IndexedPeekIterator.newIndexedPeekIterator(jsonObject));
        } else if (val.isArray() && val.elements().hasNext()) {
            deque.add(IndexedPeekIterator.newIndexedPeekIterator(val));
        } else {
            //转换KV 格式
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
    protected Pair<String, Object> jsonVal2Obj(JsonNode val) {
        if (val.isTextual()) {
            return Pair.of(DataTypeEnum.TEXT.getKey(), val.textValue());
        } else if (val.isNumber()) {
            return Pair.of(DataTypeEnum.NUMBER.getKey(), new BigDecimal(val.toString()));
        } else if (val.isArray()) {
            return Pair.of(DataTypeEnum.ARRAY.getKey(), newJsonifyArrayList());
        } else if (val.isObject()) {
            ObjectNode node = (ObjectNode) val;
            String key = (node.isEmpty(null) || node.isNull()) ? DataTypeEnum.OBJECT_NULL.getKey() : DataTypeEnum.OBJECT.getKey();
            return Pair.of(key, newJsonifyLinkedHashMap());
        }
        log.error("jsonVal2Obj [" + DataTypeEnum.UNKNOWN.getKey() + "]");
        return Pair.of(DataTypeEnum.UNKNOWN.getKey(), null);
    }

    /**
     * JacksonJsonObject
     *
     * @param val
     * @return
     */
    private JacksonJsonObject asObject(JsonNode val) {
        return new JacksonJsonObject(val);
    }

}
