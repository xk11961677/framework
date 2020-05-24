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
package com.sky.framework.notify.dd;


import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */
public class DingTalkBuilder {
    /**
     * 参数可自定义access_token
     */
    private String accessToken;

    private final HashMap<String, Object> map;

    public DingTalkBuilder(String token) {
        this.accessToken = token;
        map = new HashMap<>();
    }

    public DingTalkBuilder() {
        map = new HashMap<>();
    }

    public DingTalkBuilder markdownMessage(String title, String text) {
        map.put("msgtype", "markdown");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("title", title);
        contentMap.put("text", text);
        map.put("markdown", contentMap);
        return this;
    }

    public DingTalkBuilder at(ArrayList<String> phones, boolean isAtAll) {
        Map<String, Object> at = new HashMap<>();
        at.put("atMobiles", phones);
        at.put("isAtAll", isAtAll);
        map.put("at", at);
        return this;
    }

    String build() {
        return JSON.toJSONString(map);
    }

    public String getAccessToken() {
        return accessToken;
    }
}
