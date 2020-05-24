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

import com.sky.framework.rule.engine.component.ExpressionUnit;
import com.sky.framework.rule.engine.component.OperationUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore("javax.management.*")
public class ComplexRuleExecutorTest {

    @Test(expected = NullPointerException.class)
    public void test() {
        ComplexRuleExecutor t = new ComplexRuleExecutor();
        String express = "";
        express = "True1 || !(False2 && ( True3 || False4 && True5) && !(False6 || False7 && ( True8 || !True9 )) || False10)";
        //express = "True1 || True2 || True3 || True4 && True5 && True6 && !True7 && True8 || True9";

        List<OperationUnit> stack = t.parseExpress(express);

        ExpressionUnit root;
        root = t.breakExpress(stack, null);
        System.out.println(root.calculate());

    }
}
