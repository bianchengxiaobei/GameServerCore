package com.chen.server.config;
/**
 * Mina服务器的配置信息类，从xml文件加载
 * @author Administrator
 *
 */
public class MinaServerConfig extends ServerConfig
{
	private int mina_port;

	public int getMina_port() {
		return mina_port;
	}

	public void setMina_port(int mina_port) {
		this.mina_port = mina_port;
	}
	
}
