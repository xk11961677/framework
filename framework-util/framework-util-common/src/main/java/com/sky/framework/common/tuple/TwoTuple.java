package com.sky.framework.common.tuple;

/**
 * 两个元素的元组，用于在一个方法里返回两种类型的值
 *
 * @author
 */
public class TwoTuple<A, B> {
    private final A first;
    private final B second;

    public TwoTuple(A a, B b) {
        this.first = a;
        this.second = b;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
