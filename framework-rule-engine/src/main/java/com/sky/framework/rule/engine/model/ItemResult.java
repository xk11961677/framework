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
package com.sky.framework.rule.engine.model;

import com.sky.framework.rule.engine.enums.ResultEnum;

/**
 * 执行结果,每个rule执行结果, 判断是否继续或跳出等
 *
 * @author
 */
public class ItemResult {

    //整体EngineResultContext是否继续
    public static final int CONTINUE = 1;

    //是否继续循环执行自己
    public static final int LOOP = 2;

    //
    public static final int BROKEN = 3;

    private ResultEnum result = ResultEnum.EMPTY;
    private String remark;

    private boolean returnValue;

    public boolean getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(boolean returnValue) {
        this.returnValue = returnValue;
    }

    //默认可以继续
    private int continueFlag = CONTINUE;

    public ResultEnum getResult() {
        return result;
    }

    public void setResult(ResultEnum result) {
        this.result = result;
        if (this.result == ResultEnum.WAIT) {
            continueFlag = BROKEN;
        }
        //continueFlag =  ( this.result != RESULT.WAIT);		//非中断状态
    }

    public void setResult(String result) {
        int iResult = Integer.parseInt(result);
        this.setResult(iResult);
    }

    public void setResult(int result) {
        this.result.setValue(result);
        if (this.result == ResultEnum.WAIT) {
            continueFlag = BROKEN;
        }
        //continueFlag =  ( this.result != RESULT.WAIT);		//非中断状态
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean canBeContinue() {
        return continueFlag == CONTINUE;
    }

    public void setContinue(int continueFlag) {
        this.continueFlag = continueFlag;
    }

    public boolean shouldLoop() {
        return continueFlag == LOOP;
    }

    /**
     * 默认通过
     *
     * @param bRet
     */
    public void pass(boolean bRet) {
        this.setResult(ResultEnum.PASSED);
        this.setRemark(ResultEnum.PASSED.getName());
        this.setContinue(ItemResult.CONTINUE);
        this.setReturnValue(bRet);
    }
}

