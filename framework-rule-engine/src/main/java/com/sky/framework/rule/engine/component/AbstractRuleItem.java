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
package com.sky.framework.rule.engine.component;

import com.sky.framework.rule.engine.constant.OperatorConstants;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.EngineResultContext;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;

import java.math.BigDecimal;

/**
 * @author
 */
public abstract class AbstractRuleItem {

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
    protected EngineResultContext resultContext;


    /**
     * 执行验证
     *
     * @param item
     * @return
     * @throws RuleEngineException
     */
    public abstract ItemResult doCheck(RuleItem item) throws RuleEngineException;

    /**
     * 比较运算操作，将执行的结果和RuleItem中的baseline作比较。
     *
     * @param data           比较对象
     * @param comparisonOperator 比较操作符号，在OperatorConstants中定义。
     * @param baseline       比较基线，用于比较的对象。
     * @return 根据comparisonOperator运行的结果。 true or false。
     * @throws RuleEngineException 参数不合法，或者比较操作符不合法。
     */
    public static boolean comparisonOperate(Object data, String comparisonOperator, String baseline) throws RuleEngineException {
        boolean bRet = false;
        if (null == data || null == baseline || null == comparisonOperator) {
            throw new RuleEngineException("null pointer error of subject or baseline or comparison code.");
        }
        BigDecimal bdSubject = null;
        BigDecimal object = null;

        String subject = ObjectUtils.toString(data);
        switch (comparisonOperator) {
            case OperatorConstants.OPR_CODE.EQUAL:
                try {
                    bdSubject = new BigDecimal(subject);
                    object = new BigDecimal(baseline);
                    bRet = (bdSubject.compareTo(object) == 0);
                } catch (Exception e) {
                    bRet = subject.equals(baseline);
                }
                break;
            case OperatorConstants.OPR_CODE.GREATER:
                try {
                    bdSubject = new BigDecimal(subject);
                    object = new BigDecimal(baseline);
                    bRet = (bdSubject.compareTo(object) > 0);
                } catch (Exception e1) {
                    bRet = (subject.compareTo(baseline) > 0);
                }
                break;
            case OperatorConstants.OPR_CODE.LESS:
                try {
                    bdSubject = new BigDecimal(subject);
                    object = new BigDecimal(baseline);
                    bRet = (bdSubject.compareTo(object) < 0);
                } catch (Exception e1) {
                    bRet = (subject.compareTo(baseline) < 0);
                }
                break;
            case OperatorConstants.OPR_CODE.NOT_EQUAL:
                try {
                    bdSubject = new BigDecimal(subject);
                    object = new BigDecimal(baseline);
                    bRet = (bdSubject.compareTo(object) != 0);
                } catch (Exception e) {
                    bRet = !subject.equals(baseline);
                }
                break;
            case OperatorConstants.OPR_CODE.GREATER_EQUAL:
                try {
                    bdSubject = new BigDecimal(subject);
                    object = new BigDecimal(baseline);
                    bRet = (bdSubject.compareTo(object) >= 0);
                } catch (Exception e) {
                    bRet = (subject.compareTo(baseline) >= 0);
                }
                break;
            case OperatorConstants.OPR_CODE.LESS_EQUAL:
                try {
                    bdSubject = new BigDecimal(subject);
                    object = new BigDecimal(baseline);
                    bRet = (bdSubject.compareTo(object) <= 0);
                } catch (Exception e) {
                    bRet = (subject.compareTo(baseline) <= 0);
                }
                break;
            case OperatorConstants.OPR_CODE.INCLUDE:
                bRet = subject.contains(baseline);
                break;
            case OperatorConstants.OPR_CODE.NOT_INCLUDE:
                bRet = !subject.contains(baseline);
                break;
            case OperatorConstants.OPR_CODE.INCLUDED_BY:
                bRet = baseline.contains(subject);
                break;
            case OperatorConstants.OPR_CODE.NOT_INCLUDED_BY:
                bRet = !baseline.contains(subject);
                break;
            case OperatorConstants.OPR_CODE.EQUAL_IGNORE_CASE:
                bRet = subject.equalsIgnoreCase(baseline);
                break;
            case OperatorConstants.OPR_CODE.NOT_EQUAL_IGNORE_CASE:
                bRet = !subject.equalsIgnoreCase(baseline);
                break;
            case OperatorConstants.OPR_CODE.MATCH:
                bRet = subject.matches(baseline);
                break;
            case OperatorConstants.OPR_CODE.UNMATCH:
                bRet = !subject.matches(baseline);
                break;
            case OperatorConstants.OPR_CODE.EXISTS:
                bRet = true;
                break;
            case OperatorConstants.OPR_CODE.EXISTS_FIELD:
                bRet = !subject.equals("unknown");
                break;
            default:
                //todo
                break;
        }
        return bRet;
    }
}
