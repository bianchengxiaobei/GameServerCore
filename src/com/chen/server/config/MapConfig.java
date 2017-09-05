package com.chen.server.config;

public class MapConfig
{
	private int serverId;
	  private int channelId;
	  private int mapId;
	  private int mapModelId;

	  public int getServerId()
	  {
	    return this.serverId;
	  }

	  public void setServerId(int serverId) {
	    this.serverId = serverId;
	  }

	  public int getChannelId() {
	    return this.channelId;
	  }

	  public void setChannelId(int lineId) {
	    this.channelId = lineId;
	  }

	  public int getMapId() {
	    return this.mapId;
	  }

	  public void setMapId(int mapId) {
	    this.mapId = mapId;
	  }

	  public int getMapModelId() {
	    return this.mapModelId;
	  }

	  public void setMapModelId(int mapModelId) {
	    this.mapModelId = mapModelId;
	  }
}
