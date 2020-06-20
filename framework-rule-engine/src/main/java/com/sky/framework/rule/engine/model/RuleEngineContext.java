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
package com.sky.framework.rule.engine.model;

import com.sky.framework.rule.engine.enums.ResultEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 整体规则上下文
 *
 * @author
 */
@Slf4j
public class RuleEngineContext {
    /**
     * 执行器类
     */
    @Getter
    @Setter
    private Class executorClass;
    /**
     * 默认执行结果
     */
    @Getter
    @Setter
    private ResultEnum result = ResultEnum.EMPTY;
    /**
     * 规则容器(准备验证的)
     */
    @Getter
    @Setter
    private List<RuleItem> ruleItems;
    /**
     * 拒绝的规则
     */
    @Getter
    private List<RejectRuleItem> rejectRuleItems = new ArrayList<>();

    /**
     * ruleId :  ruleItem
     */
    @Getter
    private Map<String, RuleItem> map = new HashMap<>();

    public void setRuleItems(List<RuleItem> ruleItems) {
        this.ruleItems = ruleItems;
        ruleItems.forEach(r -> map.put(r.getItemNo(), r));
    }
}
