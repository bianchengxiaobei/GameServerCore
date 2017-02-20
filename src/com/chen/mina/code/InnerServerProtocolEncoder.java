package com.chen.mina.code;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.chen.message.Message;
import com.chen.message.TransfersMessage;

public class InnerServerProtocolEncoder implements ProtocolEncoder
{
	@Override
	public void dispose(IoSession arg0) throws Exception {
		
	}
	@Override
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out)
			throws Exception {
		if (obj instanceof Message)
		{
			Message message = (Message)obj;
			IoBuffer buf = IoBuffer.allocate(100);
			buf.setAutoExpand(true);
			buf.setAutoShrink(true);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(0);
			buf.putInt(message.getId());
			buf.putLong(message.getSendId());
			buf.putInt(message.getRoleId().size());
			for (int i=0; i<message.getRoleId().size(); i++)
			{
				buf.putLong(((Long)message.getRoleId().get(i)).longValue());
			}
			message.write(buf);
			buf.flip();
			buf.putInt(buf.limit() - 4);
			buf.rewind();
			
			out.write(buf);
			out.flush();
		}
		else if ((obj instanceof TransfersMessage)) {
			TransfersMessage message = (TransfersMessage) obj;
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			buf.setAutoShrink(true);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(message.getLengthWithRole());
			buf.putInt(message.getId());
			buf.putLong(message.getSendId());
			buf.putInt(message.getRoleIds().size());
			for (int i = 0; i < message.getRoleIds().size(); i++) {
				buf.putLong(((Long) message.getRoleIds().get(i)).longValue());
			}
			buf.put(message.getBytes());
			buf.flip();
			out.write(buf);
			out.flush();
		}
	}

}
