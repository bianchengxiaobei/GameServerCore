package com.chen.cache.structs;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.util.ConcurrentHashSet;

public class WaitingUpdateQueue<V> implements Serializable
{
	private static final long serialVersionUID = 3016000684709255375L;
	private ConcurrentLinkedQueue<V> queue = new ConcurrentLinkedQueue<V>();
	private ConcurrentHashSet<V> set = new ConcurrentHashSet<V>();
	
	public void add(V value)
	{
		if (!this.set.contains(value))
		{
			this.set.add(value);
			this.queue.add(value);
		}
	}
	public V poll()
	{
		Object value = this.queue.poll();
		if (value != null)
		{
			this.set.remove(value);
		}
		return (V)value;
	}
	public boolean contain(V value)
	{
		return this.set.contains(value);
	}
	public void remove(V value)
	{
		this.set.remove(value);
		this.queue.remove(value);
	}
}