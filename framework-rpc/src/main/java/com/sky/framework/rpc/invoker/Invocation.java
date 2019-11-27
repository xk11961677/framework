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
package com.sky.framework.rpc.invoker;

import java.util.Map;

/**
 * 调用者对象
 *
 * @author
 */
public interface Invocation {

    /**
     * 获取类名称
     *
     * @return
     */
    String getClazzName();

    /**
     * 获取方法名称
     *
     * @return
     */
    String getMethodName();

    /**
     * 获取方法参数类型
     *
     * @return
     */
    Class<?>[] getParameterTypes();

    /**
     * 获取方法参数
     *
     * @return
     */
    Object[] getArguments();

    /**
     * 获取全部附加参数
     *
     * @return
     */
    Map<String, String> getAttachments();

    /**
     * 设置附加参数
     *
     * @param key
     * @param value
     */
    void setAttachment(String key, String value);

    /**
     * 附加参数没有此参数,则设置此参数
     *
     * @param key
     * @param value
     */
    void setAttachmentIfAbsent(String key, String value);

    /**
     * 获取附加参数
     *
     * @param key
     * @return
     */
    String getAttachment(String key);

    /**
     * 获取附加参数,没有则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    String getAttachment(String key, String defaultValue);


}
