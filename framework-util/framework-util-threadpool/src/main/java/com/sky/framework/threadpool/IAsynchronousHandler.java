package com.sky.framework.threadpool;

import java.util.concurrent.Callable;


/**
 * Description:
 * @author
 */
public interface IAsynchronousHandler extends Callable<Object>{
	
	/**
	 * 执行完成后，调用的方法
	 * 即在runnable()方法执行完成后在执行executeAfter()
	 * Description: t - 导致终止的异常；如果执行正常结束，则为 null
	 */
	 void executeAfter(Throwable t);
	/**
	 * 执行完成后前，调用的方法
	 * 即在runnable()方法执行前在执行executeBefore()
	 * t - 将运行任务 r 的线程。
	 */
	 void executeBefore(Thread t);

}
