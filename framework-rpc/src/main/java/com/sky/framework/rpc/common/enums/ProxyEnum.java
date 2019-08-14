package com.sky.framework.rpc.common.enums;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


/**
 * @author
 */
public enum ProxyEnum {
    JDK("jdk"),

    JAVASSIST("javassist"),

    BYTEBUDDY("byteBuddy");

    @Getter
    @Setter
    private String proxy;

    ProxyEnum(String proxy) {
        this.proxy = proxy;
    }


    public static ProxyEnum acquire(String proxy) {
        Optional<ProxyEnum> serializeEnum =
                Arrays.stream(ProxyEnum.values())
                        .filter(v -> Objects.equals(v.getProxy(), proxy))
                        .findFirst();
        return serializeEnum.orElse(ProxyEnum.JAVASSIST);
    }
}


