package com.chen.mina.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.chen.mina.IServer;
import com.chen.mina.code.InnerServerProtocolCodecFactory;
import com.chen.mina.handle.ServerProtocolHandler;
import com.chen.server.Server;
import com.chen.server.config.ClientServerConfig;
import com.chen.server.config.ServerInfo;
import com.chen.server.loader.ClientServerConfigXmlLoader;

/**
 * 内部客户服务器
 * @author chen
 *
 */
public abstract class ClientServer extends Server implements IServer
 {
	public static int gateSessionNumber = 1;

	public static int centerSessionNumber = 1;

	public static int publicSessionNumber = 1;
	protected Logger log = LogManager.getLogger(ClientServer.class);
	protected HashMap<Integer , List<IoSession>> gateSessions = new HashMap<Integer , List<IoSession>>();
	protected List<IoSession> centerSessions = new ArrayList<IoSession>();
	protected NioSocketConnector socket = null;
	protected ClientServer(String serverConfig)
	{
		this(serverConfig,gateSessionNumber,centerSessionNumber);
	}
	protected ClientServer(String serverConfig,int gSessionNumber,int wSessionNumber)
	{
		super(new ClientServerConfigXmlLoader().load(serverConfig));
		gateSessionNumber = gSessionNumber;
		centerSessionNumber = wSessionNumber;
	}
	protected void init() 
	{
		super.init();
	}
	public void run()
	{
		super.run();
		this.socket = new NioSocketConnector();
		this.socket.getFilterChain().addLast("codec", new ProtocolCodecFilter(new InnerServerProtocolCodecFactory()));
		OrderedThreadPoolExecutor threadpool = new OrderedThreadPoolExecutor(500);
	    this.socket.getFilterChain().addLast("threadPool", new ExecutorFilter(threadpool));
	    
	    int recsize = 524288;
	    int sendsize = 1048576;
	    SocketSessionConfig sc = this.socket.getSessionConfig();
	    sc.setReceiveBufferSize(recsize);
	    sc.setSendBufferSize(sendsize);
	    sc.setSoLinger(0);
	    this.socket.setHandler(new ServerProtocolHandler(this));
	    ClientServerConfig config = (ClientServerConfig)this.serverConfig;
	    if (config != null)
	    {
    		System.out.println("zhong服务器："+config.getCenterServer() == null);
	    	if (config.getCenterServer() != null)
	    	{
	    		int connected = 0;
	    		while (connected < centerSessionNumber)
	    		{
	    			ConnectFuture connect = this.socket.connect(new InetSocketAddress(config.getCenterServer().getIp(),config.getCenterServer().getPort()));
	    			connect.awaitUninterruptibly(60000L);
	    			
	    			if (!connect.isConnected())
	    			{
	    				try {
							Thread.sleep(5000L);
						} catch (Exception e) {
							this.log.error(e,e);
						}
	    			}
	    			else
	    			{
	    				IoSession session = connect.getSession();
	    	            session.setAttribute("connect-server-id", Integer.valueOf(config.getCenterServer().getId()));
	    	            session.setAttribute("connect-server-ip", config.getCenterServer().getIp());
	    	            session.setAttribute("connect-server-port", Integer.valueOf(config.getCenterServer().getPort()));
	    	            addSession(session, config.getCenterServer().getId(), 3);
	    	            register(session, 3);
	    	            connected++;
	    			}
	    		}	    		
	    	}
	    	for (int i=0; i<config.getGateServers().size();i++)
    		{
	    		System.out.println("网关服务器："+config.getGateServers().size());
    			ServerInfo info = (ServerInfo)config.getGateServers().get(i);
    			int connected = 0;
    			while (connected < gateSessionNumber)
    			{
    				ConnectFuture connect = this.socket.connect(new InetSocketAddress(info.getIp(), info.getPort()));
    		          connect.awaitUninterruptibly();

    		          if (!connect.isConnected()) {
    		            try {
    		              Thread.sleep(5000L);
    		            } catch (Exception e) {
    		              this.log.error(e, e);
    		            }
    		          }
    		          else
    		          {
    		            IoSession session = connect.getSession();
    		            session.setAttribute("connect-server-id", Integer.valueOf(info.getId()));
    		            session.setAttribute("connect-server-ip", info.getIp());
    		            session.setAttribute("connect-server-port", Integer.valueOf(info.getPort()));
    		            addSession(session, info.getId(), 1);

    		            register(session, 1);

    		            connected++;
    		          }
    		    }
    		}
			if (config.getPublicServer() != null) {
				int connected = 0;
				while (connected < publicSessionNumber) {
					ConnectFuture connect = this.socket
							.connect(new InetSocketAddress(config
									.getPublicServer().getIp(), config
									.getPublicServer().getPort()));
					connect.awaitUninterruptibly(60000L);

					if (!connect.isConnected()) {
						try {
							Thread.sleep(5000L);
						} catch (Exception e) {
							this.log.error(e, e);
						}
					} else {
						IoSession session = connect.getSession();
						session.setAttribute("connect-server-id", Integer
								.valueOf(config.getPublicServer().getId()));
						session.setAttribute("connect-server-ip", config
								.getPublicServer().getIp());
						session.setAttribute("connect-server-port", Integer
								.valueOf(config.getPublicServer().getPort()));
						addSession(session, config.getPublicServer().getId(), 4);

						register(session, 4);

						connected++;
					}
				}
			}
	    }
	    connectComplete();
	}
	public void addSession(IoSession session, int id, int type)
	{
		if (type == 1)
		{
			synchronized (this.gateSessions) {
				List<IoSession> sessions = (List<IoSession>)this.gateSessions.get(Integer.valueOf(id));
				if (sessions == null)
				{
					sessions = new ArrayList<IoSession>();
					this.gateSessions.put(Integer.valueOf(id), sessions);
				}
				sessions.add(session);
			}
		}
		if (type == 3)
		{
			synchronized (this.centerSessions) {
				this.centerSessions.add(session);
			}
		}
	}

	protected void removeSession(IoSession session, int id, int type) {
		if (type == 1) {
			synchronized (this.gateSessions) {
				List<IoSession> sessions = (List<IoSession>) this.gateSessions.get(Integer
						.valueOf(id));
				if (sessions != null)
					sessions.remove(session);
			}
		}
		if (type == 3)
			synchronized (this.centerSessions) {
				this.centerSessions.remove(session);
			}
	}
	public List<IoSession> getCenterSession()
	{
		return this.centerSessions;
	}
	public HashMap<Integer, List<IoSession>> getGateSession()
	{
		return this.gateSessions;
	}
	public abstract void register(IoSession paramIoSession, int paramInt);
	protected abstract void connectComplete();
}
