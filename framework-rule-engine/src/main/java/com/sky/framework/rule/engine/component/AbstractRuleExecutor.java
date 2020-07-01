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
import com.sky.framework.rule.engine.constant.OperatorConstants;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.model.RuleEngineContext;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author
 */
@SuppressWarnings("AliControlFlowStatementWithoutBraces")
@Slf4j
public abstract class AbstractRuleExecutor {

    /**
     * 源数据
     */
    @Getter
    @Setter
    protected Object object;

    /**
     * 最终结果上下文
     */
    @Getter
    @Setter
    protected RuleEngineContext ruleEngineContext;


    /**
     * 执行验证
     *
     * @param item
     * @return
     * @throws RuleEngineException
     */
    public abstract ItemResult doCheck(RuleItem item) throws RuleEngineException;

    /**
     * 支持 对key 的操作符判断
     *
     * @param data
     * @param comparisonOperator
     * @param baseline
     * @param key
     * @return
     */
    public static boolean comparisonOperate(Object data, String comparisonOperator, List baseline, String key) {
        if (OperatorConstants.OPR_CODE.EXISTS.equals(comparisonOperator)) {
            return StringUtils.isBlank(key) ? false : true;
        }
        if (OperatorConstants.OPR_CODE.NOT_EXISTS.equals(comparisonOperator)) {
            return StringUtils.isBlank(key) ? true : false;
        }
        return comparisonOperate(data, comparisonOperator, baseline);
    }

    /**
     * 比较运算操作，将执行的结果和RuleItem中的baseline作比较
     *
     * @param data               比较对象
     * @param comparisonOperator 比较操作符号，在OperatorConstants中定义
     * @param baseline           比较基线，用于比较的对象
     * @return 根据comparisonOperator运行的结果。 true or false
     * @throws RuleEngineException 参数不合法，或者比较操作符不合法
     */
    public static boolean comparisonOperate(Object data, String comparisonOperator, List baseline) {
        if (null == baseline || null == comparisonOperator) {
            return false;
            //throw new RuleEngineException("null pointer error of subject or baseline or comparison code.");
        }
        Operator operator = OperatorTable.INSTANCE.get(comparisonOperator);
        if (operator == null) {
            log.warn("AbstractRuleItem.comparisonOperate operator:{} not found", comparisonOperator);
            return false;
        }
        return operator.execute(data, baseline);
    }
}
