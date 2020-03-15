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
package com.sky.framework.rule.engine.enums;

/**
 * @author
 */
public enum ResultEnum {

    EMPTY(0), PASSED(1), CONCERNED(2), REJECTED(3), WAIT(4), SELFDEFINE(5);

    ResultEnum() {

    }

    private ResultEnum(int value) {
        this.setValue(value);
    }

    private int value = 0;

    public void setValue(int value) {
        switch (value) {
            case 1:
                defaultDesc = "PASSED";
                break;
            case 2:
                defaultDesc = "CONCERNED";
                break;
            case 3:
                defaultDesc = "REJECTED";
                break;
            case 4:
                defaultDesc = "WAIT";
                break;
            case 5:
                defaultDesc = "SELFDEFINE";
                break;
            default:
                break;
        }

        this.value = value;
    }

    public int compare(ResultEnum target) {
        return this.value - target.value;
    }

    public static ResultEnum valueOf(int value) {

        ResultEnum result = ResultEnum.EMPTY;
        result.setValue(value);
        return result;

    }

    /*
        @Override
        public int compareTo(RESULT o){
            return 0;
        }
    */
    public int getValue() {
        return value;
    }

    private String defaultDesc = "";

    public String getName() {
        return defaultDesc;
    }

    public void setName(String defaultDesc) {
        this.defaultDesc = defaultDesc;
        this.parse(defaultDesc);
    }

    public boolean parse(String value) {

        boolean bRet = true;
        value = value.toUpperCase();
        switch (value) {
            case "PASSED":
            case "RESULT.PASSED":
                this.value = 1;
                this.defaultDesc = "PASSED";
                break;
            case "CONCERNED":
            case "RESULT.CONCERNED":
                this.value = 2;
                this.defaultDesc = "CONCERNED";
                break;
            case "REJECTED":
            case "RESULT.REJECTED":
                this.value = 3;
                this.defaultDesc = "REJECTED";
                break;
            case "WAIT":
            case "RESULT.WAIT":
                this.value = 4;
                this.defaultDesc = "WAIT";
                break;
            case "SELFDEFINE":
            case "RESULT.SELFDEFINE":
                this.value = 5;
                this.defaultDesc = "SELFDEFINE";
                break;
            default:
                bRet = false;
                break;
        }

        return bRet;
    }

}