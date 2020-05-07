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
package com.framework.util.kv;

import org.apache.commons.text.translate.CharSequenceTranslator;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * 重写LinkedHashMap toString 输出成json格式
 *
 * @author
 */
public class JsonifyLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;

    private transient CharSequenceTranslator translator = StringEscapePolicy.DEFAULT.getCharSequenceTranslator();

    public void setTranslator(CharSequenceTranslator translator) {
        this.translator = translator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (Entry<K, V> mem : entrySet()) {
            sb.append('"');
            sb.append(mem.getKey());
            sb.append('"');
            sb.append(':');
            if (mem.getValue() instanceof String) {
                sb.append('"');
                sb.append(translator.translate((String) mem.getValue()));
                sb.append('"');
            } else {
                sb.append(mem.getValue());
            }
            sb.append(',');
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }
        sb.append('}');

        return sb.toString();
    }

}
