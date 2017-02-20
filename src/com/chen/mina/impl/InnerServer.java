package com.chen.mina.impl;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.chen.cache.executor.OrderedQueuePoolExecutor;
import com.chen.mina.IServer;
import com.chen.mina.code.InnerServerProtocolCodecFactory;
import com.chen.mina.handle.ServerProtocolHandler;
import com.chen.server.Server;
import com.chen.server.config.InnerServerConfig;
import com.chen.server.loader.InnerServerConfigXmlLoader;

public abstract class InnerServer extends Server implements IServer
{
	private int port;
	protected NioSocketAcceptor acceptor;
	
	public InnerServer(String serverConfig)
	{
		super(new InnerServerConfigXmlLoader().load(serverConfig));
	}
	protected void init() {
		super.init();
		this.port = ((InnerServerConfig)this.serverConfig).getPort();
	}
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
		private InnerServer server;
		
		public ConnectServer(InnerServer server)
		{
			this.server = server;
		}
		public void run()
		{
			InnerServer.this.acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = InnerServer.this.acceptor.getFilterChain();
			chain.addLast("codec", new ProtocolCodecFilter(new InnerServerProtocolCodecFactory()));
			OrderedThreadPoolExecutor threadPool = new OrderedThreadPoolExecutor(500);
			chain.addLast("threadPool", new ExecutorFilter(threadPool));
			
			int recsize = 524288;
			int sendsize = 1048576;
			
			InnerServer.this.acceptor.setReuseAddress(true);
			SocketSessionConfig sc = InnerServer.this.acceptor.getSessionConfig();
			sc.setReuseAddress(true);
			sc.setReceiveBufferSize(recsize);
			sc.setSendBufferSize(sendsize);
			sc.setTcpNoDelay(true);
			sc.setSoLinger(0);
			
			InnerServer.this.acceptor.setHandler(new ServerProtocolHandler(this.server));
			try {
				InnerServer.this.acceptor.bind(new InetSocketAddress(
						InnerServer.this.port));
				this.log.info("内部服务器 " + this.server.getServer_name()
						+ " 开启，端口为： " + InnerServer.this.port);
			} catch (IOException e) {
				this.log.error("内部服务器 " + this.server.getServer_name()
						+ " 端口 " + InnerServer.this.port + "已经被占用:"
						+ e.getMessage());
				System.exit(1);
			}
		}
	}
}
