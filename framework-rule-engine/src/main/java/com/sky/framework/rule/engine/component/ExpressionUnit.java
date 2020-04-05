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

import lombok.Data;

import java.util.List;

/**
 * 组表达式单元
 *
 * @author
 */
@Data
public class ExpressionUnit {

    private ExpressionUnit left = null;
    private ExpressionUnit right = null;
    private String operator = null;
    private String name = null;
    private boolean value = false;

    public List<OperationUnit> leftSubList = null;
    public List<OperationUnit> rightSubList = null;

    /**
     * 计算 左右
     *
     * @return
     */
    public boolean calculate() {
        if ("!".equals(operator)) {
            value = !right.calculate();
        } else if ("&&".equals(operator)) {
            value = left.calculate() && right.calculate();
        } else if ("||".equals(operator)) {
            value = left.calculate() || right.calculate();
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        if (leftSubList != null) {
            ret.append("[");
            ret.append(leftSubList.toString());
            ret.append("] ");
        }
        ret.append(name);
        ret.append(":");
        ret.append(operator);
        if (rightSubList != null) {
            ret.append(" [");
            ret.append(rightSubList.toString());
            ret.append("]");
        }
        return ret.toString();
    }
}
