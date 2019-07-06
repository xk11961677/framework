package com.sky.framework.validator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@ConfigurationProperties(prefix = "validator")
@Data
@SuppressWarnings("all")
public class ValidatorProperties {

    private int errorCode = -1;

    private String errorMsg = "验证器异常";

    /**
     * hibernate 默认 990001
     * @see com.sky.framework.model.enums.FailureCodeEnum
     */
    private int hibernateDefaultErrorCode = 990001;

    private int order = 100;

    private List<String> beanNames = new ArrayList<>();
}
