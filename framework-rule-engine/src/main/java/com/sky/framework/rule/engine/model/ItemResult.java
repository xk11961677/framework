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
package com.sky.framework.rule.engine.model;


import com.sky.framework.rule.engine.enums.ResultEnum;

/**
 * 执行结果,每个rule执行结果, 判断是否继续或跳出等
 *
 * @author
 */
public class ItemResult {
    /**
     * 是否继续
     */
    public static final int CONTINUE = 1;
    /**
     * 默认空结果
     */
    private ResultEnum result = ResultEnum.EMPTY;
    /**
     * 默认可以继续
     */
    private int continueFlag = CONTINUE;
    /**
     * 验证未通过的当前规则
     */
    private RuleItem item;

    public void setItem(RuleItem item) {
        this.item = item;
    }

    public RuleItem getItem() {
        return item;
    }

    public ResultEnum getResult() {
        return result;
    }

    public void setResult(ResultEnum result) {
        this.result = result;
    }

    public boolean canBeContinue() {
        return continueFlag == CONTINUE;
    }

    public void setContinue(int continueFlag) {
        this.continueFlag = continueFlag;
    }


    /**
     * 通过
     *
     * @param
     */
    public static ItemResult pass(RuleItem item) {
        ItemResult result = new ItemResult();
        result.setResult(ResultEnum.PASSED);
        result.setContinue(item.getContinueFlag());
        return result;
    }

    /**
     * 拒绝,设置拒绝的规则
     *
     * @return
     */
    public static ItemResult fail(RuleItem item) {
        ItemResult result = new ItemResult();
        result.setResult(ResultEnum.REJECTED);
        result.setContinue(item.getContinueFlag());
        result.setItem(item);
        return result;
    }
}

