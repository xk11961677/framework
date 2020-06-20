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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.framework.rule.engine.enums.ResultEnum;
import com.sky.framework.rule.engine.factory.RuleItemFactory;
import com.sky.framework.rule.engine.model.RuleEngineContext;
import com.sky.framework.rule.engine.model.RuleItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

/**
 * DefaultRuleExecutor.getValue 使用的jsonPath
 *
 * @author
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class RuleEngineServiceTest {

    private static List<RuleItem> itemList;

    @Before
    public void before() {
        itemList = RuleItemFactory.get();
    }


    @Test
    public void test_01() {
        String source = "{'aaa':['gap123'],'bbb':'119','ccc':'测试'}";
        JSONObject jsonObject = JSON.parseObject(source);
        RuleEngineService ruleEngineService = new RuleEngineService();
        RuleEngineContext result = ruleEngineService.start(jsonObject, itemList);
        Assert.assertEquals(ResultEnum.REJECTED, result.getResult());
    }

    @Test
    public void test_02() {
        String source = "{'aaa':['gap7','gap123','gap456'],'bbb':'123','ccc':'测试'}";
        JSONObject jsonObject = JSON.parseObject(source);
        RuleEngineService ruleEngineService = new RuleEngineService();
        RuleEngineContext result = ruleEngineService.start(jsonObject, itemList);
        Assert.assertEquals(ResultEnum.PASSED, result.getResult());
    }
}
