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

/**
 * 1bit 		+ 41bit 		+ 17bit 		+ 5bit
 * |			    |				|				|
 * |				|				|				|
 * 符合位     	        时间戳（毫秒）		     序列号			      机器码
 * 第1bit固定是0  符号位不动 。
 * 第2bit到第42bit使用时间蹉，精确到毫秒  41bit。 使用年限是69年
 * 第43bit到第59bit使用自增的序列号       17bit  可用序列号最大131071个，说明一毫秒我们可以生成131071个不同的序列号。
 * 第60bit到第64bit使用机器码	5bit   可以使系统可以分布式，最大分布式数量是32台机子。
 * <p>
 * 雪花生成ID工具类
 *
 * @author
 */
public class SnowflakeIdGenerate extends AbstractIdGenerate<Long> {

    public SnowflakeIdGenerate(final long machineCode) {
        super(machineCode);
    }

    @Override
    public Long generate() {
        return super.generateLong();
    }
}
