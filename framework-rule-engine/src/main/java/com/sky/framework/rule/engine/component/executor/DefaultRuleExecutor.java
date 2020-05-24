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
package com.sky.framework.rule.engine.component.executor;


import com.jayway.jsonpath.JsonPath;
import com.sky.framework.rule.engine.component.AbstractRuleItem;
import com.sky.framework.rule.engine.constant.OperatorConstants;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.extern.slf4j.Slf4j;


/**
 * 默认单一规则执行器
 *
 * @author
 */
@Slf4j
public class DefaultRuleExecutor extends AbstractRuleItem {

    /**
     * @param item
     * @return
     * @throws RuleEngineException
     */
    @Override
    public ItemResult doCheck(RuleItem item) throws RuleEngineException {
        Object source = getObject();
        //根据source 与 comparisonValue 获取数据的当前值 (需要符合json-path格式)
        Object subject = getValue(item.getComparisonField(), source);
        //执行操作表达式比较,返回结果
        boolean bRet = comparisonOperate(subject, item.getComparisonOperator(), item.getBaseline());
        return bRet ? ItemResult.pass(item) : ItemResult.fail(item);
    }


    /**
     * 替换路径,并提取值,如果不报错则证明数据变化,需发送
     *
     * @param path
     * @param data
     * @return
     */
    protected Object getValue(String path, Object data) {
        try {
            Object read = JsonPath.read(data, "$." + path);
            return read;
        } catch (Exception e) {
            //证明字段不存在
            return OperatorConstants.UNKNOWN;
        }
    }
}
