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


import lombok.Data;
import org.apache.commons.lang.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author
 */
@Data
public class RuleItem implements Serializable, Comparable {
    /**
     * 序号
     */
    private String itemNo;
    /**
     * 待比较字段
     */
    private String comparisonField;
    /**
     * 比较操作符
     */
    private String comparisonOperator;
    /**
     * 比较基线值
     */
    private List<String> baseline;
    /**
     * 执行结果
     */
    private String result;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 是否继续标识,默认是
     */
    private int continueFlag = 1;
    /**
     *
     */
    private String parentItemNo;
    /**
     * 组表达式
     */
    private String groupExpress;
    /**
     * 扩展字段
     */
    private String ext;

    @Override
    public int compareTo(Object obj) {
        return this.priority.compareTo(((RuleItem) obj).getPriority());
    }

    public String getItemNo() {
        if (this.groupExpress != null) {
            return replace(this.groupExpress);
        }
        return this.comparisonField + "##" + this.comparisonOperator + "##" + ObjectUtils.toString(this.baseline).replace(" ", "") + "##" + this.priority;
    }

    private String replace(String value) {
        return ObjectUtils.toString(value).replace("(", "").replace(")", "");
    }
}
