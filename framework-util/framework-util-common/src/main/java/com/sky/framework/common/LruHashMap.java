package com.sky.framework.common;

import java.util.LinkedHashMap;

/**
 * 非线程安全的，
 * 支持lru 算法的map，此map 构造时设置map存储的最大值
 * 当超过此值时，执行lru算法
 */
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1779766949449438092L;

    private static final int INITIAL_CAPACITY = 16;

    private static final float LOAD_FACTOR = 0.75f;

    /**
     * 固定map 大小
     */
    private int sizeCount;

    /**
     * 执行map 的默认构造
     */
    public LruHashMap(int sizeCount) {
        super(INITIAL_CAPACITY, LOAD_FACTOR, true);
        this.sizeCount = sizeCount;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        if (super.size() <= this.sizeCount) {
            return super.removeEldestEntry(eldest);
        } else {
            super.remove(eldest.getKey());
            return true;
        }
    }

}
