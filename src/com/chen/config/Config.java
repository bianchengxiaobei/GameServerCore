package com.chen.config;

import com.chen.server.Server;

public class Config 
{
	private static int id = 0;
	private static Object obj = new Object();
	public static long getId()
	{
		synchronized (obj)
		{
			id += 1;
			return (Server.server_id & 0xFFFF) << 48 | (System.currentTimeMillis() / 1000L & 0xFFFFFFFF) << 16 | id & 0xFFFF;
		}
	}
}
