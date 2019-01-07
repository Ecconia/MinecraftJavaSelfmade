package de.ecconia.mc.jclient.data;

import de.ecconia.mc.jclient.data.players.PlayerList;
import de.ecconia.mc.jclient.data.world.WorldManager;

public class ServerData
{
	//World storage
	private final WorldManager worldManager;
	
	//Online player storage
	private final PlayerList playerList;
	
	//Entities
	
	public ServerData()
	{
		worldManager = new WorldManager();
		playerList = new PlayerList();
	}
	
	public WorldManager getWorldManager()
	{
		return worldManager;
	}
	
	public PlayerList getPlayerList()
	{
		return playerList;
	}
}
