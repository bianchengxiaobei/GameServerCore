package com.chen.timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SchedulerTask implements Job
{
	private Logger log = LogManager.getLogger(SchedulerEvent.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getMergedJobDataMap();
		String className = data.getString("className");
		try 
		{
			Class c = Class.forName(className);
			SchedulerEvent job = (SchedulerEvent)c.newInstance();
			job.action();
		} catch (Exception e) {
			log.error(e,e);
		}
		
	}

}
