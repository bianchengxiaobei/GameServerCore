package com.chen.mina.code;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.chen.mina.context.ServerContext;

public class InnerServerProtocolDecoder implements ProtocolDecoder{

	@Override
	public void decode(IoSession session, IoBuffer buff, ProtocolDecoderOutput out)
			throws Exception {
		ServerContext context = null;
		if (session.getAttribute("context") != null)
		{
			context = (ServerContext)session.getAttribute("context");
		}
		if (context == null)
		{
			context = new ServerContext();
			session.setAttribute("context", context);
		}
		IoBuffer io = context.getBuff();
		io.put(buff);
		while (true)
		{
			io.flip();
			if (io.remaining() < 4)
			{
				io.compact();
				break;
			}
			int length = io.getInt();
			if (io.remaining() < length)
			{
				io.rewind();
				io.compact();
				break;
			}
			byte[] bytes = new byte[length];
			io.get(bytes);
			out.write(bytes);
			if (io.remaining() == 0)
			{
				io.clear();
				break;
			}
			io.compact();
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		
		if (session.getAttribute("context") != null)
		{
			session.removeAttribute("context");
		}
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput arg1)
			throws Exception {
		
		
	}

}
