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
package com.sky.framework.rule.engine.component.impl;


import com.alibaba.fastjson.JSONObject;
import com.sky.framework.rule.engine.component.AbstractRuleItem;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;


/**
 * 默认单一规则执行器
 *
 * @author
 */
@Slf4j
public class DefaultRuleExecutor extends AbstractRuleItem {

    /**
     * todo 计算规则是否匹配,并返回结果
     *
     * @param item
     * @return
     * @throws RuleEngineException
     */
    @Override
    public ItemResult doCheck(RuleItem item) throws RuleEngineException {

        Object source = getObject();

        //根据source 与 comparisonValue 获取数据的当前值
        String subject = ((JSONObject) source).getString(item.getComparisonValue());

        //执行操作表达式比较,返回结果
        boolean bRet = comparisonOperate(subject, item.getComparisonCode(), item.getBaseline());

        ItemResult checkResult = new ItemResult();
        checkResult.pass(bRet);
        if (bRet) {
            checkResult.setResult(StringUtils.isBlank(item.getResult()) ? "1" : item.getResult());
            checkResult.setRemark(checkResult.getResult().getName());
            String continueFlag = StringUtils.isBlank(item.getContinueFlag()) ? "1" : item.getContinueFlag();
            checkResult.setContinue(Integer.parseInt(continueFlag));
        } else {
            // add false result return.
        }
        return checkResult;
    }
}
