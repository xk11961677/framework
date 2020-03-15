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

import com.sky.framework.rule.engine.RuleEngineService;
import com.sky.framework.rule.engine.component.AbstractRuleItem;
import com.sky.framework.rule.engine.component.ExpressionUnit;
import com.sky.framework.rule.engine.component.OperationUnit;
import com.sky.framework.rule.engine.enums.TypeEnum;
import com.sky.framework.rule.engine.exception.RuleEngineException;
import com.sky.framework.rule.engine.model.ItemResult;
import com.sky.framework.rule.engine.enums.ResultEnum;
import com.sky.framework.rule.engine.model.RuleItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Slf4j
public class ComplexRuleExecutor extends AbstractRuleItem {

    /**
     * 解析出各个独立的单元。
     *
     * @param express
     * @return
     */
    private List<OperationUnit> parseExpress(String express) {

        List<OperationUnit> theStack = new ArrayList<>();
        StringBuffer element = new StringBuffer();
        int level = 0;
        OperationUnit unit = null;

        for (int iLoop = 0; iLoop < express.length(); iLoop++) {
            char alphabet = express.charAt(iLoop);

            switch (alphabet) {
                case '(':            //单字节运算符
                    if (element.length() > 0) {
                        unit = new OperationUnit();
                        unit.element = element.toString();
                        unit.type = TypeEnum.VARIABLE;
                        unit.level = level;
                        theStack.add(unit);
                    }
                    unit = new OperationUnit();
                    unit.element = String.valueOf(alphabet);
                    unit.type = TypeEnum.LEFT_BRACKET;
                    level++;            // ( 本身也属于下个level.
                    unit.level = level;
                    theStack.add(unit);
                    element = new StringBuffer();
                    break;
                case ')':
                    if (element.length() > 0) {
                        unit = new OperationUnit();
                        unit.element = element.toString();
                        unit.type = TypeEnum.VARIABLE;
                        unit.level = level;
                        theStack.add(unit);
                    }
                    unit = new OperationUnit();
                    unit.element = String.valueOf(alphabet);
                    unit.type = TypeEnum.RIGHT_BRACKET;
                    unit.level = level;
                    theStack.add(unit);
                    level--;                    //// ) 本身也属于上一个level.
                    element = new StringBuffer();
                    break;
                case '!':
                    if (element.length() > 0) {
                        unit = new OperationUnit();
                        unit.element = element.toString();
                        unit.type = TypeEnum.VARIABLE;
                        unit.level = level;
                        theStack.add(unit);
                    }
                    unit = new OperationUnit();
                    unit.element = String.valueOf(alphabet);
                    unit.type = TypeEnum.MONOCULAR;
                    unit.level = level;
                    theStack.add(unit);
                    element = new StringBuffer();
                    break;
                case '&':
                case '|':
                    //如果是2个||或者是2个&&，那么就是逻辑运算符
                    if (express.length() >= iLoop + 1 && express.charAt(iLoop + 1) == alphabet) {
                        if (element.length() > 0) {
                            unit = new OperationUnit();
                            unit.element = element.toString();
                            unit.type = TypeEnum.VARIABLE;
                            unit.level = level;
                            theStack.add(unit);
                        }
                        unit = new OperationUnit();
                        unit.element = String.valueOf(alphabet) + String.valueOf(alphabet);
                        unit.type = TypeEnum.BINOCULAR;
                        unit.level = level;
                        theStack.add(unit);
                        iLoop++;
                        element = new StringBuffer();
                    } else {
                        element.append(alphabet);
                    }
                    break;
                case ' ':        //去除空格。
                case '\n':        //去除换行。
                case '\r':        //去除回车。
                    break;
                default:
                    element.append(alphabet);
                    break;
            }

        }

        //扫尾的字符串也要添加进去。
        if (element.length() > 0) {
            String end = element.toString();
            if (")".equals(end)) {
                unit = new OperationUnit();
                unit.element = end;
                unit.type = TypeEnum.RIGHT_BRACKET;
                //必须是1
                unit.level = 1;
                theStack.add(unit);
            } else if ("!".equals(end) || "(".equals(end) || "|".equals(end) || "&".equals(end)) {
                System.err.println("结尾的字符不能是关键字。");
            } else {
                //变量。
                unit = new OperationUnit();
                unit.element = end;
                unit.type = TypeEnum.VARIABLE;
                //必须是0.
                unit.level = 0;
                theStack.add(unit);
            }
        }
        return theStack;
    }

    /**
     * 去除前后端无用的括号。
     *
     * @param list
     * @param minLevel -- 如果是-1，那么需要重新累计level值。
     * @return
     */
    private List<OperationUnit> trimExpress(List<OperationUnit> list, int minLevel) {
        //无括号表达式。
        if (list.size() <= 1) {
            return list;
        }
        //开头不是左括号
        if (list.get(0).type != TypeEnum.LEFT_BRACKET) {
            return list;
        }
        //开头是左括号，但是结尾不是右括号。
        if (list.get(0).type == TypeEnum.LEFT_BRACKET && list.get(list.size() - 1).type != TypeEnum.RIGHT_BRACKET) {
            return list;
        }
        //最少括号数目是0，就是中间有非括号的情况 () + ()。
        if (minLevel == 0) {
            return list;
        }
        //未知的最小括号数目，重新取得。
        if (minLevel < 0) {
            int tempLevel = Integer.MAX_VALUE;
            for (OperationUnit unit : list) {
                if (tempLevel > unit.level) {
                    tempLevel = unit.level;
                }
            }
            minLevel = tempLevel;
        }
        //最少括号数目是0，就是中间有非括号的情况 () + ()。
        if (minLevel <= 0) {
            return list;
        }
        //依次拷贝到去除多余括号的数组中去。
        List<OperationUnit> newList = new ArrayList<OperationUnit>();
        for (int iLoop = 0; iLoop < list.size(); iLoop++) {
            OperationUnit unit = list.get(iLoop);
            if (iLoop < minLevel) {
                //非标准括号，可能是表达式不合格。
                if (unit.type != TypeEnum.LEFT_BRACKET) {
                    System.err.println("括号个数不匹配。");
                    //throw new Exception("括号个数不匹配。");
                }
            } else if (iLoop >= list.size() - minLevel) {
                if (unit.type != TypeEnum.RIGHT_BRACKET) {
                    //throw new Exception("括号个数不匹配。");
                    System.err.println("括号个数不匹配。");
                }
            } else {
                unit.level = unit.level - minLevel;
                newList.add(unit);
            }
        }
        return newList;
    }

