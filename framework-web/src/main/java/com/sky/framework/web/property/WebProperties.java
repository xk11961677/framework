package com.sky.framework.web.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
@ConfigurationProperties(prefix = "web")
@Data
public class WebProperties {

    private Boolean globalExceptionEnabled;

    private Boolean globalConverterEnabled;

}
