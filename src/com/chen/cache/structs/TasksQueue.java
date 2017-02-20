package com.chen.cache.structs;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TasksQueue<V>
{
	private Lock lock = new ReentrantLock();
	private final ArrayDeque<V> tasksQueues = new ArrayDeque<V>();
	private boolean processingCompleted = true;
	public V poll()
	{
		try {
			this.lock.lock();
			return this.tasksQueues.poll();
		}
		finally
		{
			this.lock.unlock();
		}
	}
	public boolean add(V value)
	{
		try {
			this.lock.lock();
			return this.tasksQueues.add(value);
		}
		finally
		{
			this.lock.unlock();
		}
	}
	public void clear()
	{
		try {
			this.lock.lock();
			this.tasksQueues.clear();
		}finally{
			this.lock.unlock();
		}
	}
	public int size()
	{
		return this.tasksQueues.size();
	}
	public boolean isProcessingCompleted() {
		return processingCompleted;
	}
	public void setProcessingCompleted(boolean processingCompleted) {
		this.processingCompleted = processingCompleted;
	}
}
