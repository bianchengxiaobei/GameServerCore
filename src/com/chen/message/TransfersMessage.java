package com.chen.message;

import java.util.ArrayList;
import java.util.List;

public class TransfersMessage
{
	private int id;
	private long sendId;
	private List<Long> roleIds = new ArrayList<Long>();
	private byte[] bytes;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getSendId() {
		return sendId;
	}
	public void setSendId(long sendId) {
		this.sendId = sendId;
	}
	public List<Long> getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public int getLength()
	{
		return this.bytes.length + 4;
	}
	public int getLengthWithRole()
	{
		return this.bytes.length + 4 + 8 + this.roleIds.size() * 64 / 8 + 4;
	}
}
