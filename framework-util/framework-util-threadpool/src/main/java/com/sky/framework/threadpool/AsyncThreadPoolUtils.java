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
package com.sky.framework.threadpool;


/**
 * Description: 辅助工具类,平均分配任务数
 *
 * @author
 */
public class AsyncThreadPoolUtils {

    public static final int cpuNum = Runtime.getRuntime().availableProcessors();

    /**
     * Description: 平均分配任务数
     *
     * @param threadNum
     * @param taskNum
     * @param maxNumOfOneThread
     * @return
     */
    public static int[] assignTaskNumber(int threadNum, int taskNum, int maxNumOfOneThread) {
        int[] taskNums = new int[threadNum];
        int numOfSingle = taskNum / threadNum;
        int otherNum = taskNum % threadNum;
        if (maxNumOfOneThread > 0 && numOfSingle >= maxNumOfOneThread) {
            numOfSingle = maxNumOfOneThread;
            otherNum = 0;
        }
        for (int i = 0; i < taskNums.length; i++) {
            if (i < otherNum) {
                taskNums[i] = numOfSingle + 1;
            } else {
                taskNums[i] = numOfSingle;
            }
        }
        return taskNums;
    }


}