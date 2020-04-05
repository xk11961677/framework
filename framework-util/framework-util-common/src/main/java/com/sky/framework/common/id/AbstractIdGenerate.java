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
package com.sky.framework.common.id;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象 ID 生成器
 *
 * @author
 */
public abstract class AbstractIdGenerate<T extends Serializable> implements IdGenerate<T> {

    private final static int MAX_MACHINE_CODE = 31;
    /**
     * 最大17bit的序列号是131071
     */
    private final static int MAX_ORDER_NO = 131071;
    /**
     * 时间戳的掩码41bit
     */
    private final static long TIME_CODE = Long.MAX_VALUE >>> 22;
    /**
     * Fri Feb 01 01:01:01 CST 2019
     * 1548954061000L
     * 指定生成器开始时间
     */
    private final static long START_TIME = 1548954061000L;
    /**
     * 机器码 （0-31）
     */
    private final long MACHINE_CODE;
    /**
     * 用于生成序列号
     */
    private AtomicInteger orderNo;

    public AbstractIdGenerate(final long machineCode) {
        if (machineCode < 0 || machineCode > MAX_MACHINE_CODE) {
            throw new IllegalArgumentException("请注意，1、机器码在多台机器或应用间是不允许重复的！2、机器码取值仅仅在0~31之间");
        }
        this.MACHINE_CODE = machineCode;
        orderNo = new AtomicInteger(0);
    }

    protected Long generateLong() {
        //1.与基准时间对其，得到相对时间
        long currentTimeMillis = System.currentTimeMillis() - START_TIME;
        //2.保留相对时间的低41bit
        currentTimeMillis = currentTimeMillis & TIME_CODE;
        //3、将1到41bit移到高位去 就是23~63。
        currentTimeMillis = currentTimeMillis << 22;

        /*
         * 序列号自增1和获取
         * 注意：先增加再取值。
         */
        int orderNo = this.orderNo.incrementAndGet();
        do {
            if (orderNo > MAX_ORDER_NO) {
                //如果超过了最大序列号   则重置为0
                if (this.orderNo.compareAndSet(orderNo, 0)) {
                    //这里使用cas操作，所以不需要加锁    1、操作失败了   则表示别的线程已经更改了数据，则直接进行自增并获取则可以了
                    orderNo = 0;
                } else {
                    //注意：先增加再取值。
                    orderNo = this.orderNo.incrementAndGet();
                }
            }
        } while (orderNo > MAX_ORDER_NO);

        //符号位（1）bit、时间戳（2~42）bit | 序列号（43~59）bit | 机器码（60~64）bit
        return currentTimeMillis | (orderNo << 5) | MACHINE_CODE;
    }
}
