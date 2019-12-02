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
