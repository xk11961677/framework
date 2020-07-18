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


import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * @author
 */
public final class FastJsonObject implements Iterable<Entry<String, Object>> {

    private final JSONObject jsonObject;

    public FastJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject get(String name) {
        return this.jsonObject.getJSONObject(name);
    }

    @Override
    public int hashCode() {
        return this.jsonObject.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return !(o instanceof FastJsonObject) ? false : Objects.equals(this.jsonObject, ((FastJsonObject) o).jsonObject);
        }
    }

    @Override
    public String toString() {
        return this.jsonObject.toString();
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        Set<Entry<String, Object>> entries = this.jsonObject.entrySet();
        return new FastJsonEntryIterator(entries.iterator());
    }

    private final class FastJsonEntryIterator implements Iterator<Entry<String, Object>> {
        private final Iterator<Entry<String, Object>> jsonIterator;

        private FastJsonEntryIterator(Iterator<Entry<String, Object>> jsonIterator) {
            this.jsonIterator = jsonIterator;
        }

        @Override
        public boolean hasNext() {
            return this.jsonIterator.hasNext();
        }

        @Override
        public Entry<String, Object> next() {
            Entry<String, Object> member = this.jsonIterator.next();
            return member;
        }
    }
}
