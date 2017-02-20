package com.chen.timer;

import com.chen.command.ICommand;

public abstract class SchedulerEvent implements ICommand
{
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
