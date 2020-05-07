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

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.*;

import java.util.Collections;
import java.util.HashMap;

import static java.util.Collections.unmodifiableMap;

/**
 * JSON 字符串转移策略
 *
 * @author
 */
public enum StringEscapePolicy implements CharSequenceTranslatorFactory {

    /**
     * Escapes all JSON special characters and Unicode.
     */
    ALL(StringEscapeUtils.ESCAPE_JSON),

    /**
     * Escapes all JSON special characters and Unicode but slash('/').
     */
    ALL_BUT_SLASH(new AggregateTranslator(new LookupTranslator(
            unmodifiableMap(new HashMap<CharSequence, CharSequence>() {
                private static final long serialVersionUID = 1L;

                {//NOSONAR
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE),
            JavaUnicodeEscaper.outsideOf(32, 0x7f))),

    /**
     * Escapes all JSON special characters but Unicode.
     */
    ALL_BUT_UNICODE(new AggregateTranslator(new LookupTranslator(
            unmodifiableMap(new HashMap<CharSequence, CharSequence>() {
                private static final long serialVersionUID = 1L;

                {//NOSONAR
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                    put("/", "\\/");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE))),

    /**
     * Escapes all JSON special characters but slash('/') and Unicode.
     */
    ALL_BUT_SLASH_AND_UNICODE(new AggregateTranslator(new LookupTranslator(
            Collections.unmodifiableMap(new HashMap<CharSequence, CharSequence>() {
                private static final long serialVersionUID = 1L;

                {//NOSONAR
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE))),

    /**
     * Escapes all JSON special characters but slash('/') and Unicode.
     */
    DEFAULT(new AggregateTranslator(new LookupTranslator(
            unmodifiableMap(new HashMap<CharSequence, CharSequence>() {
                private static final long serialVersionUID = 1L;

                {//NOSONAR
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE)));

    private final CharSequenceTranslator translator;

    private StringEscapePolicy(CharSequenceTranslator translator) {
        this.translator = translator;
    }

    @Override
    public CharSequenceTranslator getCharSequenceTranslator() {
        return translator;
    }

}
