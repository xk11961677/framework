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
package com.sky.framework.rule.engine;


import com.sky.framework.rule.engine.component.AbstractRuleItem;
import com.sky.framework.rule.engine.component.RuleExecutorTable;
import com.sky.framework.rule.engine.component.executor.ComplexRuleExecutor;
import com.sky.framework.rule.engine.component.executor.DefaultRuleExecutor;
import com.sky.framework.rule.engine.enums.ResultEnum;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.model.RuleEngineContext;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Slf4j
public class RuleEngineService {

    private Class executorClass;

    public RuleEngineService() {
        this(DefaultRuleExecutor.class);
    }

    public RuleEngineService(Class executorClass) {
        this.executorClass = executorClass;
    }

    /**
     * @param object
     * @return
     * @throws RuleEngineException
     */
    public RuleEngineContext start(Object object, List<RuleItem> items) throws RuleEngineException {
        RuleEngineContext ruleEngineContext = new RuleEngineContext();
        ruleEngineContext.setRuleItems(items);
        ruleEngineContext.setResult(ResultEnum.PASSED);
        ruleEngineContext.setExecutorClass(executorClass);

        List<RuleItem> itemList = this.filterItem(items, null);

        for (RuleItem item : itemList) {
            if (StringUtils.isNotEmpty(item.getGroupExpress())) {
                AbstractRuleItem executor = new ComplexRuleExecutor();
                executor.setObject(object);
                executor.setRuleEngineContext(ruleEngineContext);
                ItemResult result = executor.doCheck(item);
                ruleEngineContext.setResult(result.getResult());
                if (ResultEnum.PASSED.equals(result.getResult()) && !result.canBeContinue()) {
                    break;
                }
            } else {
                AbstractRuleItem executor = RuleExecutorTable.get(executorClass);
                executor.setObject(object);
                executor.setRuleEngineContext(ruleEngineContext);
                ItemResult result = executor.doCheck(item);
                ruleEngineContext.setResult(result.getResult());
                if (ResultEnum.PASSED.equals(result.getResult()) && !result.canBeContinue()) {
                    break;
                }
            }
        }
        return ruleEngineContext;
    }


    /**
     * 查找同层级的规则列表
     *
     * @param itemList     要查找的对象
     * @param parentItemNo 父级规则
     * @return 同层级的规则列表
     */
    public List<RuleItem> filterItem(List<RuleItem> itemList, String parentItemNo) {

        List<RuleItem> newItemList = new ArrayList<>();

        for (int iLoop = 0; iLoop < itemList.size(); iLoop++) {

            RuleItem item = itemList.get(iLoop);
            if (StringUtils.isEmpty(parentItemNo)) {
                if (StringUtils.isEmpty(item.getParentItemNo())) {
                    newItemList.add(item);
                }
            } else {
                if (parentItemNo.equalsIgnoreCase(item.getParentItemNo())) {
                    newItemList.add(item);
                }
            }
        }

        return newItemList;

    }
}
