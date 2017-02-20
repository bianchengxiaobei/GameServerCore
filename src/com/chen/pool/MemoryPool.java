package com.chen.pool;

import java.io.Serializable;
import java.util.Vector;
/**
 * 对象池
 * @author Administrator
 *
 * @param <T>
 */
public class MemoryPool<T extends MemoryObject> implements Serializable
{
	private static final long serialVersionUID = -5555346087271870315L;
	public int MAX_SIZE = 500;
	private Vector<T> cache = new Vector<T>();
	public MemoryPool()
	{
		
	}
	public MemoryPool(int max)
	{
		this.MAX_SIZE = max;
	}

	public void put(T value) {
		synchronized (this.cache) {
			if ((!this.cache.contains(value))
					&& (this.cache.size() < this.MAX_SIZE)) {
				value.release();

				this.cache.add(value);
			}
		}
	}

	public T get(Class<?> c) throws IllegalAccessException,
			InstantiationException {
		synchronized (this.cache) {
			MemoryObject value = null;
			if (this.cache.size() > 0)
				value = (MemoryObject) this.cache.remove(0);
			else {
				value = (MemoryObject) c.newInstance();
			}

			return (T)value;
		}
	}
}
