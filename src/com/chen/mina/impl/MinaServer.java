package com.chen.mina.impl;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.chen.mina.IServer;
import com.chen.mina.code.ServerProtocolCodecFactory;
import com.chen.mina.handle.ServerProtocolHandler;
import com.chen.server.Server;
import com.chen.server.config.MinaServerConfig;
import com.chen.server.loader.MinaServerConfigXmlLoader;

public abstract class MinaServer extends Server implements IServer
{
	protected static Logger log = LogManager.getLogger(MinaServer.class);
	private int port;
	protected NioSocketAcceptor acceptor;
	protected MinaServer(String serverConfig) {
		super(new MinaServerConfigXmlLoader().load(serverConfig));
	}
	protected void init()
	{
		super.init();
		this.port = ((MinaServerConfig)this.serverConfig).getMina_port();
	}
	@Override
	public void run()
	{
		super.run();
		new Thread(new ConnectServer(this)).start();
	}

	public abstract void sessionClosed(IoSession paramIoSession);

	public abstract void exceptionCaught(IoSession paramIoSession,
			Throwable paramThrowable);

	public abstract void doCommand(IoSession paramIoSession,
			IoBuffer paramIoBuffer);
	private class ConnectServer implements Runnable
	{
		private Logger log = LogManager.getLogger(ConnectServer.class);
		private MinaServer server;
		public ConnectServer(MinaServer server)
		{
			this.server = server;
		}
		@Override
		public void run()
		{
			MinaServer.this.acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = MinaServer.this.acceptor.getFilterChain();
			chain.addLast("codec", new ProtocolCodecFilter(new ServerProtocolCodecFactory()));
			OrderedThreadPoolExecutor threadPool = new OrderedThreadPoolExecutor(500);
			chain.addLast("threadPool", new ExecutorFilter(threadPool));
			int recsize = 5120;
			int sendsize = 20480;
			int timeout = 30;
			MinaServer.this.acceptor.setReuseAddress(true);
			SocketSessionConfig sc = MinaServer.this.acceptor.getSessionConfig();
			sc.setReuseAddress(true);
			sc.setReceiveBufferSize(recsize);
			sc.setSendBufferSize(sendsize);
			sc.setTcpNoDelay(true);
			sc.setSoLinger(0);
			sc.setIdleTime(IdleStatus.READER_IDLE, timeout);
			MinaServer.this.acceptor.setHandler(new ServerProtocolHandler(this.server));
			try {
				MinaServer.this.acceptor.bind(new InetSocketAddress(MinaServer.this.port));
				this.log.info("Mina服务器"+this.server.getServer_name()+"启动，端口:"+MinaServer.this.port);
				
			} catch (Exception e) {
				this.log.error("Mina服务器"+this.server.getServer_name()+"启动失败!"+e);
				System.exit(1);
			}
		}
	}
}
