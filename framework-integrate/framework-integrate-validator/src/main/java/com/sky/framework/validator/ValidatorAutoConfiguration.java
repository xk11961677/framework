package com.sky.framework.validator;

import com.baidu.unbiz.fluentvalidator.ValidateCallback;
import com.baidu.unbiz.fluentvalidator.interceptor.FluentValidateInterceptor;
import com.sky.framework.validator.aop.ValidatorAdvisor;
import com.sky.framework.validator.callback.ValidateResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Configuration
@EnableConfigurationProperties(value = ValidatorProperties.class)
@ComponentScan(basePackageClasses = ValidatorAutoConfiguration.class)
@Slf4j
public class ValidatorAutoConfiguration {

    @Resource
    private ValidatorProperties validatorProperties;


    @Bean
    @ConditionalOnMissingBean
    public ValidateCallback validateCallback() {
        return new ValidateResultCallback();
    }

    @Bean("fluentValidateInterceptor")
    @ConditionalOnMissingBean
    public FluentValidateInterceptor fluentValidateInterceptor(ValidateCallback validateCallback) {
        FluentValidateInterceptor validateInterceptor = new FluentValidateInterceptor();
        validateInterceptor.setCallback(validateCallback);
        validateInterceptor.setLocale("zh_CN");
        validateInterceptor.setHibernateDefaultErrorCode(validatorProperties.getHibernateDefaultErrorCode());
        return validateInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidatorAdvisor validatorAdvisor(FluentValidateInterceptor fluentValidateInterceptor) {
        ValidatorAdvisor advisor = new ValidatorAdvisor();
        advisor.setExpression(expression(validatorProperties.getBeanNames()));
        advisor.setFluentValidateInterceptor(fluentValidateInterceptor);
        advisor.setOrder(validatorProperties.getOrder());
        return advisor;
    }


    /**
     * s.append("execution(* ").append(beanName).append(" ").append("(..))").append(" or ");
     *
     * @param beanNames
     * @return
     */
    private String expression(List<String> beanNames) {
        if(beanNames == null || beanNames.size() ==0) {
            beanNames = new ArrayList<>();
            beanNames.add("*xxxxxxxxxx");
        }
        Assert.notEmpty(beanNames, "'beanNames' must not be empty");
        StringBuffer s = new StringBuffer().append("( ");
        for (String beanName : beanNames) {
            s.append("bean(" + beanName + ")").append(" || ");
        }
        String rs = s.substring(0, s.length() - 4);
        rs += ")";
        return rs;
    }
}
