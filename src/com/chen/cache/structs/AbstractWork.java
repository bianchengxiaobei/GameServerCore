package com.chen.cache.structs;

public abstract class AbstractWork implements Runnable
{
	private TasksQueue<AbstractWork> taskQueue;

	public TasksQueue<AbstractWork> getTaskQueue() {
		return taskQueue;
	}

	public void setTaskQueue(TasksQueue<AbstractWork> taskQueue) {
		this.taskQueue = taskQueue;
	}
}
