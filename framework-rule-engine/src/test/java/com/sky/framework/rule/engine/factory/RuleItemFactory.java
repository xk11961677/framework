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
package com.sky.framework.rule.engine.factory;


import com.sky.framework.rule.engine.constant.OperatorConstants;
import com.sky.framework.rule.engine.model.RuleItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 规则工厂
 */
public class RuleItemFactory {

    public static List<RuleItem> get() {
        List<RuleItem> itemList = new ArrayList<>();

        RuleItem ruleItem1 = new RuleItem();
        ruleItem1.setComparisonOperator(OperatorConstants.OPR_CODE.EQUAL);
        ruleItem1.setComparisonField("aaa");
        ruleItem1.setBaseline(Arrays.asList("gap7"));

        RuleItem ruleItem2 = new RuleItem();
        ruleItem2.setComparisonOperator(OperatorConstants.OPR_CODE.GREATER);
        ruleItem2.setComparisonField("bbb");
        ruleItem2.setBaseline(Arrays.asList("122"));

        RuleItem ruleItem0 = new RuleItem();
        ruleItem0.setGroupExpress("(" + ruleItem1.getItemNo() + "&&" + ruleItem2.getItemNo() + ")");

        ruleItem1.setParentItemNo(ruleItem0.getItemNo());
        ruleItem2.setParentItemNo(ruleItem0.getItemNo());

        //
        RuleItem ruleItem3 = new RuleItem();
        ruleItem3.setComparisonOperator(OperatorConstants.OPR_CODE.IN);
        //ruleItem3.setComparisonOperator(OperatorConstants.OPR_CODE.EQUAL);
        ruleItem3.setComparisonField("aaa");
        ruleItem3.setBaseline(Arrays.asList("gap", "gap456"));

        RuleItem ruleItem4 = new RuleItem();
        ruleItem4.setComparisonOperator(OperatorConstants.OPR_CODE.LESS);
        ruleItem4.setComparisonField("bbb");
        ruleItem4.setBaseline(Arrays.asList("120"));

        RuleItem ruleItem5 = new RuleItem();
        ruleItem5.setGroupExpress("(" + ruleItem3.getItemNo() + "&&" + ruleItem4.getItemNo() + ")");

        ruleItem3.setParentItemNo(ruleItem5.getItemNo());
        ruleItem4.setParentItemNo(ruleItem5.getItemNo());

        RuleItem ruleItem6 = new RuleItem();
        ruleItem6.setGroupExpress("(" + ruleItem0.getItemNo() + "||" + ruleItem5.getItemNo() + ")");

        // 1 && 2 需要注释掉
        ruleItem0.setParentItemNo(ruleItem6.getItemNo());
        ruleItem5.setParentItemNo(ruleItem6.getItemNo());

        itemList.add(ruleItem2);
        itemList.add(ruleItem1);
        // 1 && 2 需要注释掉
        itemList.add(ruleItem3);
        // 1 && 2 需要注释掉
        itemList.add(ruleItem4);

        itemList.add(ruleItem0);
        // 1 && 2 需要注释掉
        itemList.add(ruleItem5);
        // 1 && 2 需要注释掉
        itemList.add(ruleItem6);

        return itemList;
    }
}
