package com.sky.framework.web.config;

import com.sky.framework.common.LogUtil;
import com.sky.framework.web.constant.GlobalConstant;
import com.sky.framework.web.property.SwaggerProperties;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@ConditionalOnProperty(value = GlobalConstant.SWAGGER_ENABLE, matchIfMissing = true)
@EnableSwagger2
@Slf4j
public class SwaggerConfiguration {

    @Resource
    private SwaggerProperties swaggerProperties;

    /**
     * Reservation api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket createRestApi() {
        LogUtil.info(log, "skycloud base swagger starter !!! :{}", swaggerProperties);
        //每次都需手动输入header信息
        ParameterBuilder pb = new ParameterBuilder();
        List<Parameter> pars = new ArrayList();
        pb.name("Authorization").description("user access_token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false);
        pars.add(pb.build());

        ParameterBuilder pbChannel = new ParameterBuilder();
        pbChannel.name("channel").description("user channel")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false);
        pars.add(pbChannel.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                //配置鉴权信息
                .securitySchemes(securitySchemes())
				.securityContexts(securityContexts())
                .globalOperationParameters(pars)
                .enable(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
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