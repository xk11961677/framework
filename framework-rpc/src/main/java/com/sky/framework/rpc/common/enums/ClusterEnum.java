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
package com.sky.framework.rpc.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author
 */
public enum ClusterEnum {
    FAILOVER("failover"),
    FAILBACK("failback"),
    FAILFAST("failfast"),
    FAILSAFE("failsafe");


    String key;

    ClusterEnum(String key) {
        this.key = key;
    }


    public static ClusterEnum acquire(String key) {
        Optional<ClusterEnum> clusterEnum =
                Arrays.stream(ClusterEnum.values())
                        .filter(v -> Objects.equals(v.getKey(), key))
                        .findFirst();
        return clusterEnum.orElse(ClusterEnum.FAILOVER);
    }

    public String getKey() {
        return key;
    }
}
