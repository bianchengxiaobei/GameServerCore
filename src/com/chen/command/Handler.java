package com.chen.command;

import com.chen.message.Message;

public abstract class Handler implements ICommand
{
	private Message message;
	private Object parameter;
	private long createTime;
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public Object getParameter() {
		return parameter;
	}
	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
