package com.chen.server.config;

import java.util.ArrayList;
import java.util.List;
/**
 * 服务器信息配置类，用xml加载
 * @author chen
 *
 */
public class ClientServerConfig extends ServerConfig
{
	private List<ServerInfo> gateServers = new ArrayList<ServerInfo>();
	private ServerInfo CenterServer;
	private ServerInfo publicServer;
	public List<ServerInfo> getGateServers() {
		return gateServers;
	}
	public void setGateServers(List<ServerInfo> gateServers) {
		this.gateServers = gateServers;
	}
	public ServerInfo getCenterServer() {
		return CenterServer;
	}
	public void setCenterServer(ServerInfo centerServer) {
		CenterServer = centerServer;
	}
	public ServerInfo getPublicServer() {
		return publicServer;
	}
	public void setPublicServer(ServerInfo publicServer) {
		this.publicServer = publicServer;
	}
}
