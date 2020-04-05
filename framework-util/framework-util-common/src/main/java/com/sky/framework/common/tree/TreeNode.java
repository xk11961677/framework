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
package com.sky.framework.common.tree;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Data
public class TreeNode {
    /**
     * 树节点ID
     */
    @JSONField(ordinal = 1)
    private String nodeId;
    /**
     * 树节点名称
     */
    @JSONField(ordinal = 2)
    private String nodeName;
    /**
     * 父节点ID
     */
    @JSONField(ordinal = 3)
    private String parentNodeId;
    /**
     * 节点在树中的排序号
     */
    @JSONField(ordinal = 4)
    private int orderNum;
    /**
     * 节点所在的层级
     */
    @JSONField(ordinal = 5)
    private int level;

    /**
     * 节点方法
     */
    @JSONField(ordinal = 6)
    private String method;

    /**
     * 节点url
     */
    @JSONField(ordinal = 7)
    private String url;

    /**
     * 节点
     */
    @JSONField(ordinal = 8)
    private String code;

    /**
     * 节点
     */
    @JSONField(ordinal = 9)
    private String icon;

    private TreeNode parent;
    /**
     * 当前节点的子节点
     */
    @JSONField(ordinal = 10)
    private List<TreeNode> children = new ArrayList<TreeNode>();
    /**
     * 当前节点的子孙节点
     */
    private List<TreeNode> allChildren = new ArrayList<TreeNode>();

    public TreeNode(ITreeNode obj) {
        this.nodeId = obj.getNodeId();
        this.nodeName = obj.getNodeName();
        this.parentNodeId = obj.getNodeParentId();
        this.orderNum = obj.getOrderNum();
        this.code = obj.getCode();
        this.method = obj.getMethod();
        this.url = obj.getUrl();
        this.icon = obj.getIcon();
    }

    public void addChild(TreeNode treeNode) {
        this.children.add(treeNode);
    }

    public void removeChild(TreeNode treeNode) {
        this.children.remove(treeNode);
    }

    public List<TreeNode> getAllChildren() {
        if (this.allChildren.isEmpty()) {
            for (TreeNode treeNode : this.children) {
                this.allChildren.add(treeNode);
                this.allChildren.addAll(treeNode.getAllChildren());
            }
        }
        return this.allChildren;
    }


    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

}
