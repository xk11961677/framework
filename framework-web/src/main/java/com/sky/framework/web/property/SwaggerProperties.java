package com.sky.framework.web.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The class Swagger properties.
 *
 * @author
 */

@Component
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerProperties {

    private Boolean enabled;

    private String title;

    private String description;

    private String version = "1.0";

    private String license = "Apache License 2.0";

    private String licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0";

    private String contactName = "";

    private String contactUrl = "";

    private String contactEmail = "";

}
