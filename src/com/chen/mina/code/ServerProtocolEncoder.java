package com.chen.mina.code;

import java.nio.ByteOrder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.chen.message.Message;
import com.chen.message.TransfersMessage;
import com.chen.util.SessionUtil;

public class ServerProtocolEncoder implements ProtocolEncoder
{
	protected Logger log = LogManager.getLogger("GateSessionClose");
	private static final int MAX_SIZE = 1048576;
	@Override
	public void dispose(IoSession arg0) throws Exception {
		
	}

	@Override
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out)
			throws Exception {
		if (session.getScheduledWriteBytes() > MAX_SIZE)
		{
			SessionUtil.closeSession(session, "等待发送字节过多["+session.getScheduledWriteBytes()+"]");
		}
		if (obj instanceof Message)
		{
			Message message = (Message)obj;
			IoBuffer buf = IoBuffer.allocate(100);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.setAutoExpand(true);
			buf.setAutoShrink(true);
			buf.putInt(0);//放入长度
			buf.putInt(message.getId());//放入id
			message.write(buf);//放入消息
			buf.flip();
			buf.putInt(buf.limit() - 4);//修改写入的长度
			buf.rewind();
			if (session.isConnected())
			{
				out.write(buf);
				out.flush();
			}
		}else if (obj instanceof TransfersMessage)
		{
			TransfersMessage message = (TransfersMessage)obj;
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			buf.setAutoShrink(true);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(0);
			buf.putInt(message.getId());
			buf.put(message.getBytes());
			buf.flip();
			int length = buf.limit() - 4;
			buf.putInt(length);
			buf.rewind();
			if (session.isConnected())
			{
				out.write(buf);
				out.flush();
			}
		}
	}

}
