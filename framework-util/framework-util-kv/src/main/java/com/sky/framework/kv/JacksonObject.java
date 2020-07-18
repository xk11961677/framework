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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * @author
 */
public final class JacksonObject implements Iterable<Entry<String, JsonNode>> {

    private final JsonNode jsonObject;

    public JacksonObject(JsonNode jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonNode get(String name) {
        return this.jsonObject.get(name);
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
            return !(o instanceof JacksonObject) ? false : Objects.equals(this.jsonObject, ((JacksonObject) o).jsonObject);
        }
    }

    @Override
    public String toString() {
        return this.jsonObject.toString();
    }

    @Override
    public Iterator<Entry<String, JsonNode>> iterator() {
        return new JacksonJsonEntryIterator(this.jsonObject.fields());
    }

    private final class JacksonJsonEntryIterator implements Iterator<Entry<String, JsonNode>> {
        private final Iterator<Entry<String, JsonNode>> jsonNodeIterator;

        private JacksonJsonEntryIterator(Iterator<Entry<String, JsonNode>> jsonNodeIterator) {
            this.jsonNodeIterator = jsonNodeIterator;
        }

        @Override
        public boolean hasNext() {
            return this.jsonNodeIterator.hasNext();
        }

        @Override
        public Entry<String, JsonNode> next() {
            Entry<String, JsonNode> member = (Entry) this.jsonNodeIterator.next();
            return member;
        }
    }
}
