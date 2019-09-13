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
package com.sky.framework.common.tree;

/**
 * @author
 */
public interface ITreeNode {
    /**
     * 获取节点ID
     *
     * @return
     */
    String getNodeId();

    /**
     * 获取节点名称
     *
     * @return
     */
    String getNodeName();

    /**
     * 获取节点code
     *
     * @return
     */
    String getCode();

    /**
     * 获取父节点ID
     *
     * @return
     */
    String getNodeParentId();

    /**
     * 获取排序num
     *
     * @return
     */
    Integer getOrderNum();

    /**
     * 获取方法名
     *
     * @return
     */
    String getMethod();

    /**
     * 获取url
     *
     * @return
     */
    String getUrl();

    /**
     * 获取icon
     * @return
     */
    String getIcon();
}
