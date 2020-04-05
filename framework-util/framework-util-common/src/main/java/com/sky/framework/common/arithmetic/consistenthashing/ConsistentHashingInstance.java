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
package com.sky.framework.common.arithmetic.consistenthashing;


import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性hash算法类
 *  线程不安全
 */
public final class ConsistentHashingInstance {
	//默认虚节点个数
	public static final int NUMREPS = 160;
	
	public static final int HASH_FOUR = 4;
	
	private TreeMap<Long, Node> ketamaNodes;
	private HashAlgorithm hashAlg;
	private int numReps ;
	
	public ConsistentHashingInstance(List<Node> nodes, HashAlgorithm alg){
		this(nodes, alg, NUMREPS);
	}
	
    public ConsistentHashingInstance(List<Node> nodes, HashAlgorithm alg, int nodeCopies) {
		hashAlg = alg;
		ketamaNodes = new TreeMap<Long, Node>();
		
        numReps= nodeCopies;
        
		for (Node node : nodes) {
			for (int i = 0; i < numReps / HASH_FOUR; i++) {
				byte[] digest = hashAlg.computeMd5(node.getName() + i);
				for(int h = 0; h < HASH_FOUR; h++) {
					long m = hashAlg.hash(digest, h);
					
					ketamaNodes.put(m, node);
				}
			}
		}
    }
    /**
     * 获取结点信息
     * 
     * @param k
     * @return
     */
	public Node getPrimary(final String k) {
		byte[] digest = hashAlg.computeMd5(k);
		Node rv = getNodeForKey(hashAlg.hash(digest, 0));
		return rv;
	}

	Node getNodeForKey(long hash) {
		final Node rv;
		Long key = hash;
		if(!ketamaNodes.containsKey(key)) {
			SortedMap<Long, Node> tailMap=ketamaNodes.tailMap(key);
			if(tailMap.isEmpty()) {
				key=ketamaNodes.firstKey();
			} else {
				key=tailMap.firstKey();
			}
		}
		rv=ketamaNodes.get(key);
		return rv;
	}
}
