package de.ecconia.mc.jclient.data;

import de.ecconia.mc.jclient.data.player.MainPlayer;
import de.ecconia.mc.jclient.data.players.PlayerList;
import de.ecconia.mc.jclient.data.world.WorldManager;
import de.ecconia.mc.jclient.network.connector.Sender;

public class ServerData
{
	//World storage
	private final WorldManager worldManager;
	
	//Online player storage
	private final PlayerList playerList;
	
	//Entities
	private final MainPlayer mainPlayer;
	
	public ServerData(Sender sender)
	{
		worldManager = new WorldManager();
		playerList = new PlayerList();
		mainPlayer = new MainPlayer(sender);
	}
	
	public WorldManager getWorldManager()
	{
		return worldManager;
	}
	
	public PlayerList getPlayerList()
	{
		return playerList;
	}
	
	public MainPlayer getMainPlayer()
	{
		return mainPlayer;
	}
}
