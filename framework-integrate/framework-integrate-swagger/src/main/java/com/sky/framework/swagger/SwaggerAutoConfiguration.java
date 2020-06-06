/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
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
package com.sky.framework.swagger;

import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class Swagger configuration.
 *
 * @author
 */
@Configuration
@EnableConfigurationProperties(value = SwaggerProperties.class)
@ComponentScan(basePackageClasses = SwaggerAutoConfiguration.class)
@ConditionalOnProperty(value = SwaggerProperties.SWAGGER_ENABLE, matchIfMissing = true)
@EnableSwagger2
@Slf4j
public class SwaggerAutoConfiguration {

    public static final String PREFIX = "fw.";

    @Resource
    private SwaggerProperties swaggerProperties;

    /**
     * Reservation api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket createRestApi() {
        log.info("sky framework web add swagger successfully !:{}", swaggerProperties);
        //每次都需手动输入header信息
        List<Parameter> pars = new ArrayList();
        this.addParameters(pars);

        Predicate<RequestHandler> selector = RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class);
        if (!StringUtils.isEmpty(swaggerProperties.getBasePackage())) {
            selector = RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage());
        }

        Docket build = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(selector)
                .paths(PathSelectors.any())
                .build();

        if (swaggerProperties.getAuthorization()) {
            //配置鉴权信息
            build.securitySchemes(securitySchemes()).securityContexts(securityContexts());
        }

        if (!StringUtils.isEmpty(swaggerProperties.getHost())) {
            build.host(swaggerProperties.getHost());
        }

        build.globalOperationParameters(pars).enable(true);

        return build;
    }

    /**
     * 添加参数
     *
     * @param pars
     */
    private void addParameters(List<Parameter> pars) {
        if (swaggerProperties.getAuthorization()) {
            ParameterBuilder pb = new ParameterBuilder();
            pb.name("Authorization").description("access_token")
                    .modelRef(new ModelRef("string")).parameterType("header")
                    .required(false);
            pars.add(pb.build());
        }

        if (swaggerProperties.getChannel()) {
            ParameterBuilder pbChannel = new ParameterBuilder();
            pbChannel.name("channel").description("channel")
                    .modelRef(new ModelRef("string")).parameterType("header")
                    .required(false);
            pars.add(pbChannel.build());
        }
    }

    private ApiInfo apiInfo() {
        String description = swaggerProperties.getDescription();
        description = StringUtils.isEmpty(description) ? swaggerProperties.getTitle() : description;
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(description)
                .version(swaggerProperties.getVersion())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .contact(new Contact(swaggerProperties.getContactName(), swaggerProperties.getContactUrl(), swaggerProperties.getContactEmail()))
                .build();
    }

    private List<ApiKey> securitySchemes() {
        return new ArrayList(Collections.singleton(new ApiKey("Authorization", "Authorization", "header")));
    }

    private List<SecurityContext> securityContexts() {
        return new ArrayList(
                Collections.singleton(SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!oauth).*$"))
                        .build())
        );
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return new ArrayList(Collections.singleton(new SecurityReference("Authorization", authorizationScopes)));
    }

}