package com.sky.framework.common.tuple;

/**
 * 元组辅助类，用于多种类型值的返回，如在分页的时候，后台存储过程既返回了查询得到的
 * 当页的数据（List类型），又得到了数据表中总共的数据总数（Integer类型），然后将这
 * 两个参数封装到该类中返回到action中使用
 * 使用泛型方法实现，利用参数类型推断，编译器可以找出具体的类型
 *
 * @author
 */
public final class TupleUtil {

    private TupleUtil() {
    }

    /**
     * 生成并返回二元元组
     *
     * @param a   元组数据一
     * @param <A> 元组数据一的类型(可以根据元组数据一的类型自动推断)
     * @param b   元组数据二
     * @param <B> 元组数据二的类型(可以根据元组数据二的类型自动推断)
     * @return 生成的二元元组
     */
    public static <A, B> TwoTuple<A, B> tuple(A a, B b) {
        return new TwoTuple<A, B>(a, b);
    }

    /**
     * 生成并返回三元元组
     *
     * @param a   元组数据一
     * @param <A> 元组数据一的类型(可以根据元组数据一的类型自动推断)
     * @param b   元组数据二
     * @param <B> 元组数据二的类型(可以根据元组数据二的类型自动推断)
     * @param c   元组数据三
     * @param <C> 元组数据三的类型(可以根据元组数据三的类型自动推断)
     * @return 生成的三元元组
     */
    public static <A, B, C> ThreeTuple<A, B, C> tuple(A a, B b, C c) {
        return new ThreeTuple<A, B, C>(a, b, c);
    }

    /**
     * 生成并返回四元元组
     *
     * @param a   元组数据一
     * @param <A> 元组数据一的类型(可以根据元组数据一的类型自动推断)
     * @param b   元组数据二
     * @param <B> 元组数据二的类型(可以根据元组数据二的类型自动推断)
     * @param c   元组数据三
     * @param <C> 元组数据三的类型(可以根据元组数据三的类型自动推断)
     * @param c   元组数据四
     * @param <C> 元组数据四的类型(可以根据元组数据四的类型自动推断)
     * @return 生成的四元元组
     */
    public static <A, B, C, D> FourTuple<A, B, C, D> tuple(A a, B b, C c, D d) {
        return new FourTuple<A, B, C, D>(a, b, c, d);
    }
}
