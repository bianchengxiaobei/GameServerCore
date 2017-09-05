package com.chen.message;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

public abstract class Bean 
{
	protected Logger log = LogManager.getLogger(Bean.class);

	protected void writeInt(IoBuffer buf, int value) {
		buf.putInt(value);
	}
	protected void writeString(IoBuffer buf, String value) 
	{
		if (value == null || value.length() == 0) {
			buf.putInt(0);
			return;
		}
		try {
			byte[] bytes = value.getBytes("UTF-8");
			buf.putInt(bytes.length);
			buf.put(bytes);
		} catch (UnsupportedEncodingException e) {
			this.log.error("Encode String Error:" + e.getMessage());
		}
	}
	protected void writeLong(IoBuffer buf, long value) {
		buf.putLong(value);
	}
	protected void writeFloat(IoBuffer buf,float value)
	{
		buf.putFloat(value);
	}
	protected void writeBean(IoBuffer buf, Bean value) {
		value.write(buf);
	}

	protected void writeShort(IoBuffer buf, int value) {
		buf.putShort((short) value);
	}

	protected void writeShort(IoBuffer buf, short value) {
		buf.putShort(value);
	}

	protected void writeByte(IoBuffer buf, byte value) {
		buf.put(value);
	}
	protected void writeIntList(IoBuffer buf,List<Integer> value)
	{
		this.writeInt(buf, value.size());
		for (int i=0; i<value.size(); i++)
		{
			this.writeInt(buf, value.get(i));
		}
	}
	protected void writeStrList(IoBuffer buf,List<String> value)
	{
		this.writeInt(buf, value.size());
		for (int i=0; i<value.size(); i++)
		{
			this.writeString(buf, value.get(i));
		}
	}
	protected void writeLongList(IoBuffer buf,List<Long> value)
	{
		this.writeInt(buf, value.size());
		for (int i=0; i<value.size(); i++)
		{
			this.writeLong(buf, value.get(i));
		}
	}
	protected int readInt(IoBuffer buf) {
		return buf.getInt();
	}

	protected String readString(IoBuffer buf) {
		int length = buf.getInt();
		if (length == 0)
			return null;
		byte[] bytes = new byte[length];
		buf.get(bytes);
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.log.error("Decode String Error:" + e.getMessage());
		}
		return null;
	}

	protected long readLong(IoBuffer buf) {
		return buf.getLong();
	}
	protected float readFloat(IoBuffer buf)
	{
		return buf.getFloat();
	}
	protected Bean readBean(IoBuffer buf, Class<? extends Bean> clazz) {
		try {
			Bean bean = (Bean) clazz.newInstance();
			bean.read(buf);
			return bean;
		} catch (IllegalAccessException e) {
			this.log.error("Decode Bean Error:" + e.getMessage());
		} catch (InstantiationException e) {
			this.log.error("Decode Bean Error:" + e.getMessage());
		}
		return null;
	}

	protected short readShort(IoBuffer buf) {
		return buf.getShort();
	}

	protected byte readByte(IoBuffer buf) {
		return buf.get();
	}
	protected List<Integer> readIntList(IoBuffer buf)
	{
		List<Integer> list = new ArrayList<Integer>();
		int num = 0;
		num = this.readInt(buf);
		for (int i=0; i<num; i++)
		{
			int item = 0;
			item = this.readInt(buf);
			list.add(item);
		}
		return list;
	}
	protected List<Long> readLongList(IoBuffer buf)
	{
		List<Long> list = new ArrayList<Long>();
		int num = 0;
		num = this.readInt(buf);
		for (int i=0; i<num; i++)
		{
			long item = 0;
			item = this.readLong(buf);
			list.add(item);
		}
		return list;
	}
	protected List<String> readStringList(IoBuffer buf)
	{
		List<String> list = new ArrayList<String>();
		int num = 0;
		num = this.readInt(buf);
		for (int i=0; i<num; i++)
		{
			String item = "";
			item = this.readString(buf);
			list.add(item);
		}	
		return list;
	}
	public abstract boolean write(IoBuffer paramIoBuffer);

	public abstract boolean read(IoBuffer paramIoBuffer);
}
