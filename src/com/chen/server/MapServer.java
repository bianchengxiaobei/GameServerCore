package com.chen.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.chen.command.Handler;
import com.chen.server.config.MapConfig;
import com.chen.server.thread.ServerThread;
import com.chen.server.thread.config.ThreadConfig;
import com.chen.server.thread.loader.ThreadConfigXmlLoader;

public abstract class MapServer implements Runnable
{
	private String name;
	  private long zoneId;
	  private int zoneModelId;
	  private List<MapConfig> mapConfigs = new ArrayList();
	  private static final String defaultThreadConfigFile = "thread-config.xml";
	  protected List<ThreadConfig> threadConfigs;
	  protected HashMap<String, Thread> thread_pool = new HashMap<>();
	  protected ThreadGroup thread_group;

	  protected MapServer(String name, long zoneId, int zoneModelId, List<MapConfig> mapConfigs)
	  {
	    this(name, zoneId, zoneModelId, mapConfigs, defaultThreadConfigFile);
	  }

	  protected MapServer(String name, long zoneId, int zoneModelId, List<MapConfig> mapConfigs, String threadConfig) {
	    this.name = name;
	    this.zoneId = zoneId;
	    this.zoneModelId = zoneModelId;
	    this.mapConfigs = mapConfigs;

	    if (threadConfig != null) this.threadConfigs = new ThreadConfigXmlLoader().load(threadConfig); else {
	      this.threadConfigs = new ThreadConfigXmlLoader().load("thread-config.xml");
	    }
	    this.thread_group = new ThreadGroup(name);

	    for (int i = 0; i < this.threadConfigs.size(); i++) {
	      ThreadConfig config = (ThreadConfig)this.threadConfigs.get(i);
	      ServerThread thread = new ServerThread(this.thread_group, getName() + "-->" + config.getThreadName(), config.getHeart());
	      this.thread_pool.put(config.getThreadName(), thread);
	    }

	    init();
	  }

	  protected abstract void init();

	  public void run() {
	    Iterator iter = this.thread_pool.values().iterator();
	    while (iter.hasNext()) {
	      Thread thread = (Thread)iter.next();
	      thread.start();
	    }
	  }

	  public void stop(boolean flag) {
	    Iterator iter = this.thread_pool.values().iterator();
	    while (iter.hasNext()) {
	      ServerThread thread = (ServerThread)iter.next();
	      thread.stop(true);
	    }
	  }

	  public void addCommand(Handler handler)
	  {
	    ServerThread thread = null;
	    if ((handler.getMessage() != null) && (handler.getMessage().getQueue() != null))
	      thread = (ServerThread)this.thread_pool.get(handler.getMessage().getQueue());
	    else {
	      thread = (ServerThread)this.thread_pool.get("Main");
	    }

	    if (thread != null)
	    {
	      thread.addCommand(handler);
	    }
	    else handler.action();
	  }

	  public String getName()
	  {
	    return this.name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }

	  public List<MapConfig> getMapConfigs() {
	    return this.mapConfigs;
	  }

	  public void setMapConfigs(List<MapConfig> mapConfigs) {
	    this.mapConfigs = mapConfigs;
	  }

	  public long getZoneId() {
	    return this.zoneId;
	  }

	  public void setZoneId(long zoneId) {
	    this.zoneId = zoneId;
	  }

	  public int getZoneModelId() {
	    return this.zoneModelId;
	  }

	  public void setZoneModelId(int zoneModelId) {
	    this.zoneModelId = zoneModelId;
	  }
}
