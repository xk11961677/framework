package com.sky.framework.rpc.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author
 */
public enum LoadBalanceEnum {
    ROUNDROBIN("round"),
    RANDOM("random"),
    ROUNDROBIN_WEIGHT("weight"),
    LEAST_CONNECTION("least");


    String key;

    LoadBalanceEnum(String key) {
        this.key = key;
    }


    public static LoadBalanceEnum acquire(String key) {
        Optional<LoadBalanceEnum> loadBalanceEnum =
                Arrays.stream(LoadBalanceEnum.values())
                        .filter(v -> Objects.equals(v.getKey(), key))
                        .findFirst();
        return loadBalanceEnum.orElse(LoadBalanceEnum.ROUNDROBIN);
    }

    public String getKey() {
        return key;
    }
}
