/*
 * The MIT License (MIT)
 * Copyright © 2019 <reach>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author
 */
public class Tree implements ITree {
    private HashMap<String, TreeNode> treeNodesMap = new HashMap<>();
    private List<TreeNode> treeNodesList = new ArrayList<>();

    public Tree(List<ITreeNode> list) {
        initTreeNodeMap(list);
        initTreeNodeList();
    }

    private void initTreeNodeMap(List<ITreeNode> list) {
        TreeNode treeNode;
        for (ITreeNode item : list) {
            treeNode = new TreeNode(item);
            treeNodesMap.put(treeNode.getNodeId(), treeNode);
        }

        Iterator<TreeNode> iter = treeNodesMap.values().iterator();
        TreeNode parentTreeNode;
        while (iter.hasNext()) {
            treeNode = iter.next();
            if (treeNode.getParentNodeId() == null || treeNode.getParentNodeId() == "") {
                continue;
            }

            parentTreeNode = treeNodesMap.get(treeNode.getParentNodeId());
            if (parentTreeNode != null) {
                treeNode.setParent(parentTreeNode);
                parentTreeNode.addChild(treeNode);
            }
        }
    }

    private void initTreeNodeList() {
        if (treeNodesList.size() > 0) {
            return;
        }
        if (treeNodesMap.size() == 0) {
            return;
        }
        Iterator<TreeNode> iter = treeNodesMap.values().iterator();
        TreeNode treeNode;
        while (iter.hasNext()) {
            treeNode = iter.next();
            if (treeNode.getParent() == null) {
                this.treeNodesList.add(treeNode);
                this.treeNodesList.addAll(treeNode.getAllChildren());
            }
        }
    }

    @Override
    public List<TreeNode> getTree() {
        return this.treeNodesList;
    }

    @Override
    public List<TreeNode> getRoot() {
        List<TreeNode> rootList = new ArrayList<TreeNode>();
        if (this.treeNodesList.size() > 0) {
            for (TreeNode node : treeNodesList) {
                if (node.getParent() == null) {
                    rootList.add(node);
                }
            }
        }
        return rootList;
    }

    @Override
    public TreeNode getTreeNode(String nodeId) {
        return this.treeNodesMap.get(nodeId);
    }
}