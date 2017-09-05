package com.chen.mina.code;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.chen.mina.context.ServerContext;
import com.chen.util.SessionUtil;

public class ServerProtocolDecoder implements ProtocolDecoder
{
	protected Logger log = LogManager.getLogger(ServerProtocolDecoder.class);
	private static final String START_TIME = "start_time";
	private static final String RECEIVE_COUNT = "receive_count";
	private static final int MAX_SIZE = 10240;
	private static final int MAX_COUNT = 30;
	@Override
	public void decode(IoSession session, IoBuffer buff, ProtocolDecoderOutput out)
			throws Exception {
		long startTime = 0L;
		if (session.containsAttribute(START_TIME))
		{
			startTime = ((Long)session.getAttribute(START_TIME)).longValue();
		}
		int count = 0;
		if (session.containsAttribute(RECEIVE_COUNT))
		{
			count = ((Integer)session.getAttribute(RECEIVE_COUNT)).intValue();
		}
		if (System.currentTimeMillis() - startTime > 1000L)
		{
			if (count > 10)
				this.log.error("客户端"+session+"--->发送消息："+count);
			startTime = System.currentTimeMillis();
			count = 0;
		}
		count++;
		if (count > MAX_COUNT)
		{
			this.log.error("客户端"+session+"--->发送消息:"+count+"--->关闭--->buff剩余:"+buff.remaining());
			SessionUtil.closeSession(session, "发送消息过多["+count+"]");
			return ;
		}
		session.setAttribute(START_TIME, Long.valueOf(startTime));
		session.setAttribute(RECEIVE_COUNT, Integer.valueOf(count));
		ServerContext context = null;
		if (session.getAttribute("context") != null)
		{
			context = (ServerContext)session.getAttribute("context");
		}
		if (context == null)
		{
			context = new ServerContext();
			session.setAttribute("context",context);
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
			//byte[] temp = io.array();
			//int length = (temp[0] & 0xFF) << 24 | (temp[1] & 0xFF) << 16 | (temp[2] & 0xFF) << 8 | (temp[3] & 0xFF) << 0;
			int length = io.getInt();
			if (length > MAX_SIZE || length <= 0)
			{
				int pre = 0;
				if (session.containsAttribute("pre_message"))
				{
					pre = ((Integer)session.getAttribute("pre_message")).intValue();
				}
				SessionUtil.closeSession(session, "发送消息过长,长度为["+length+"],前一条消息长度为["+pre+"]");
				break;
			}
			if (io.remaining() < length)
			{
				io.rewind();
				io.compact();
				break;
			}
			int order = io.getInt();
			//int order = (temp[4] & 0xFF) << 24 | (temp[5] & 0xFF) << 16 | (temp[6] & 0xFF) << 8 | (temp[7] & 0xFF) << 0;
			//order ^= 512;
			//order ^= length;
			int preOrder = 0;
			if (session.containsAttribute("pre_order"))
			{
				preOrder = ((Integer)session.getAttribute("pre_order")).intValue();
			}
			byte[] bytes = new byte[length-4];
			io.get(bytes);
			int messageid = 0;
			try {
				messageid = (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF) << 0;
			} catch (Exception e) {
				this.log.error(e);
			}
			if (order == preOrder)
			{
				if (order == 0)
				{
					this.log.info("客户端"+session+"发送消息序列成功，发包序列："+order+",当前消息id:"+messageid);
				}
				session.setAttribute("pre_order",Integer.valueOf(order+1));
				//ByteBuffer buffer = ByteBuffer.allocate(4);
				//buffer.putInt(length);
				//byte[] lenbytes = buffer.array();
				//System.arraycopy(lenbytes, 0, bytes, 0, 4);
				out.write(bytes);
			}
			else
			{
				StringBuffer buffer = new StringBuffer();
		        if (session.containsAttribute("session_ip")) {
		          buffer.append("ip:" + session.getAttribute("session_ip") + ",");
		        }
		        if (session.containsAttribute("player_id")) {
		          buffer.append("player:" + session.getAttribute("player_id") + ",");
		        }
		        if (session.containsAttribute("user_id")) {
		          buffer.append("user:" + session.getAttribute("user_id") + ",");
		        }
		        this.log.error(session + "[" + buffer.toString() + "]发送消息序列出错，发包序列：" + order + ",当前序列:" + preOrder + ",当前消息号:" + messageid);
			}
			if (io.remaining() == 0)
			{
				io.clear();
				break;
			}
			io.compact();
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception 
	{
		if (session.getAttribute("context") != null)
		{
			session.removeAttribute("context");
		}
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		
	}

}
