package com.sky.framework.common.tuple;

/**
 * 四个元素的元组，用于在一个方法里返回四种类型的值
 *
 * @author
 */
public class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {
    private final D fourth;

    public FourTuple(A a, B b, C c, D d) {
        super(a, b, c);
        this.fourth = d;
    }

    public D getFourth() {
        return fourth;
    }
}
