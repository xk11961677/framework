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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import com.sky.framework.rule.engine.constant.OperatorConstants;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.model.RuleEngineContext;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    protected RuleEngineContext ruleEngineContext;


    /**
     * 执行验证
     *
     * @param item
     * @return
     * @throws RuleEngineException
     */
    public abstract ItemResult doCheck(RuleItem item) throws RuleEngineException;

    public static boolean comparisonOperate(Object data, String comparisonOperator, List baseline, String key) {
        if (OperatorConstants.OPR_CODE.EXISTS.equals(comparisonOperator)) {
            return key == null ? false : true;
        }
        return comparisonOperate(data, comparisonOperator, baseline);
    }

    /**
     * 比较运算操作，将执行的结果和RuleItem中的baseline作比较。
     *
     * @param data               比较对象
     * @param comparisonOperator 比较操作符号，在OperatorConstants中定义。
     * @param baseline           比较基线，用于比较的对象。
     * @return 根据comparisonOperator运行的结果。 true or false。
     * @throws RuleEngineException 参数不合法，或者比较操作符不合法。
     */
    public static boolean comparisonOperate(Object data, String comparisonOperator, List baseline) {
        boolean bRet = false;
        if (null == baseline || null == comparisonOperator) {
            return false;
            //throw new RuleEngineException("null pointer error of subject or baseline or comparison code.");
        }
        BigDecimal bdSubject = null;
        BigDecimal object = null;
        String subject = null;

        List<Object> list = new ArrayList<>();
        list.add(data);
        if (data != null && data instanceof JSONArray) {
            list = ((JSONArray) data).toJavaList(Object.class);
        }
        List<String> dataListString = null;
        List<String> baselineListString = null;

        switch (comparisonOperator) {
            case OperatorConstants.OPR_CODE.EQUAL:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    try {
                        bdSubject = new BigDecimal(subject);
                        object = new BigDecimal(ObjectUtils.toString(baseline.get(0)));
                        bRet = (bdSubject.compareTo(object) == 0);
                    } catch (Exception e) {
                        bRet = subject.equals(ObjectUtils.toString(baseline.get(0)));
                    }
                    if (bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.GREATER:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    try {
                        bdSubject = new BigDecimal(subject);
                        object = new BigDecimal(ObjectUtils.toString(baseline.get(0)));
                        bRet = (bdSubject.compareTo(object) > 0);
                    } catch (Exception e1) {
                        bRet = (subject.compareTo(ObjectUtils.toString(baseline.get(0))) > 0);
                    }
                    if (bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.LESS:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    try {
                        bdSubject = new BigDecimal(subject);
                        object = new BigDecimal(ObjectUtils.toString(baseline.get(0)));
                        bRet = (bdSubject.compareTo(object) < 0);
                    } catch (Exception e1) {
                        bRet = (subject.compareTo(ObjectUtils.toString(baseline.get(0))) < 0);
                    }
                    if (bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.NOT_EQUAL:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    try {
                        bdSubject = new BigDecimal(subject);
                        object = new BigDecimal(ObjectUtils.toString(baseline.get(0)));
                        bRet = (bdSubject.compareTo(object) != 0);
                    } catch (Exception e) {
                        bRet = !subject.equals(ObjectUtils.toString(baseline.get(0)));
                    }
                    if (!bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.GREATER_EQUAL:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    try {
                        bdSubject = new BigDecimal(subject);
                        object = new BigDecimal(ObjectUtils.toString(baseline.get(0)));
                        bRet = (bdSubject.compareTo(object) >= 0);
                    } catch (Exception e1) {
                        bRet = (subject.compareTo(ObjectUtils.toString(baseline.get(0))) >= 0);
                    }
                    if (bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.LESS_EQUAL:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    try {
                        bdSubject = new BigDecimal(subject);
                        object = new BigDecimal(ObjectUtils.toString(baseline.get(0)));
                        bRet = (bdSubject.compareTo(object) <= 0);
                    } catch (Exception e1) {
                        bRet = (subject.compareTo(ObjectUtils.toString(baseline.get(0))) <= 0);
                    }
                    if (bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.INCLUDE:
                if (data == null) return false;
                dataListString = JSON.parseArray(JSON.toJSONString(list), String.class);
                baselineListString = JSON.parseArray(JSON.toJSONString(baseline), String.class);
                bRet = CollectionUtils.intersection(baselineListString, dataListString).size() == baselineListString.size();
                break;
            case OperatorConstants.OPR_CODE.NOT_INCLUDE:
                if (data == null) return true;
                dataListString = JSON.parseArray(JSON.toJSONString(list), String.class);
                baselineListString = JSON.parseArray(JSON.toJSONString(baseline), String.class);
                int all = dataListString.size() + baselineListString.size();
                bRet = CollectionUtils.disjunction(baselineListString, dataListString).size() == all;
                break;
            case OperatorConstants.OPR_CODE.IN:
                if (data == null) return false;
                dataListString = JSON.parseArray(JSON.toJSONString(list), String.class);
                baselineListString = JSON.parseArray(JSON.toJSONString(baseline), String.class);
                bRet = CollectionUtils.intersection(baselineListString, dataListString).size() > 0;
                break;
            case OperatorConstants.OPR_CODE.NIN:
                if (data == null) return true;
                dataListString = JSON.parseArray(JSON.toJSONString(list), String.class);
                baselineListString = JSON.parseArray(JSON.toJSONString(baseline), String.class);
                bRet = CollectionUtils.intersection(baselineListString, dataListString).size() == 0;
                break;
            case OperatorConstants.OPR_CODE.EQUAL_IGNORE_CASE:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    bRet = subject.equalsIgnoreCase(ObjectUtils.toString(baseline.get(0)));
                    if (bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.NOT_EQUAL_IGNORE_CASE:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    bRet = !subject.equalsIgnoreCase(ObjectUtils.toString(baseline.get(0)));
                    if (!bRet) {
                        break;
                    }
                }
                break;
            case OperatorConstants.OPR_CODE.MATCH:
                if (data == null) return false;
                for (Object value : list) {
                    subject = ObjectUtils.toString(value);
                    bRet = subject.matches(ObjectUtils.toString(baseline.get(0)));
                    if (bRet) {
                        break;
                    }
                }
                break;
            default:
                //todo
                break;
        }
        return bRet;
    }
}
