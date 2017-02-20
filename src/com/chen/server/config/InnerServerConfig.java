package com.chen.server.config;
/**
 * 内部游戏服务器配置实体类
 * @author chen
 *
 */
public class InnerServerConfig extends ServerConfig
{
	private int port;
	public int getPort()
	{
		return this.port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
}
