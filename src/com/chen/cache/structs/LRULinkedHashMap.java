package com.chen.cache.structs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LRULinkedHashMap<K,V> extends LinkedHashMap<K, V>
{
	private static final long serialVersionUID = -5005299612061830223L;
	private int max = 16;
	private static final int START_NUMBER = 16;
	private static final float DEAFAULT_LOAD_FACTOR = 0.75f;
	private Lock lock = new ReentrantLock();
	
	public LRULinkedHashMap(int max)
	{
		super(16,0.75f,true);
		this.max = max;
	}
	public boolean removeEldestEntry(Map.Entry<K, V> eldest)
	{
		return size() > this.max;
	}
	public V get(Object k)
	{
		try {
			this.lock.lock();
			return super.get(k);
		} 
		finally
		{
			this.lock.unlock();
		}
	}
	public V put(K key,V value)
	{
		try {
			this.lock.lock();
			return super.put(key, value);
		} finally{
			this.lock.unlock();
		}
	}
	public V remove(Object key)
	{
		try {
			this.lock.lock();
			return super.remove(key);
		}finally{
			this.lock.unlock();
		}
	}
}
