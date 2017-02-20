package com.chen.cache.executor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.command.Handler;
public class NonOrderedQueuePoolExecutor extends ThreadPoolExecutor
{
	private Logger log = LogManager.getLogger(NonOrderedQueuePoolExecutor.class);
	public NonOrderedQueuePoolExecutor(int corePoolSize)
	{
		super(corePoolSize, corePoolSize, 30L, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
	}
	public void execute(Handler command)
	{
		Work work = new Work(command);
		execute(work);
	}
	private class Work implements Runnable
	{
		private Handler command;
		public Work(Handler handler)
		{
			this.command = handler;
		}
		public void run()
		{
			long start = System.currentTimeMillis();
			this.command.action();
			long end = System.currentTimeMillis();
			if (end - start > 50L)
			{
				log.error("NonOrderedQueuePoolExecutor-->"+this.command.getClass().getSimpleName()+"Run:"+(end - start));
			}
		}
	}
}
