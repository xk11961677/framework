/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
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
package com.sky.framework.model.util;

import com.google.common.collect.Maps;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * 用户上下文 (不支持webflux)
 *
 * @author
 */
public class UserContextHolder {

    private ThreadLocal<Map<String, String>> threadLocal;

    private UserContextHolder() {
        this.threadLocal = new ThreadLocal<>();
    }

    /**
     * 创建实例
     *
     * @return
     */
    public static UserContextHolder getInstance() {
        return SingletonHolder.INSTANCE.getInstance();
    }

    /**
     * 枚举类线程安全,仅被装载一次,且不会被反射创建
     */
    private enum SingletonHolder {
        INSTANCE;
        private final UserContextHolder instance;

        SingletonHolder() {
            instance = new UserContextHolder();
        }

        private UserContextHolder getInstance() {
            return instance;
        }
    }

    /**
     * 用户上下文中放入信息
     *
     * @param map
     */
    public void setContext(Map<String, String> map) {
        threadLocal.set(map);
    }

    /**
     * 获取上下文中的信息
     *
     * @return
     */
    public Map<String, String> getContext() {
        return threadLocal.get();
    }

    /**
     * 获取上下文中的用户名
     *
     * @return
     */
    public String getUsername() {
        return Optional.ofNullable(threadLocal.get()).orElse(Maps.newHashMap()).get("user_name");
    }


    /**
     * 获取上下文中的用户id
     *
     * @return
     */
    public Long getUserId() {
        String userId = Optional.ofNullable(threadLocal.get()).orElse(Maps.newHashMap()).get("user_id");
        if (!StringUtils.isEmpty(userId)) {
            return Long.parseLong(userId);
        }
        return null;
    }

    /**
     * @return
     */
    public String getChannel() {
        return Optional.ofNullable(threadLocal.get()).orElse(Maps.newHashMap()).get("channel");
    }

    /**
     * @return
     */
    public String getToken() {
        return Optional.ofNullable(threadLocal.get()).orElse(Maps.newHashMap()).get("token");
    }

    /**
     * @return
     */
    public String getBearerToken() {
        return "bearer " + getToken();
    }


    /**
     * 清空上下文
     */
    public void clear() {
        threadLocal.remove();
    }

}
