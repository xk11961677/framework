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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 测试类
 */
@RunWith(JUnit4.class)
public class JsonKVUtilsTest {

    @Test
    public void testPropertiesKV() throws IOException {
        URL url = Resources.getResource("properties.json");
        String json = Resources.toString(url, Charsets.UTF_8);
        JSONObject jsonObject = JSONObject.parseObject(json);
        List<KeyValue> propertyValueList = JsonKVUtils.convertPropertiesToKV(jsonObject, KeyValue.class);
        Assert.assertNotNull(propertyValueList);
    }

    @Test
    public void testPropertiesFromKV() throws IOException {
        URL url = Resources.getResource("un_properties.json");
        String json = Resources.toString(url, Charsets.UTF_8);
        List<KeyValue> propertyValueList = JSONArray.parseArray(json, KeyValue.class);
        JSONObject value = JsonKVUtils.convertPropertiesFromKV(propertyValueList);
        Assert.assertNotNull(value);
    }
}
