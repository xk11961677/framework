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
package com.framework.util.notify;


import com.framework.util.notify.wx.WxSendUtils;
import com.baozun.tech.notify.wx.message.*;
import com.framework.util.notify.wx.message.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

@RunWith(JUnit4.class)
public class WxSendUtilsTest {

    private String webhook = "http://xxxx";

    @Test
    public void sendLinkMessage() {
        LinkMessage message = new LinkMessage();
        message.setText("123");
        message.setTitle("test");
        message.setMessageUrl("http://www.baidu.com");
        message.setPicUrl("http://www.baidu.com");
        WxSendUtils.send(webhook, message);
    }

    @Test
    public void sendMarkdownMessage() {
        MarkdownMessage message = new MarkdownMessage();
        message.add("test");
        message.add(MarkdownMessage.getHeaderText(1, "test"));
        message.add(MarkdownMessage.getBoldText("test"));
        message.add(MarkdownMessage.getImageText("http://xxxx"));
        message.add(MarkdownMessage.getItalicText("http://xxxx"));
        message.add(MarkdownMessage.getLinkText("http://xxxx", ""));
        message.add(MarkdownMessage.getReferenceText("http://xxxx"));
        message.add(MarkdownMessage.getOrderListText(new ArrayList<>()));
        message.add(MarkdownMessage.getUnorderListText(new ArrayList<>()));
        WxSendUtils.send(webhook, message);
    }

    @Test
    public void sendNewsArticle() {
        NewsMessage message = new NewsMessage();
        NewsArticle newsArticle = new NewsArticle();
        newsArticle.setDescription("123");
        newsArticle.setTitle("111");
        newsArticle.setUrl("http://xxx");
        newsArticle.setPicurl("http://xxx");
        message.addNewsArticle(newsArticle);
        WxSendUtils.send(webhook, message);
    }

    @Test
    public void sendTextMessage() {
        TextMessage message = new TextMessage("test");
        message.setAtAll(true);//@所有人
        WxSendUtils.send(webhook, message);
    }
}
