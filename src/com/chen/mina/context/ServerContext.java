package com.chen.mina.context;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

public class ServerContext 
{
	private IoBuffer buff;

	public ServerContext() {
		this.buff = IoBuffer.allocate(1024);
		this.buff.setAutoExpand(true);
		this.buff.setAutoShrink(true);
		this.buff.order(ByteOrder.LITTLE_ENDIAN);
	}

	public void append(IoBuffer buff) {
		this.buff.put(buff);
	}

	public IoBuffer getBuff() {
		return this.buff;
	}
}
