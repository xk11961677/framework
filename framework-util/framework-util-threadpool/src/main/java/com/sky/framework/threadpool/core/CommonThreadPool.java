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
package com.sky.framework.threadpool.core;

import com.sky.framework.threadpool.AsyncThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步执行公共线程池
 *
 * @author
 */
@Slf4j
public final class CommonThreadPool {

    private static ExecutorService executorService;

    private static final String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final long EXECUTE_TIME = 10000L;

    private CommonThreadPool() {

    }

    /**
     * 异步执行公共执行方法
     *
     * @param command
     * @return future, 返回异步等待对象
     * @since
     */
    public static Future<Object> execute(IAsynchronousHandler command) {
        return executorService.submit(new ThreadPoolAdaptor(command, EXECUTE_TIME));
    }

    /**
     * 关闭线程池
     *
     * @return boolean
     * @since
     */
    public static boolean shutdown() {
        if (executorService != null) {
            executorService.shutdown();
            return true;
        }
        return false;
    }

    /**
     * 优雅关闭线程池
     *
     * @param timeout
     * @param unit
     */
    public static void shutdownGracefully(long timeout, TimeUnit unit) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout, unit)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(timeout, unit)) {
                    log.warn(String.format("%s didn't terminate!", executorService));
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 初始化线程池
     *
     * @param vo
     * @since
     */
    public static ThreadPoolExecutorExtend init(AsyncThreadPoolProperties vo) {
        int corePoolSize = vo.getCorePoolSize();
        int maximumPoolSize = vo.getMaximumPoolSize();
        int initialCapacity = vo.getInitialCapacity();
        long keepAliveTime = vo.getKeepAliveTime();
        String threadName = vo.getThreadName();
        //增加构造队列容量参数
        TaskQueue taskqueue = new TaskQueue(initialCapacity, vo.isDiscard());
        ThreadPoolExecutorExtend executeNew = new ThreadPoolExecutorExtend(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                taskqueue, new TaskThreadFactory(threadName), new ThreadPoolRejectedExecutionHandler(vo.isDiscard()));

        taskqueue.setParent(executeNew);

        executorService = executeNew;
        return executeNew;
    }

    /**
     * 获取线程池
     *
     * @return
     */
    public static ThreadPoolExecutorExtend getThreadPool() {
        return (ThreadPoolExecutorExtend) executorService;
    }

    /**
     * 是否大于内存限制的阀值
     *
     * @return
     */
    public static boolean isMemoryThreshold() {
        long size = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        long thresholdSize = (long) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() * 0.7);
        if (size > thresholdSize) {
            return true;
        }
        return false;
    }

    /**
     * 线程工厂
     */
    static class TaskThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        TaskThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
            //t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * 自定义线程创建方法
     * 扩展内存检测
     * 更严格控制是否开启 最大线程数限定数线程
     * 由优先使用队列转变为优先使用最大线程值
     */
    static class TaskQueue extends LinkedBlockingQueue<Runnable> {
        /**
         *
         */
        private static final long serialVersionUID = -3966913824895982184L;

        ThreadPoolExecutorExtend parent = null;

        boolean isDiscard = true;

        public TaskQueue() {
            super();
        }

        public TaskQueue(int initialCapacity) {
            super(initialCapacity);
        }

        public TaskQueue(int initialCapacity, boolean isDiscard) {
            super(initialCapacity);
            this.isDiscard = isDiscard;
        }

        public TaskQueue(Collection<? extends Runnable> c) {
            super(c);
        }

        public void setParent(ThreadPoolExecutorExtend tp) {
            parent = tp;
        }

        public boolean force(Runnable o) {
            if (parent.isShutdown()) {
                throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
            }
            //forces the item onto the queue, to be used if the task is rejected
            return super.offer(o);
        }

        @Override
        public boolean offer(Runnable o) {
            //we can't do any checks
            if (parent == null) {
                return super.offer(o);
            }

            //内存限制
            if (this.isDiscard && isMemoryThreshold()) {
                return false;
            }

            //we are maxed out on threads, simply queue the object
            if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
                return super.offer(o);
            }
            //we have idle threads, just add it to the queue
            //note that we don't use getActiveCount(), see BZ 49730
            AtomicInteger submittedTasksCountNew = parent.submittedTasksCount;
            if (submittedTasksCountNew != null && submittedTasksCountNew.get() <= parent.getPoolSize()) {
                return super.offer(o);
            }
            //if we have less threads than maximum force creation of a new thread
            if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
                return false;
            }

            //if we reached here, we need to add it to the queue
            return super.offer(o);
        }
    }

    /**
     * 自定义线程池拒绝策略实现
     * 队列达到上限则打印详细日志
     * 轻量级管理线程池数量
     */
    static class ThreadPoolRejectedExecutionHandler implements RejectedExecutionHandler {
        //默认丢弃
        private boolean isDiscard = true;

        public ThreadPoolRejectedExecutionHandler() {
        }

        public ThreadPoolRejectedExecutionHandler(boolean isDiscard) {
            this.isDiscard = isDiscard;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            //设置不丢弃 或者 没到内存阀值, 尝试重新添加到队列(如果添加成功则返回,添加失败需要打印日志、抛异常)
            if (!isDiscard || !isMemoryThreshold()) {
                try {
                    boolean reAdd = executor.getQueue().offer(r, 3, TimeUnit.MILLISECONDS);
                    if (reAdd) {
                        return;
                    }
                } catch (InterruptedException e) {
                    log.error("线程被中断:{}", e.getMessage(), e);
                }
            }

            Object print = r;
            if (r instanceof CommonFutureTask) {
                IAsynchronousHandler handlerAdaptor = ((CommonFutureTask) r).getR();
                if (handlerAdaptor == null) {
                    log.error("CommonThreadPool 以达到队列容量上限：" + r.toString());
                    throw new RejectedExecutionException();
                }
                //获取真实的handler ，记录日志
                IAsynchronousHandler handler;
                if (handlerAdaptor instanceof ThreadPoolAdaptor) {
                    handler = ((ThreadPoolAdaptor) handlerAdaptor).getHandler();
                    if (handler == null) {
                        handler = handlerAdaptor;
                    }
                } else {
                    handler = handlerAdaptor;
                }
                print = handler;
            }

            printLog(print);
            decrement(executor);
            throw new RejectedExecutionException();
        }

        /**
         * 自定义线程池，执行数量管理递减
         *
         * @param executor
         */
        private void decrement(ThreadPoolExecutor executor) {
            if (executor instanceof ThreadPoolExecutorExtend) {
                ((ThreadPoolExecutorExtend) executor).getSubmittedTasksCount().decrementAndGet();
            }
        }

        /**
         * 打印日志
         *
         * @param r
         */
        private void printLog(Object r) {
            StringBuilder sb = new StringBuilder();
            sb.append("任务名称:").append(r.getClass());
            sb.append("。happenTime=").append(formatDate());
            sb.append("。toString=").append(r.toString());
            log.error("CommonThreadPool 以达到队列容量上限：" + sb.toString());
        }

        private String formatDate() {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(LONG_FORMAT);
            String result = sdf.format(date);
            return result;
        }
    }
}
