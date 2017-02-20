package com.chen.cache.executor;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.cache.structs.AbstractWork;
import com.chen.cache.structs.OrderedQueuePool;
import com.chen.cache.structs.TasksQueue;

public class OrderedQueuePoolExecutor extends ThreadPoolExecutor
{
	protected static Logger log = LogManager.getLogger(OrderedQueuePoolExecutor.class);
	private OrderedQueuePool<Long, AbstractWork> pool = new OrderedQueuePool<Long, AbstractWork>();
	private String name;
	private int corePoolSize;
	private int maxQueueSize;
	
	public OrderedQueuePoolExecutor(String name, int corePoolSize, int maxQueueSize)
	{
		super(corePoolSize, 2*corePoolSize, 30L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		this.name = name;
		this.corePoolSize = corePoolSize;
		this.maxQueueSize = maxQueueSize;
	}
	public OrderedQueuePoolExecutor(int corePoolSize)
	{
		this("queue-pool", corePoolSize, 10000);
	}
	public boolean addTask(Long key,AbstractWork task)
	{
		key = Long.valueOf(key.longValue() % this.corePoolSize);
		TasksQueue<AbstractWork> queue = this.pool.getTasksQueue(key);
		boolean run = false;
		boolean result = false;
		synchronized (queue)
		{
			if (this.maxQueueSize > 0 && queue.size() > this.maxQueueSize)
			{
				log.error("队列"+this.name+"["+key+"]抛弃指令");
				queue.clear();
			}
			result = queue.add(task);
			if (result)
			{
				task.setTaskQueue(queue);
				if (queue.isProcessingCompleted())
				{
					queue.setProcessingCompleted(false);
					run = true;
				}
			}
			else
			{
				log.error("队列添加任务失败！");
			}
		}
		if (run)
		{
			execute((Runnable)queue.poll());
		}
		return result;
	}
	protected void afterExecute(Runnable r,Throwable t)
	{
		super.afterExecute(r, t);
		AbstractWork task = (AbstractWork)r;
		TasksQueue<AbstractWork> queue = task.getTaskQueue();
		if (queue != null)
		{
			AbstractWork afterWork = null;
			synchronized(queue)
			{
				afterWork = (AbstractWork)queue.poll();
				if (afterWork == null)
				{
					queue.setProcessingCompleted(true);
				}
			}
			if (afterWork != null)
			{
				execute(afterWork);
			}
		}
		else
		{
			log.error("执行队列为空");
		}
	}
	public int getTaskCounts()
	{
		int count = super.getActiveCount();
		Iterator<Entry<Long, TasksQueue<AbstractWork>>> iter = this.pool.getTasksQueues().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Long, TasksQueue<AbstractWork>> entry = iter.next();
			TasksQueue<AbstractWork> tasksQueue = entry.getValue();
			count += tasksQueue.size();
		}
		return count;
	}
}
