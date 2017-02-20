package com.chen.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;

public abstract class Message extends Bean
{
	private long sendId;
	private List<Long> roleId = new ArrayList<Long>();
	private IoSession session;

	public abstract int getId();

	public abstract String getQueue();

	public abstract String getServer();

	public IoSession getSession() {
		return this.session;
	}
	public void setSession(IoSession session) {
		this.session = session;
	}
	public long getSendId() {
		return sendId;
	}

	public void setSendId(long sendId) {
		this.sendId = sendId;
	}

	public List<Long> getRoleId() {
		return roleId;
	}

	public void setRoleId(List<Long> roleId) {
		this.roleId = roleId;
	}
}
