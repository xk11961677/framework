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
package com.sky.framework.redis.aop;

import com.sky.framework.redis.annotation.DistributedLock;
import com.sky.framework.redis.annotation.DistributedLockKey;
import com.sky.framework.redis.exception.DistributedLockException;
import com.sky.framework.redis.property.RedissonProperties;
import com.sky.framework.redis.util.DistributedLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Aspect
@Component
@ConditionalOnClass(Redisson.class)
@Slf4j
public class LockAspect implements Ordered {

    private static final String LOCK_NAME_PREFIX = "distributedLock";

    private static final String LOCK_NAME_SEPARATOR = ".";

    @Autowired
    private RedissonProperties redissonProperties;

    @Pointcut(value = "@annotation(distributedLock)")
    public void pointCut(DistributedLock distributedLock) {
    }


    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, DistributedLock distributedLock) throws Throwable {
        return doDistributedLock(proceedingJoinPoint, distributedLock);
    }

    @AfterThrowing(pointcut = "@annotation(distributedLock)", throwing = "error")
    public void afterThrowing(Throwable error, DistributedLock distributedLock) throws Throwable {
        throw error;
    }

    /**
     * 执行分布式锁
     *
     * @param proceedingJoinPoint
     * @param distributedLock
     * @return
     */
    private Object doDistributedLock(ProceedingJoinPoint proceedingJoinPoint, DistributedLock distributedLock) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();

        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();

        String lockKey = distributedLock.prefix() + LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + parseKey(distributedLock.keys(), method, args);

        boolean lock = distributedLock.model().getLock(lockKey, distributedLock.timeout(), distributedLock.leaseTime());
        try {
            if (!lock) {
                String clazzName = proceedingJoinPoint.getTarget().getClass().getSimpleName();
                String methodName = method.getName();
                throw new DistributedLockException(String.format("clazz=[%s], method=[%s], key=[%s] distributed lock not acquired !", clazzName, methodName, lockKey));
            }
            return proceedingJoinPoint.proceed();
        } finally {
            DistributedLockUtils.forceUnlock(lockKey);
        }
    }


    /**
     * 获取锁上的key
     * key 定义在注解上，支持SPEL表达式
     *
     * @return
     */
    private String parseKey(String[] keys, Method method, Object[] args) {
        List<String> list = new ArrayList<>();
        ExpressionParser parser = new SpelExpressionParser();
        //解析注解上的keys
        if (keys != null && keys.length != 0) {
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paraNameArr = u.getParameterNames(method);
            StandardEvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < paraNameArr.length; i++) {
                context.setVariable(paraNameArr[i], args[i]);
            }

            for (String key : keys) {
                list.add(parser.parseExpression(key).getValue(context, String.class));
            }
        }

        //解析参数注解上的key
        parseParameterKey(method, args, list, parser);

        if (CollectionUtils.isEmpty(list)) {
            throw new DistributedLockException("please setting distributed lock key !");
        }
        return StringUtils.collectionToDelimitedString(list, "", "-", "");
    }

    /**
     * 解析方法入参的独立DistributedLockKey
     *
     * @param method
     * @param args
     * @param list
     * @param parser
     */
    private void parseParameterKey(Method method, Object[] args, List<String> list, ExpressionParser parser) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(DistributedLockKey.class) != null) {
                DistributedLockKey keyAnnotation = parameters[i].getAnnotation(DistributedLockKey.class);
                if (keyAnnotation.value().isEmpty()) {
                    list.add(args[i].toString());
                } else {
                    StandardEvaluationContext context = new StandardEvaluationContext(args[i]);
                    String key = parser.parseExpression(keyAnnotation.value()).getValue(context, String.class);
                    list.add(key);
                }
            }
        }
    }

    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
        List<String> parameterKey = new ArrayList<>();

        return parameterKey;
    }

    @Override
    public int getOrder() {
        return redissonProperties.getLockAspectOrder();
    }
}
