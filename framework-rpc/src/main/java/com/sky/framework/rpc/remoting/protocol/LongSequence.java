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
package com.sky.framework.rpc.remoting.protocol;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * 利用对象继承的内存布局规则来padding避免false sharing, 注意其中对象头会至少占用8个字节
 * ---------------------------------------
 * For 32 bit JVM:
 * _mark   : 4 byte constant
 * _klass  : 4 byte pointer to class
 * For 64 bit JVM:
 * _mark   : 8 byte constant
 * _klass  : 8 byte pointer to class
 * For 64 bit JVM with compressed-oops:
 * _mark   : 8 byte constant
 * _klass  : 4 byte pointer to class
 * ---------------------------------------
 */
class LongLhsPadding {
    @SuppressWarnings("unused")
    protected long p01, p02, p03, p04, p05, p06, p07;
}

class LongValue extends LongLhsPadding {
    protected volatile long value;
}

class LongRhsPadding extends LongValue {
    @SuppressWarnings("unused")
    protected long p09, p10, p11, p12, p13, p14, p15;
}

/**
 * 序号生成器, 每个线程预先申请一个区间, 步长(step)固定, 以此种方式尽量减少CAS操作,
 * 需要注意的是, 这个序号生成器不是严格自增的, 并且也溢出也是可以接受的(接受负数).
 *
 * @author
 */
public class LongSequence extends LongRhsPadding {

    private static final int DEFAULT_STEP = 128;

    private static final AtomicLongFieldUpdater<LongValue> updater = AtomicLongFieldUpdater.newUpdater(LongValue.class, "value");

    private final ThreadLocal<LocalSequence> localSequence = new ThreadLocal<LocalSequence>() {

        @Override
        protected LocalSequence initialValue() {
            return new LocalSequence();
        }
    };

    private final int step;

    public LongSequence() {
        this(DEFAULT_STEP);
    }

    public LongSequence(int step) {
        this.step = step;
    }

    public LongSequence(long initialValue, int step) {
        updater.set(this, initialValue);
        this.step = step;
    }

    public long next() {
        return localSequence.get().next();
    }

    private long getNextBaseValue() {
        return updater.getAndAdd(this, step);
    }

    private final class LocalSequence {

        private long localBase = getNextBaseValue();
        private long localValue = 0;

        public long next() {
            long realVal = ++localValue + localBase;

            if (localValue == step) {
                localBase = getNextBaseValue();
                localValue = 0;
            }

            return realVal;
        }
    }
}
