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
package com.sky.framework.common.xml;


import com.alibaba.fastjson.JSONObject;
import com.sky.framework.common.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author
 */
@Slf4j
public class XmlUtils {

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     *
     * @param strxml
     * @return
     * @throws Exception
     */
    public static Map<Object, Object> doXMLParse(String strxml) throws Exception {
        if (null == strxml || "".equals(strxml)) {
            return null;
        }
        Map<Object, Object> map;
        InputStream in = null;
        try {
            map = new HashMap<>();
            in = new ByteArrayInputStream(strxml.getBytes());
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List<Element> list = root.getChildren();
            Iterator<Element> it = list.iterator();
            while (it.hasNext()) {
                Element e = it.next();
                String k = e.getName();
                String v = "";
                List<Element> children = e.getChildren();
                if (children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = getChildrenText(children);
                }
                map.put(k, v);
            }
        } catch (JDOMException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            in.close();
        }
        return map;
    }

    /**
     * 获取子结点的xml
     *
     * @param children
     * @return String
     */
    public static String getChildrenText(List<Element> children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator<Element> it = children.iterator();
            while (it.hasNext()) {
                Element e = it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List<Element> list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }
        return sb.toString();
    }

    /**
     * 将xml字符串转换为JSON对象
     *
     * @param xml xml字符串
     * @return JSON对象
     */
    public static JSONObject xmlToJson(String xml) {
        try {
            JSONObject json = new JSONObject();
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(is);
            Element root = doc.getRootElement();
            json.put(root.getName(), iterateElement(root));
            return json;
        } catch (Exception e) {
            LogUtils.error(log, "xml2json exception:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param element
     * @return
     */
    private static Map iterateElement(Element element) {
        List node = element.getChildren();
        List list = null;
        Map map = new HashMap();
        for (int i = 0; i < node.size(); i++) {
            list = new LinkedList();
            Element element1 = (Element) node.get(i);
            if (element1.getTextTrim().equals("")) {
                if (element1.getChildren().size() == 0) {
                    continue;
                }
                if (map.containsKey(element1.getName())) {
                    list = (List) map.get(element1.getName());
                }
                list.add(iterateElement(element1));
                map.put(element1.getName(), list);
            } else {
                if (map.containsKey(element1.getName())) {
                    list = (List) map.get(element1.getName());
                }
                list.add(element1.getTextTrim());
                map.put(element1.getName(), list);
            }
        }
        return map;
    }
}
