package com.sky.framework.validator.aop;

import com.baidu.unbiz.fluentvalidator.interceptor.FluentValidateInterceptor;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.core.Ordered;

/**
 * @author
 */
public class ValidatorAdvisor implements PointcutAdvisor, Ordered {

    @Setter
    private FluentValidateInterceptor fluentValidateInterceptor;

    @Setter
    private String expression;

    @Setter
    private int order;

    @Override
    public Advice getAdvice() {
        return fluentValidateInterceptor;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Pointcut getPointcut() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return pointcut;
    }
}
