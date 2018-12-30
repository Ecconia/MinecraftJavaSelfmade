package de.ecconia.mc.jclient.data;

import de.ecconia.mc.jclient.data.world.WorldManager;

public class ServerData
{
	//World storage
	private final WorldManager worldManager;
	
	//Online player storage
	
	//Entities
	
	public ServerData()
	{
		worldManager = new WorldManager();
	}
	
	public WorldManager getWorldManager()
	{
		return worldManager;
	}
}
