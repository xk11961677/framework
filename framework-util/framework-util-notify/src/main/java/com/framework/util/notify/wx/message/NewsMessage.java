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
package com.framework.util.notify.wx.message;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图文消息
 *
 * @author
 */
public class NewsMessage implements Message {
    public static final int MAX_ARTICLE_CNT = 5;
    public static final int MIN_ARTICLE_CNT = 1;

    private List<NewsArticle> articles = new ArrayList<>();

    public void addNewsArticle(NewsArticle newsArticle) {
        if (articles.size() >= MAX_ARTICLE_CNT) {
            throw new IllegalArgumentException("number of articles can't more than " + MAX_ARTICLE_CNT);
        }
        articles.add(newsArticle);
    }

    @Override
    public String toJsonString() {
        Map<String, Object> items = new HashMap<>();
        items.put("msgtype", "news");

        Map<String, Object> news = new HashMap<>();
        if (articles.size() < MIN_ARTICLE_CNT) {
            throw new IllegalArgumentException("number of articles can't less than " + MIN_ARTICLE_CNT);
        }
        news.put("articles", articles);
        items.put("news", news);
        return JSON.toJSONString(items);
    }
}
