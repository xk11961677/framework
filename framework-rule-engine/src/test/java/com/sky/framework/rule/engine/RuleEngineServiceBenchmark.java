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
import com.sky.framework.rule.engine.factory.RuleItemFactory;
import com.sky.framework.rule.engine.model.RuleItem;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 规则模块 基准测试
 *
 * @author
 */
@Warmup(iterations = 2)
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode({Mode.Throughput})
@State(Scope.Benchmark)
@Fork(3)
//@Threads(2)
//@GroupThreads(10)
//@Measurement(iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS)
@SuppressWarnings("all")
public class RuleEngineServiceBenchmark {//NOSONAR

    private JSONObject jsonObject;

    private static List<RuleItem> itemList;


    /**
     * 数据准备
     *
     * @throws Exception
     */
    @Setup
    public void prepare() throws Exception {
        String source = "{'aaa':['gap123'],'bbb':'119','ccc':'测试'}";
        JSONObject jsonObject = JSON.parseObject(source);
        itemList = RuleItemFactory.get();
    }

    /**
     * 资源回收
     */
    @TearDown
    public void shutdown() {

    }

    /**
     * 验证
     */
    @Benchmark
    public void runCheck() {
        RuleEngineService ruleEngineService = new RuleEngineService();
        ruleEngineService.start(jsonObject, itemList);
    }


    /**
     * 使用方式:
     * http://openjdk.java.net/projects/code-tools/jmh/
     * <p>
     * 通过下面地址可将result.json文件上传查看图
     * http://deepoove.com/jmh-visual-chart/
     */
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(RuleEngineServiceBenchmark.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }
}
