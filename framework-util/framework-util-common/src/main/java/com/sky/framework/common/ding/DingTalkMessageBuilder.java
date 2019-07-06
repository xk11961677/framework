package com.sky.framework.common.ding;


import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */
public class DingTalkMessageBuilder {
    private final HashMap<String, Object> map;

    public DingTalkMessageBuilder() {
        map = new HashMap<>();
    }

    public DingTalkMessageBuilder markdownMessage(String title, String text) {
        map.put("msgtype", "markdown");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("title", title);
        contentMap.put("text", text);
        map.put("markdown", contentMap);
        return this;
    }

    public DingTalkMessageBuilder at(ArrayList<String> phones,boolean isAtAll) {
        Map<String, Object> at = new HashMap<>();
        at.put("atMobiles", phones);
        at.put("isAtAll", isAtAll);
        map.put("at", at);
        return this;
    }

    String build() {
        return JSON.toJSONString(map);
    }
}
