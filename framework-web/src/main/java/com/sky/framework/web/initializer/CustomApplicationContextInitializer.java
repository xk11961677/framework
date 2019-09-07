/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
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
package com.sky.framework.web.initializer;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 解决jar包,某些bean创建顺序问题
 * @author
 */
@Slf4j
public class CustomApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        this.initValidator(applicationContext);
    }

    /**
     * @param applicationContext
     * @see org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
     */
    public void initValidator(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        //todo 此处应该再加判断 第一次查看是否使用apollo , 然后再次判断ApolloBootstrapPropertySources
        boolean flag = environment.getPropertySources().contains("ApolloBootstrapPropertySources");
        String value = environment.getProperty("hibernate.validator.fail_fast", "true");
        if (flag && value.equals("true")) {
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
            Validator validator = validator();
            defaultListableBeanFactory.registerSingleton("validator", validator);
            defaultListableBeanFactory.registerSingleton("methodValidationPostProcessor", methodValidationPostProcessor(validator));
        }
    }

    private Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }

    private MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator);
        return postProcessor;
    }

}
