package com.chen.mina.handle;

import java.nio.ByteOrder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.chen.mina.IServer;
/**
 * 服务器事务处理类
 * @author chen
 *
 */
public class ServerProtocolHandler extends IoHandlerAdapter
{
	protected static Logger log = LogManager.getLogger(ServerProtocolHandler.class);
	private IServer server;
	public ServerProtocolHandler(IServer server)
	{
		this.server = server;
	}

	public void sessionCreated(IoSession iosession) throws Exception {
		this.server.sessionCreate(iosession);
	}

	public void sessionOpened(IoSession iosession) throws Exception {
		this.server.sessionOpened(iosession);
	}

	public void sessionClosed(IoSession iosession) throws Exception {
		this.server.sessionClosed(iosession);
	}

	public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
			throws Exception {
		this.server.sessionIdle(iosession, idlestatus);
	}

	public void exceptionCaught(IoSession iosession, Throwable cause)
			throws Exception {
		this.server.exceptionCaught(iosession, cause);
	}

	public void messageReceived(IoSession iosession, Object obj)
			throws Exception {
		byte[] bytes = (byte[]) obj;

		IoBuffer buf = IoBuffer.allocate(bytes.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.put(bytes);

		buf.flip();

		this.server.doCommand(iosession, buf);
	}

	public void messageSent(IoSession iosession, Object obj) throws Exception {
	}
}