    private ExpressionUnit breakExpress(List<OperationUnit> list, ExpressionUnit current) throws RuleEngineException {
        list = trimExpress(list, -1);
        ExpressionUnit root = current;
        if (root == null) {
            root = new ExpressionUnit();
        }
        int firstOr = -1, firstAnd = -1, firstNot = -1;
        for (int iLoop = 0; iLoop < list.size(); iLoop++) {
            OperationUnit unit = list.get(iLoop);
            //取得括号外的内容。括号中的内容不作为划分的信息。
            if (unit.level == 0) {
                //双目运算符
                if (unit.type == TypeEnum.BINOCULAR) {
                    if (firstOr == -1 && "||".equals(unit.element)) {
                        firstOr = iLoop;
                        break;
                    }
                    if (firstAnd == -1 && "&&".equals(unit.element)) {
                        firstAnd = iLoop;
                    }
                } else if (unit.type == TypeEnum.MONOCULAR) {
                    if (firstNot == -1 && "!".equals(unit.element)) {
                        firstNot = iLoop;
                    }
                }
            }
        }

        if (firstOr > 0) {
            root.setOperator(list.get(firstOr).element);
            root.setName("OR");
            root.leftSubList = this.trimExpress(list.subList(0, firstOr), -1);
            root.setLeft(breakExpress(root.leftSubList, root.getLeft()));

            root.rightSubList = this.trimExpress(list.subList(firstOr + 1, list.size()), -1);
            root.setRight(breakExpress(root.rightSubList, root.getRight()));

            root.calculate();
        } else if (firstAnd > 0) {
            root.setOperator(list.get(firstAnd).element);
            root.setName("AND");
            root.leftSubList = this.trimExpress(list.subList(0, firstAnd), -1);
            root.setLeft(breakExpress(root.leftSubList, root.getLeft()));

            root.rightSubList = this.trimExpress(list.subList(firstAnd + 1, list.size()), -1);
            root.setRight(breakExpress(root.rightSubList, root.getRight()));

            root.calculate();
        } else if (firstNot >= 0) {
            root.setOperator(list.get(firstNot).element);
            root.setName("NOT");
            root.leftSubList = null;
            root.rightSubList = this.trimExpress(list.subList(firstNot + 1, list.size()), -1);
            root.setRight(breakExpress(root.rightSubList, root.getRight()));

            root.calculate();
            //不带运算符的情况。
        } else if (current == null && list.size() > 0) {

            if (list.get(0).type == TypeEnum.VARIABLE) {
                root.setOperator(list.get(0).element);
                root.setName("VARIABLE");

                //todo 获取规则
                System.out.println(root.getOperator());
                RuleItem item = RuleEngineService.groupMap.get(root.getOperator());
                try {
                    root.setValue(calculate(item, this.object));
                } catch (RuleEngineException e) {
                    log.error(":{}", e.getMessage());
                    throw e;
                }
            }
        }
        return root;
    }


    @Override
    public ItemResult doCheck(RuleItem item) throws RuleEngineException {
        ItemResult result = new ItemResult();
        if (StringUtils.isNotEmpty(item.getGroupExpress())) {
            List<OperationUnit> stack = parseExpress(item.getGroupExpress());
            ExpressionUnit root = breakExpress(stack, null);
            boolean bRet = root.calculate();

            //缺省认为是 passed
            result.pass(bRet);

            if (bRet) {
                result.setResult(StringUtils.isBlank(item.getResult()) ? "1" : item.getResult());
                result.setRemark(result.getResult().getName());
                String continueFlag = StringUtils.isBlank(item.getContinueFlag()) ? "1" : item.getContinueFlag();
                result.setContinue(Integer.parseInt(continueFlag));
            } else {
                // add false result return.
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static boolean calculate(RuleItem item, Object object) throws RuleEngineException {
        AbstractRuleItem auditInstance = new DefaultRuleExecutor();
        auditInstance.setObject(object);
        ItemResult result = auditInstance.doCheck(item);
        if (null != result) {
            return (result.getResult() == ResultEnum.PASSED);
        } else {
            throw new RuleEngineException("do check returns NPE");
        }
    }

    public static void main(String[] args) {
        ComplexRuleExecutor t = new ComplexRuleExecutor();
        String express = "";
        express = "True1 || !(False2 && ( True3 || False4 && True5) && !(False6 || False7 && ( True8 || !True9 )) || False10)";
        //express = "True1 || True2 || True3 || True4 && True5 && True6 && !True7 && True8 || True9";

        List<OperationUnit> stack = t.parseExpress(express);
        System.out.println(stack.toString());
        ExpressionUnit root;
        try {
            root = t.breakExpress(stack, null);
            System.out.println(root.calculate());
        } catch (RuleEngineException e) {
            e.printStackTrace();
        }
    }
}
