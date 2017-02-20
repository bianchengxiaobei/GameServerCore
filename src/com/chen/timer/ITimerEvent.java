package com.chen.timer;

import com.chen.command.ICommand;

public abstract interface ITimerEvent extends ICommand 
{
	public abstract long remain();
}
