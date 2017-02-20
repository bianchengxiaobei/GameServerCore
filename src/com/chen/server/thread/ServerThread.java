package com.chen.server.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chen.command.ICommand;
import com.chen.command.ICommandFilter;
import com.chen.timer.ITimerEvent;

public class ServerThread extends Thread
{
	private Logger log = LogManager.getLogger(ServerThread.class);
	//消息处理器阻塞队列
	private LinkedBlockingQueue<ICommand> command_queue = new LinkedBlockingQueue<ICommand>();
	//定时timer类
	private TimeThread timer;
	//线程名字
	protected String threadName;
	//任务重复执行固定延迟
	protected int heart;
	//消息处理过滤器
	private List<ICommandFilter> filters = new ArrayList<ICommandFilter>();
	private boolean stop;
	private boolean processingCompleted = false;
	
	public ServerThread(ThreadGroup group, String threadName, int heart)
	{
		super(group,threadName);
		this.threadName = threadName;
		this.heart = heart;
		//如果任务执行固定时间大于0的话，就开始时间timer类
		if (this.heart > 0)
		{
			this.timer = new TimeThread(this);
		}
		setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				ServerThread.this.log.error(e,e);
				if (ServerThread.this.timer != null)
				{
					ServerThread.this.command_queue.clear();
				}
			}
		});
	}
	public void run()
	{
		if (this.heart > 0 && this.timer != null)
		{
			this.timer.Start();//开始timer类的时间任务TimerTask,默认无延迟执行heart时间
		}
		this.stop = false;
		int loop = 0;
		while (!this.stop)
		{
			//取出队列里面的头消息处理器
			ICommand command = (ICommand)this.command_queue.poll();
			if (command == null)
			{
				try {
					synchronized (this) 
					{
						loop = 0;
						this.processingCompleted = true;
						wait();
					}
				} catch (InterruptedException e) {
					log.error(e,e);
				}
			}
			else
			{
				try {
					loop++;
					this.processingCompleted = false;
					long start = System.currentTimeMillis();
					boolean result = false;
					//过滤所有的消息过滤器，如果被过滤了，就不执行
					for (int i=0;i<this.filters.size(); i++)
					{
						if (!this.filters.get(i).filter(command))
						{
							result = true;
							break;
						}
					}
					if (!result)
					{
						//执行消息
						command.action();
						long end = System.currentTimeMillis();
						//执行这条消息时间过长
						if (end - start > 50L)
						{
							this.log.error(getName()+"--->"+command.getClass().getSimpleName()+"Run "+(end - start));
						}
						//如果持续处理消息1000条，就休眠1ms
						if (loop > 1000)
						{
							loop = 0;
							try {
								Thread.sleep(1L);
							} catch (Exception e) {
								log.error(e,e);
							}
						}
					}
				} catch (Exception e) {
					log.error(e,e);
				}
			}
		}
	}
	public void stop(boolean flag)
	{
		this.stop  =flag;
		if (this.timer != null)
		{
			this.timer.stop(flag);
		}
		this.command_queue.clear();
		try {
			synchronized (this) 
			{
				if (this.processingCompleted)
				{
					this.processingCompleted = false;
					notify();
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
	}
	public String getThreadName() {
		return threadName;
	}

	public int getHeart() {
		return heart;
	}

	public void addCommand(ICommand command)
	{
		try 
		{
			this.command_queue.add(command);
			synchronized (this) 
			{
				notify();
			}
		} catch (Exception e)
		{
			this.log.error("主线程"+this.threadName+"Notify出异常"+e.getMessage());
		}
	}
	public void addCommandFilter(ICommandFilter filter)
	{
		this.filters.add(filter);
	}
	/**
	 * 添加时间定时任务（TimeEvent(loop,delay)）
	 * 如果loop=-1=>无限循环，loop>0=>循环多少次数
	 * @param event
	 */
	public void addTimeEvent(ITimerEvent event)
	{
		if (this.timer != null)
		{
			this.timer.addTimerEvent(event);
		}
	}
	public void removeTimeEvent(ITimerEvent event)
	{
		if (this.timer != null)
		{
			this.timer.removeTimerEvent(event);
		}
	}
}
