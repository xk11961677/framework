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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 整体执行结果
 *
 * @author
 */
@Slf4j
public class EngineResultContext {

    private ResultEnum result = ResultEnum.EMPTY;

    /**
     * 规则容器
     */
    @Setter
    @Getter
    private List<RuleItem> ruleItems;

    public void setRuleItems(List<RuleItem> ruleItems) {
        this.ruleItems = ruleItems;
        ruleItems.forEach(r -> map.put(r.getItemNo(), r));
        for(Map.Entry entry : map.entrySet()) {
            System.out.println(entry.getKey());
        }

    }

    /**
     * ruleId :  ruleItem
     */
    @Getter
    private Map<String, RuleItem> map = new HashMap<>();

    public ResultEnum getResult() {
        return result;
    }

    public void setResult(ResultEnum result) {
        this.result = result;
    }

    /*public void setResult(String result) {
        boolean bRet = this.result.parse(result);
        if (!bRet) {
            try {
                this.result.setValue(Integer.parseInt(result));
            } catch (NumberFormatException e) {
                log.error(":{}", e.getMessage());
            }
        }
    }

    public void setResult(int result) {
        this.result.setValue(result);
    }*/
}
