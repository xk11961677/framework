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
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.EngineResultContext;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.enums.ResultEnum;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
@Slf4j
public class RuleEngineService {

    private static List<RuleItem> itemList;

    public static Map<String,RuleItem> groupMap = new HashMap<>();

    static {
        itemList = new ArrayList<>();

        RuleItem ruleItem0 = new RuleItem();
        ruleItem0.setGroupExpress("( 1 || 2 )");
        itemList.add(ruleItem0);

        RuleItem ruleItem1 = new RuleItem();
        ruleItem1.setItemNo("1");
        ruleItem1.setComparisonCode(OperatorConstants.OPR_CODE.EQUAL);
        ruleItem1.setComparisonValue("aaa");
        ruleItem1.setBaseline("gap");
        itemList.add(ruleItem1);

        RuleItem ruleItem2 = new RuleItem();
        ruleItem2.setItemNo("2");
        ruleItem2.setComparisonCode(OperatorConstants.OPR_CODE.GREATER);
        ruleItem2.setComparisonValue("bbb");
        ruleItem2.setBaseline("122");
        itemList.add(ruleItem2);


        groupMap.put(ruleItem1.getItemNo(),ruleItem1);

        groupMap.put(ruleItem2.getItemNo(),ruleItem2);
    }


    public static void main(String[] args) throws Exception {
        String source = "{'aaa':'gap','bbb':'123','ccc':'测试'}";
        JSONObject jsonObject = JSON.parseObject(source);
        RuleEngineService ruleEngineService = new RuleEngineService();
        EngineResultContext result = ruleEngineService.start(jsonObject);
        System.out.println(result.getResult());
    }

    /**
     * @param object
     * @return
     * @throws RuleEngineException
     */
    public EngineResultContext start(Object object) throws RuleEngineException {
        List<RuleItem> itemList = RuleEngineService.itemList;
        EngineResultContext runResult = new EngineResultContext();
        runResult.setResult(ResultEnum.PASSED);
        runResult.setResultDesc("PASSED");

        for (RuleItem item : itemList) {
            if (StringUtils.isNotEmpty(item.getGroupExpress())) {
                ComplexRuleExecutor executor = new ComplexRuleExecutor();
                executor.setObject(object);
                ItemResult result = null;
                do {
                    result = executor.doCheck(item);
                    if (result == null) {
                        break;
                    }
                    if (result.getResult().compare(runResult.getResult()) > 0) {
                        runResult.setResult(result.getResult());
                        runResult.setResultDesc(result.getRemark());
                    }
                    if (!result.canBeContinue()) {
                        break;
                    }
                } while (result != null && result.shouldLoop());

                if (!result.canBeContinue()) {
                    break;
                }
            } else {
                AbstractRuleItem auditInstance = new DefaultRuleExecutor();
                auditInstance.setObject(object);
                ItemResult result = null;
                do {
                    result = auditInstance.doCheck(item);
                    if (result == null) {
                        break;
                    }
                    if (result.getResult().compare(runResult.getResult()) > 0) {
                        runResult.setResult(result.getResult());
                        runResult.setResultDesc(result.getRemark());
                    }
                    if (!result.canBeContinue()) {
                        break;
                    }
                } while (result != null && result.shouldLoop());

                if (!result.canBeContinue()) {
                    break;
                }
            }
        }
        this.after(object);
        return runResult;
    }

    private void after(Object object) {
        //todo 将消息过滤，并发送
    }
}
