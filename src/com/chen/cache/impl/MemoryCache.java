package com.chen.cache.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.chen.cache.structs.Cache;
import com.chen.cache.structs.LRULinkedHashMap;
import com.chen.cache.structs.WaitingUpdateQueue;

public class MemoryCache<K,V> implements Cache<K, V>,Serializable
{
	private static final long serialVersionUID = -3656956459941919920L;
	private static int MAX_SIZE = 5000;
	private static int PRE_SAVE = 5;
	protected int saveSize;
	private LRULinkedHashMap<K, V> cache;
	private WaitingUpdateQueue<V> queue = new WaitingUpdateQueue<V>();

	public MemoryCache() {
		this(MAX_SIZE, PRE_SAVE);
	}

	public MemoryCache(int maxSize, int saveSize) {
		this.cache = new LRULinkedHashMap<K, V>(maxSize);
		this.saveSize = saveSize;
	}

	public synchronized void put(K key, V value) {
		if (this.cache.containsKey(key)) {
			this.queue.add(value);
			return;
		}

		this.cache.put(key, value);
	}

	public V get(K key) {
		Object value = this.cache.get(key);

		return (V)value;
	}

	public void remove(K key) {
		Object value = this.cache.get(key);

		if (value != null) {
			this.cache.remove(key);

			this.queue.remove((V)value);
		}
	}

	public List<V> getWaitingSave(int size) {
		ArrayList waiting = new ArrayList();

		int i = 0;

		Object value = this.queue.poll();
		while (value != null) {
			waiting.add(value);
			i++;
			if (i == size)
				break;
			value = this.queue.poll();
		}
		return waiting;
	}

	public List<V> getAllWaitingSave() {
		ArrayList waiting = new ArrayList();

		Object value = this.queue.poll();
		while (value != null) {
			waiting.add(value);

			value = this.queue.poll();
		}
		return waiting;
	}

	public LRULinkedHashMap<K, V> getCache() {
		return this.cache;
	}
	
}
