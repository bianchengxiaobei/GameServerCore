package com.chen.server;

public abstract class BattleServer implements Runnable
{
	private String name;	
	public BattleServer(String name)
	{
		this.name = name;
		init();
	}
	protected abstract void init();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
