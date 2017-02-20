package com.chen.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.chen.server.config.ServerConfig;
/**
 * 服务器抽象基类(线程)
 * @author chen
 *	主要初始化服务器的配置信息，比如名字，id等等
 */
public abstract class Server implements Runnable
{
	private String server_name;//服务器名字
	public static int server_id;//服务器id
	private String server_web;//服务器所在的网站站点，比如37wan，百度什么的
	protected ServerConfig serverConfig;
	public static final String DEFAULT_MAIN_THREAD = "Main";
	/**
	 * 初始化服务器配置信息类
	 */
	protected Server(ServerConfig serverConfig)
	{
		this.serverConfig = serverConfig;

		if (this.serverConfig != null) 
		{
			init();
		}
	}
	/**
	 * 根据配置类初始化服务器
	 */
	protected void init()
	{
		this.server_name = this.serverConfig.getName();
		server_id = this.serverConfig.getId();
		this.server_web = this.serverConfig.getWeb();
	}
	public String getServer_name() {
		return server_name;
	}
	public int getServer_id() {
		return server_id;
	}
	public String getServer_web() {
		return server_web;
	}
	@Override
	/**
	 * 添加jvm关闭时钩子方法
	 */
	public void run()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new CloseByExit(this.server_name)));
	}
	protected abstract void stop(); 
	/**
	 * 服务器关闭时的线程处理类
	 * @author chen
	 */
	private class CloseByExit implements Runnable
	{
		private Logger log = LogManager.getLogger(CloseByExit.class);
		private String server_name;
		public CloseByExit(String name)
		{
			this.server_name = name;
		}
		@Override
		public void run() 
		{
			Server.this.stop();
			this.log.info(this.server_name+"停止运行！");
		}		
	}
}
