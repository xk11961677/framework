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

import com.sky.framework.redis.annotation.RedissonLock;
import com.sky.framework.redis.exception.RedissonLockException;
import com.sky.framework.redis.property.RedissonProperties;
import com.sky.framework.redis.util.RedissonLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

import java.lang.reflect.Method;

/**
 * @author
 */
@Aspect
@Component
@ConditionalOnClass(Redisson.class)
@Slf4j
public class RedissonLockAspect implements Ordered {

    @Autowired
    private RedissonProperties redissonProperties;

    @Pointcut(value = "@annotation(redissonLock)")
    public void pointCut(RedissonLock redissonLock) {
    }


    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RedissonLock redissonLock) throws Throwable {
        return doRedissonLock(proceedingJoinPoint, redissonLock);
    }

    @AfterThrowing(pointcut = "@annotation(redissonLock)", throwing = "error")
    public void afterThrowing(Throwable error, RedissonLock redissonLock) {
        log.error(error.getMessage(), error);
    }

    /**
     * 执行分布式锁
     *
     * @param proceedingJoinPoint
     * @param redissonLock
     * @return
     */
    private Object doRedissonLock(ProceedingJoinPoint proceedingJoinPoint, RedissonLock redissonLock) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        String lockKey = parseKey(redissonLock.key(), method, args);
        boolean lock = RedissonLockUtils.lock(lockKey, redissonLock.timeout(), redissonLock.leaseTime());
        try {
            if (!lock) {
                String clazzName = proceedingJoinPoint.getTarget().getClass().getSimpleName();
                String methodName = method.getName();
                throw new RedissonLockException(String.format("clazz=[%s], method=[%s], key=[%s] redisson lock not acquired !", clazzName, methodName, lockKey));
            }
            return proceedingJoinPoint.proceed();
        } finally {
            RedissonLockUtils.unlock(lockKey);
        }
    }


    /**
     * 获取锁上的key
     * key 定义在注解上，支持SPEL表达式
     *
     * @return
     */
    private String parseKey(String key, Method method, Object[] args) {
        if (StringUtils.isEmpty(key)) {
            throw new RedissonLockException("please setting redisson lock key !");
        }
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }

    @Override
    public int getOrder() {
        return redissonProperties.getAopLockOrder();
    }
}
