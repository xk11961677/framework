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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


/**
 * json转换KV
 *
 * @author
 */
@Slf4j
public abstract class BaseJsonSerialization<JV> {
    /**
     * 源数据
     */
    protected JV source;
    /**
     * 返回结果容器
     */
    protected List kvList;
    /**
     * 反射key index
     */
    protected int setKeyIndex;
    /**
     * 反射value index
     */
    protected int setValueIndex;
    /**
     * 反射KV对象的方法
     */
    protected MethodAccess access;
    /**
     * 反射KV对象的构造方法
     */
    protected ConstructorAccess constructorAccess;
    /**
     * 每个json 的 kv iterator 存储容器
     */
    protected final Deque<IndexedPeekIterator<?>> deque = new ArrayDeque<>();

    /**
     * 对象组合符号
     */
    protected static final Character separator = '.';
    /**
     * 数组符号
     */
    protected static final String leftBracket = "[";
    /**
     * 数组符号
     */
    protected static final String rightBracket = "]";
    /**
     * 字符转换工厂
     */
    protected static final CharSequenceTranslatorFactory policy = StringEscapePolicy.DEFAULT;

    /**
     * 获取value
     *
     * @param val
     * @return
     */
    protected abstract Pair<String, Object> jsonVal2Obj(JV val);
    /**
     * 递归处理
     *
     * @param val
     */
    protected abstract void reduce(JV val);

    /**
     * 直接序列化成相应list kv对象
     *
     * @param clazz KV对象Class
     * @return
     */
    public List serialize(Class clazz) {
        before(clazz);
        serialize();
        after();
        return kvList;
    }

    /**
     * 前置准备
     *
     * @param clazz
     */
    protected void before(Class clazz) {
        constructorAccess = ConstructorAccess.get(clazz);
        access = MethodAccess.get(clazz);
        setKeyIndex = access.getIndex("setKey", String.class);
        setValueIndex = access.getIndex("setValue", String.class);
        kvList = new ArrayList();
    }

    /**
     * 后置回收
     */
    protected void after() {
        deque.clear();
        source = null;
    }

    /**
     * 执行序列化
     */
    protected void serialize() {
        reduce(source);
        while (!deque.isEmpty()) {
            IndexedPeekIterator<?> indexedPeekIterator = deque.getLast();
            if (!indexedPeekIterator.hasNext()) {
                deque.removeLast();
            } else if (indexedPeekIterator.peek() instanceof Map.Entry) {
                Map.Entry<String, JV> mem = (Map.Entry<String, JV>) indexedPeekIterator.next();
                reduce(mem.getValue());
            } else {
                reduce((JV) indexedPeekIterator.next());
            }
        }
    }

    /**
     * 将kv对象添加到容器
     *
     * @param val
     */
    protected void addObject(JV val) {
        Pair<String, Object> pair = jsonVal2Obj(val);
        if (pair.getKey().equals(DataTypeEnum.OBJECT_NULL.getKey())) {
            return;
        }
        String key = computeKey();
        kvList.add(newObject(key, pair.getValue()));
    }

    /**
     * 创建数组容器
     *
     * @param <T>
     * @return
     */
    protected <T> JsonifyArrayList<T> newJsonifyArrayList() {
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
    protected <K, V> JsonifyLinkedHashMap<K, V> newJsonifyLinkedHashMap() {
        JsonifyLinkedHashMap<K, V> map = new JsonifyLinkedHashMap<>();
        map.setTranslator(policy.getCharSequenceTranslator());
        return map;
    }

    /**
     * 转换kv对象
     *
     * @param key
     * @param value
     * @return
     */
    private Object newObject(String key, Object value) {
        Object obj = null;
        try {
            obj = constructorAccess.newInstance();
            access.invoke(obj, setKeyIndex, key);
            access.invoke(obj, setValueIndex, ObjectUtils.toString(value));
        } catch (Exception e) {
            log.error("serialized object exception:{}", e.getMessage(), e);
        }
        return obj;
    }

    /**
     * 计算key
     *
     * @return
     */
    private String computeKey() {
        StringBuilder sb = new StringBuilder();
        for (IndexedPeekIterator<?> iter : deque) {
            if (iter.getCurrent() instanceof Map.Entry) {
                String key = ((Map.Entry<String, JV>) iter.getCurrent()).getKey();
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
        if (!(o instanceof BaseJsonSerialization)) {
            return false;
        }
        return source.equals(((BaseJsonSerialization) o).source);
    }

    @Override
    public String toString() {
        return "BaseJsonSerialization{source=" + source + "}";
    }

}
