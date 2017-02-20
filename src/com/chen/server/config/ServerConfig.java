package com.chen.server.config;
/**
 * 抽象服务器配置基类。从xml读取然后加载
 * 主要读取服务器名字，id，站点等
 */
public abstract class ServerConfig 
{
	private String name;
	private int id;
	private String web;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
}
