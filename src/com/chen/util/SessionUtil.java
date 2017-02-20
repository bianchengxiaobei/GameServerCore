package com.chen.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.session.IoSession;

public class SessionUtil 
{
	private static Logger closelog = LogManager.getLogger("GateSessionClose");
	/**
	 * 服务器强制关闭客户端的连接
	 * @param 客户端
	 * @param 关闭理由
	 */
	public static void closeSession(IoSession session,String reason)
	{
		closelog.error("客户端"+session+"--->关闭，因为"+reason);
		session.close(true);
	}
	/**
	 * 服务器关闭客户端的连接
	 * @param 客户端
	 * @param 关闭理由
	 * @param 是否强制关闭
	 */
	public static void closeSession(IoSession session,String reason,boolean force)
	{
		closelog.error("客户端"+session+"--->关闭，因为"+reason);
		session.close(force);
	}
}
