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
package com.sky.framework.common.arithmetic;

import java.util.LinkedHashMap;

/**
 * 非线程安全的，
 * 支持lru 算法的map，此map 构造时设置map存储的最大值
 * 当超过此值时，执行lru算法
 */
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1779766949449438092L;

    private static final int INITIAL_CAPACITY = 16;

    private static final float LOAD_FACTOR = 0.75f;

    /**
     * 固定map 大小
     */
    private int sizeCount;

    /**
     * 执行map 的默认构造
     */
    public LruHashMap(int sizeCount) {
        super(INITIAL_CAPACITY, LOAD_FACTOR, true);
        this.sizeCount = sizeCount;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        if (super.size() <= this.sizeCount) {
            return super.removeEldestEntry(eldest);
        } else {
            super.remove(eldest.getKey());
            return true;
        }
    }

}
