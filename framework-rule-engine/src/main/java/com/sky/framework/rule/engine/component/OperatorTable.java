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

import com.sky.framework.rule.engine.component.operator.Operator;
import com.sky.framework.rule.engine.util.SpiLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
@Slf4j
public class OperatorTable {

    private static final Map<String, Operator> container = new ConcurrentHashMap<>();

    private static final OperatorTable INSTANCE = new OperatorTable();

    private OperatorTable() {
        ServiceLoader<Operator> operators = SpiLoader.loadAll(Operator.class);
        Iterator<Operator> iterator = operators.iterator();
        while (iterator.hasNext()) {
            Operator operator = iterator.next();
            container.put(operator.code(), operator);
        }
    }

    public static OperatorTable getInstance() {
        return INSTANCE;
    }

    /**
     * 根据code获取Operator
     *
     * @param code
     * @return
     */
    public Operator get(String code) {
        return container.get(code);
    }

    /**
     * 存储Operator运算类
     *
     * @param code
     * @param operator
     */
    public void put(String code, Operator operator) {
        container.put(code, operator);
    }
}
