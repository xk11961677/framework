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
