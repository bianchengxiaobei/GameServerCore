package com.chen.command;

public abstract interface ICommand extends Cloneable
{
	public abstract void action();
	public abstract Object clone() throws CloneNotSupportedException;
}
