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
package com.sky.framework.rule.engine.component;

import com.esotericsoftware.reflectasm.ConstructorAccess;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
public class RuleExecutorTable {

    private static final Object lock = new Object();

    private static final ConcurrentHashMap<String, ConstructorAccess> ruleExecutor = new ConcurrentHashMap<>();

    /**
     * 根据class获取执行器对象
     *
     * @param clazz
     * @return
     */
    public static AbstractRuleItem get(Class clazz) {
        ConstructorAccess constructorAccess = ruleExecutor.get(clazz.getName());
        if (constructorAccess == null) {
            synchronized (lock) {
                constructorAccess = ruleExecutor.get(clazz.getName());
                if (constructorAccess == null) {
                    constructorAccess = ConstructorAccess.get(clazz);
                    ruleExecutor.put(clazz.getName(), constructorAccess);
                }
            }
        }
        return (AbstractRuleItem) constructorAccess.newInstance();
    }
}
