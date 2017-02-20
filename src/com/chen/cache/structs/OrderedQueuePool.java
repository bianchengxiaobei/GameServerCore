package com.chen.cache.structs;

import java.util.concurrent.ConcurrentHashMap;

public class OrderedQueuePool<K,V> 
{
	ConcurrentHashMap<K, TasksQueue<V>> map = new ConcurrentHashMap<K, TasksQueue<V>>();

	public TasksQueue<V> getTasksQueue(K key) {
		synchronized (this.map) {
			TasksQueue<V> queue = (TasksQueue<V>) this.map.get(key);

			if (queue == null) {
				queue = new TasksQueue<V>();
				this.map.put(key, queue);
			}
			return queue;
		}
	}
	public ConcurrentHashMap<K, TasksQueue<V>> getTasksQueues() {
		return this.map;
	}

	public void removeTasksQueue(K key) {
		this.map.remove(key);
	}
}
