package com.sky.framework.rocketmq.model;

import lombok.Data;

/**
 * @author
 */
@Data
public class RocketMessage<M> {

    private String topic;

    private M body;

}
