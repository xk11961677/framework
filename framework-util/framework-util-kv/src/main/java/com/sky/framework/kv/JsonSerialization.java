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

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;


/**
 * json转换KV
 *
 * @author
 */
@Slf4j
public final class JsonSerialization {

    private JsonNode source;

    /**
     * 转KV对象存储容器
     */
    private JsonifyArrayList kvObjects;

    private List kvList;

    private int setKeyIndex;

    private int setValueIndex;

    private MethodAccess access;

    private ConstructorAccess constructorAccess;

    /**
     * 每个json 的 kv iterator 存储容器
     */
    private final Deque<IndexedPeekIterator<?>> elementIters = new ArrayDeque<>();

    private CharSequenceTranslatorFactory policy = StringEscapePolicy.DEFAULT;

    /**
     * 对象组合符号
     */
    private Character separator = '.';

    /**
     * 数组符号
     */
    private String leftBracket = "[";
    /**
     * 数组符号
     */
    private String rightBracket = "]";

    /**
     * 创建
     *
     * @param json the JSON string
     */
    public JsonSerialization(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.source = mapper.readTree(json);
        } catch (Exception e) {
            log.error("read type of string content exception :{}", e.getMessage());
            this.kvObjects = newJsonifyArrayList();
        }
    }

    /**
     * 直接序列化成相应list kv对象
     *
     * @param kv
     * @return
     */
    public List serialization(Class kv) {
        constructorAccess = ConstructorAccess.get(kv);
        access = MethodAccess.get(kv);
        setKeyIndex = access.getIndex("setKey", String.class);
        setValueIndex = access.getIndex("setValue", String.class);
        kvList = new ArrayList();
        serialize();
        return kvList;
    }

    /**
     * 公共序列化代码
     */
    private void serialize() {
        reduce(source);
        while (!elementIters.isEmpty()) {
            IndexedPeekIterator<?> deepestIter = elementIters.getLast();
            if (!deepestIter.hasNext()) {
                elementIters.removeLast();
            } else if (deepestIter.peek() instanceof Entry) {
                Entry<String, JsonNode> mem = (Entry<String, JsonNode>) deepestIter.next();
                reduce(mem.getValue());
            } else {
                JsonNode val = (JsonNode) deepestIter.next();
                reduce(val);
            }
        }
    }

    /**
     * 处理jsonValue
     *
     * @param val
     */
    private void reduce(JsonNode val) {
        if (val.isObject() && asObject(val).iterator().hasNext()) {
            elementIters.add(IndexedPeekIterator.newIndexedPeekIterator(asObject(val)));
        } else if (val.isArray() && val.elements().hasNext()) {
            elementIters.add(IndexedPeekIterator.newIndexedPeekIterator(val));
        } else {
            //转换KV 格式
            String key = computeKey();
            Pair pair = jsonVal2Obj(val);
            if (pair.getKey().equals(DataTypeEnum.OBJECT_NULL.getKey())) {
                return;
            }
            if (kvList != null) {
                kvList.add(newObject(key, pair.getValue()));
            } else {
                kvObjects.add(newObject(key, pair.getValue()));
            }
        }
    }

    private Object newObject(String key, Object value) {
        Object obj = null;
        try {
            obj = constructorAccess.newInstance();
            access.invoke(obj, setKeyIndex, key);
            access.invoke(obj, setValueIndex, ObjectUtils.toString(value));
        } catch (Exception e) {
            log.error(":{}", e.getMessage());
        }
        return obj;
    }

    private JacksonJsonObject asObject(JsonNode val) {
        return new JacksonJsonObject(val);
    }

    /**
     * 将json value转成对象
     *
     * @param val
     * @return
     */
    private Pair<String, Object> jsonVal2Obj(JsonNode val) {
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
     * 拼装key
     *
     * @return
     */
    private String computeKey() {
        StringBuilder sb = new StringBuilder();
        for (IndexedPeekIterator<?> iter : elementIters) {
            if (iter.getCurrent() instanceof Entry) {
                String key = ((Entry<String, JsonNode>) iter.getCurrent()).getKey();
                if (StringUtils.isBlank(key)) {
                    throw new KVException("key不能为空");
                }
                if (sb.length() != 0) {
                    sb.append(separator);
                }
                sb.append(policy.getCharSequenceTranslator().translate(key));
            } else { // JsonValue
                sb.append(separator);
                sb.append(leftBracket + iter.getIndex() + rightBracket);
            }
        }
        return sb.toString();
    }

    /**
     * 创建数组容器
     *
     * @param <T>
     * @return
     */
    private <T> JsonifyArrayList<T> newJsonifyArrayList() {
        JsonifyArrayList<T> array = new JsonifyArrayList<>();
        array.setTranslator(policy.getCharSequenceTranslator());
        return array;
    }

    /**
     * 创建个MAP
     *
     * @param <K>
     * @param <V>
     * @return
     */
    private <K, V> JsonifyLinkedHashMap<K, V> newJsonifyLinkedHashMap() {
        JsonifyLinkedHashMap<K, V> map = new JsonifyLinkedHashMap<>();
        map.setTranslator(policy.getCharSequenceTranslator());
        return map;
    }

    @Override
    public int hashCode() {
        int result = 27;
        result = 31 * result + source.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonSerialization)) {
            return false;
        }
        return source.equals(((JsonSerialization) o).source);
    }

    @Override
    public String toString() {
        return "JsonSerialization{source=" + source + "}";
    }

}
