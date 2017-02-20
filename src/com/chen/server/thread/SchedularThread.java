package com.chen.server.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.chen.timer.SchedulerTask;

public class SchedularThread extends Thread
{
	private static Object obj = new Object();

	private Logger log = LogManager.getLogger(SchedularThread.class);
	private Scheduler scheduler;
	private int count = 0;
	private List<SchedulerInfo> infos = new ArrayList<SchedularThread.SchedulerInfo>();
	public void run()
	{
		try {
			synchronized (obj) 
			{
				this.scheduler = StdSchedulerFactory.getDefaultScheduler();
				this.scheduler.start();
				init();
			}
		} catch (Exception e) {
			this.log.error(e,e);
		}
	}
	private void init()
	{
		for (int i=0; i<this.infos.size(); i++)
		{
			SchedulerInfo info = (SchedulerInfo)this.infos.get(i);
			try {
				this.scheduler.scheduleJob(info.getJob(), info.getTrigger());
			} catch (Exception e) {
				this.log.error(e,e);
			}
		}
	}
	public void addSchedulerTask(String cron, String className)
	{
		this.count++;
		JobDetail job = JobBuilder.newJob(SchedulerTask.class).withIdentity("job"+this.count,"SchedulerTaskGroup")
				.usingJobData("className", className)
				.build();
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger"+this.count, "SchedulerTaskGroup")
				.withSchedule(CronScheduleBuilder.cronSchedule(cron))
				.forJob("job"+this.count,"SchedulerTaskGroup")
				.build();
		synchronized(obj)
		{
			if (this.scheduler == null)
			{
				this.infos.add(new SchedulerInfo(job, trigger));			
			}
			else
			{
				try {
					this.scheduler.scheduleJob(job,trigger);
				} catch (Exception e) {
					this.log.error(e,e);
				}
			}
		}
	}
	public void stop(boolean flag)
	{
		try {
			this.scheduler.shutdown(flag);
		} catch (Exception e) {
			this.log.error(e,e);
		}
	}
	private class SchedulerInfo
	{
		private JobDetail job;
		public JobDetail getJob() {
			return job;
		}
		public Trigger getTrigger() {
			return trigger;
		}
		private Trigger trigger;
		public SchedulerInfo(JobDetail job, Trigger tri)
		{
			this.job = job;
			this.trigger = tri;
		}
	}
}
