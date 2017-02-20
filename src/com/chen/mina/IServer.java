package com.chen.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
/**
 * 服务器基类抽象接口
 * */
public abstract interface IServer 
{
	public static final int GATE_SERVER = 1;
	public static final int GAME_SERVER = 2;
	public static final int WORLD_SERVER = 3;
	public static final int PUBLIC_SERVER = 4;
	/**
	 * 处理消息
	 * @param paramIoSession
	 * @param paramIoBuffer
	 */
	public abstract void doCommand(IoSession paramIoSession,
			IoBuffer paramIoBuffer);

	public abstract void sessionCreate(IoSession paramIoSession);

	public abstract void sessionOpened(IoSession paramIoSession);

	public abstract void sessionClosed(IoSession paramIoSession);

	public abstract void exceptionCaught(IoSession paramIoSession,
			Throwable paramThrowable);

	public abstract void sessionIdle(IoSession paramIoSession,
			IdleStatus paramIdleStatus);
}
