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


import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author
 */

public enum SerializeEnum {

    /**
     * Jdk serialize protocol enum.
     */
    JDK("jdk", (byte) 0x04),

    /**
     * Fastjson serialize protocol enum.
     */
    FASTJSON("fastjson", (byte) 0x05),

    /**
     * Kryo serialize protocol enum.
     */
    KRYO("kryo", (byte) 0x03),

    /**
     * Hessian serialize protocol enum.
     */
    HESSIAN("hessian", (byte) 0x02),

    /**
     * Protostuff serialize protocol enum.
     */
    PROTOSTUFF("protostuff", (byte) 0x01);

    @Getter
    @Setter
    private String serialize;

    @Getter
    @Setter
    private byte serializerCode;

    SerializeEnum(String serialize, byte serializerCode) {
        this.serialize = serialize;
        this.serializerCode = serializerCode;
    }

    /**
     * Acquire serialize protocol serialize protocol enum.
     *
     * @param serialize the serialize protocol
     * @return the serialize protocol enum
     */
    public static SerializeEnum acquire(String serialize) {
        Optional<SerializeEnum> serializeEnum =
                Arrays.stream(SerializeEnum.values())
                        .filter(v -> Objects.equals(v.getSerialize(), serialize))
                        .findFirst();
        return serializeEnum.orElse(SerializeEnum.FASTJSON);
    }

    /**
     * Acquire serialize protocol serializeCode protocol enum.
     *
     * @param serializeCode the serialize protocol
     * @return the serialize protocol enum
     */
    public static SerializeEnum acquire(byte serializeCode) {
        Optional<SerializeEnum> serializeEnum =
                Arrays.stream(SerializeEnum.values())
                        .filter(v -> Objects.equals(v.getSerializerCode(), serializeCode))
                        .findFirst();
        return serializeEnum.orElse(SerializeEnum.FASTJSON);
    }
}
