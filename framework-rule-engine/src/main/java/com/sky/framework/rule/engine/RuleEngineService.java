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
package com.sky.framework.rule.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.framework.rule.engine.component.AbstractRuleItem;
import com.sky.framework.rule.engine.component.impl.ComplexRuleExecutor;
import com.sky.framework.rule.engine.component.impl.DefaultRuleExecutor;
import com.sky.framework.rule.engine.constant.OperatorConstants;
import com.sky.framework.rule.engine.enums.ResultEnum;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.EngineResultContext;
import com.sky.framework.rule.engine.model.ItemResult;
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

    private static List<RuleItem> itemList;

    static {
        itemList = new ArrayList<>();

        RuleItem ruleItem1 = new RuleItem();
        ruleItem1.setComparisonOperator(OperatorConstants.OPR_CODE.EQUAL);
        ruleItem1.setComparisonField("aaa");
        ruleItem1.setBaseline("gap");

        RuleItem ruleItem2 = new RuleItem();
        ruleItem2.setComparisonOperator(OperatorConstants.OPR_CODE.GREATER);
        ruleItem2.setComparisonField("bbb");
        ruleItem2.setBaseline("122");

        RuleItem ruleItem0 = new RuleItem();
        ruleItem0.setGroupExpress("( " + ruleItem1.getItemNo() + " && " + ruleItem2.getItemNo() + " )");

        ruleItem1.setParentItemNo(ruleItem0.getItemNo());
        ruleItem2.setParentItemNo(ruleItem0.getItemNo());

        //
        RuleItem ruleItem3 = new RuleItem();
        ruleItem3.setComparisonOperator(OperatorConstants.OPR_CODE.EQUAL);
        ruleItem3.setComparisonField("aaa");
        ruleItem3.setBaseline("gap");

        RuleItem ruleItem4 = new RuleItem();
        ruleItem4.setComparisonOperator(OperatorConstants.OPR_CODE.GREATER);
        ruleItem4.setComparisonField("bbb");
        ruleItem4.setBaseline("120");

        RuleItem ruleItem5 = new RuleItem();
        ruleItem5.setGroupExpress("( " + ruleItem3.getItemNo() + " && " + ruleItem4.getItemNo() + " )");

        ruleItem3.setParentItemNo(ruleItem5.getItemNo());
        ruleItem4.setParentItemNo(ruleItem5.getItemNo());

        RuleItem ruleItem6 = new RuleItem();
        ruleItem6.setGroupExpress("( " + ruleItem0.getItemNo() + " || " + ruleItem5.getItemNo() + " )");

        ruleItem0.setParentItemNo(ruleItem6.getItemNo());
        ruleItem5.setParentItemNo(ruleItem6.getItemNo());

        itemList.add(ruleItem2);
        itemList.add(ruleItem1);
        itemList.add(ruleItem3);
        itemList.add(ruleItem4);

        itemList.add(ruleItem0);
        itemList.add(ruleItem5);
        itemList.add(ruleItem6);
    }


    public static void main(String[] args) throws Exception {
        String source = "{'aaa':'gap','bbb':'121','ccc':'测试'}";
        JSONObject jsonObject = JSON.parseObject(source);
        RuleEngineService ruleEngineService = new RuleEngineService();
        EngineResultContext result = ruleEngineService.start(jsonObject, itemList);
        System.out.println(result.getResult());
    }

    /**
     * @param object
     * @return
     * @throws RuleEngineException
     */
    public EngineResultContext start(Object object, List<RuleItem> items) throws RuleEngineException {
        EngineResultContext runResult = new EngineResultContext();
        runResult.setRuleItems(items);
        runResult.setResult(ResultEnum.PASSED);
        List<RuleItem> itemList = this.filterItem(items, null);

        for (RuleItem item : itemList) {
            if (StringUtils.isNotEmpty(item.getGroupExpress())) {
                AbstractRuleItem executor = new ComplexRuleExecutor();
                executor.setObject(object);
                executor.setResultContext(runResult);
                ItemResult result = executor.doCheck(item);
                runResult.setResult(result.getResult());
                if (ResultEnum.PASSED.equals(result.getResult()) && !result.canBeContinue()) {
                    break;
                }
            } else {
                AbstractRuleItem executor = new DefaultRuleExecutor();
                executor.setObject(object);
                executor.setResultContext(runResult);
                ItemResult result = executor.doCheck(item);
                runResult.setResult(result.getResult());
                if (ResultEnum.PASSED.equals(result.getResult()) && !result.canBeContinue()) {
                    break;
                }
            }
        }
        return runResult;
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
