package com.sky.framework.web;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author 启动类
 */
@Configuration
@ComponentScan(basePackageClasses = WebConfiguration.class)
@Slf4j
public class WebConfiguration implements CommandLineRunner {

    @ConditionalOnProperty(prefix = "hibernate.validator", value = "fail_fast", matchIfMissing = true)
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        /**设置validator模式为快速失败返回*/
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @ConditionalOnProperty(prefix = "hibernate.validator", value = "fail_fast", matchIfMissing = true)
    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class )
                .configure()
                .addProperty( "hibernate.validator.fail_fast", "true" )
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }

    @Override
    public void run(String... args) {
        log.info("sky framework web started !");
    }
}
