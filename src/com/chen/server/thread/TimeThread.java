package com.chen.server.thread;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.chen.timer.ITimerEvent;
import com.chen.timer.TimerEvent;
/**
 * 定时执行时间器
 * @author chen
 *
 */
public class TimeThread extends Timer 
{
	//带有定时重复执行的消息处理向量集合
	private Vector<ITimerEvent> events = new Vector<ITimerEvent>();
	//启动该定时时间器的主线程
	private ServerThread main;
	//定时时间任务
	private TimerTask task;
	
	public TimeThread(ServerThread main)
	{
		super(main.threadName+"-Timer");
		this.main = main;
	}
	//在主线程里面驱动，开始执行定时时间任务
	public void Start()
	{
		this.task = new TimerTask() {			
			@Override
			public void run()
			{
				synchronized (TimeThread.this.events) 
				{
					Iterator<ITimerEvent> it = TimeThread.this.events.iterator();
					
					while(it.hasNext())
					{
						TimerEvent event = (TimerEvent)it.next();
						//如果多长时间之后再执行的间隔小于0的话，就开始判断循环次数是否大于0，如果大于0，就次数减1
						if (event.remain() <= 0L)
						{
							if (event.getLoop() > 0)
							{
								event.setLoop(event.getLoop() - 1);
							}
							else
							{
								event.setLoop(event.getLoop());
							}
							//添加到主线程中的消息队列里面
							TimeThread.this.main.addCommand(event);
						}
						if (event.getLoop() == 0)
						{
							//如果循环次数等于0的话
							it.remove();
						}
					}
				}
			}
		};
		//开始定时执行任务，延迟时间0，执行时间为heart
		schedule(this.task, 0L,this.main.getHeart());
	}
	public void stop(boolean flag)
	{
		synchronized (this.events)
		{
			this.events.clear();
			if (this.task != null)
			{
				this.task.cancel();
			}
			cancel();
		}
	}
	public void addTimerEvent(ITimerEvent event)
	{
		synchronized (events) 
		{
			this.events.add(event);
		}
	}
	public void removeTimerEvent(ITimerEvent event)
	{
		synchronized (this.events)
		{
			this.events.remove(event);
		}
	}
}
