package com.chen.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;

import com.chen.command.Handler;
import com.chen.server.config.BattleConfig;
import com.chen.server.thread.ServerThread;
import com.chen.server.thread.config.ThreadConfig;
import com.chen.server.thread.loader.ThreadConfigXmlLoader;

public abstract class BattleServer implements Runnable
{
	private String name;
	private List<BattleConfig> battleConfig = new ArrayList<BattleConfig>();
	private static final String defaultThreadConfigFile = "thread-config.xml";
	protected List<ThreadConfig> threadConfigs;
	protected HashMap<String, Thread> thread_pool = new HashMap<String, Thread>();
	protected ThreadGroup thread_group;
	
	public BattleServer(String name,List<BattleConfig> battleConfig)
	{
		this(name,battleConfig,"thread-config.xml");
	}
	public BattleServer(String name,List<BattleConfig> battleConfigs,String threadConfig)
	{
		this.name = name;
		this.battleConfig = battleConfigs;
		if (threadConfig != null && threadConfig != "")
		{
			this.threadConfigs = new ThreadConfigXmlLoader().load(threadConfig);
		}
		else
		{
			this.threadConfigs = new ThreadConfigXmlLoader().load("thread-config.xml");
		}
		init();
	}
	protected abstract void init();
	@Override
	public void run()
	{
		Iterator<Thread> iter = this.thread_pool.values().iterator();
		while (iter.hasNext())
		{
			Thread thread = (Thread)iter.next();
			thread.start();
		}
	}
	public void stop(boolean flag)
	{
		Iterator<Thread> iter = this.thread_pool.values().iterator();
		while (iter.hasNext())
		{
			ServerThread thread = (ServerThread)iter.next();
			thread.stop(true);
		}
	}
	public void addCommand(Handler handler) {
		ServerThread thread = null;
		if ((handler.getMessage() != null)
				&& (handler.getMessage().getQueue() != null))
			thread = (ServerThread) this.thread_pool.get(handler.getMessage()
					.getQueue());
		else {
			thread = (ServerThread) this.thread_pool.get("Main");
		}

		if (thread != null) {
			thread.addCommand(handler);
		} else
			handler.action();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BattleConfig> getBattleConfig() {
		return battleConfig;
	}
	public void setBattleConfig(List<BattleConfig> battleConfig) {
		this.battleConfig = battleConfig;
	}
}
