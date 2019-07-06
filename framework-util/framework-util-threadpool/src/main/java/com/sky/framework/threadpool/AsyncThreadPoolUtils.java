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