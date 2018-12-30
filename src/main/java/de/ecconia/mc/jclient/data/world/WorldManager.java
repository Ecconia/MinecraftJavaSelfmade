package de.ecconia.mc.jclient.data.world;

import de.ecconia.mc.jclient.Logger;

public class WorldManager
{
	private WorldStorage playerWorld;
	
	public WorldManager()
	{
	}
	
	/**
	 * Returns the world in which the player is located officially.
	 */
	public WorldStorage getPlayerWorld()
	{
		return playerWorld;
	}
	
	public Chunk getChunk(int x, int z)
	{
		if(playerWorld == null)
		{
			return null;
		}
		
		return playerWorld.getChunk(x, z);
	}
	
	//### Incomming data ###
	
	/**
	 * Called on world switch.
	 */
	public void respawn(int dimension)
	{
		//TBI: How does multiverse handle ID's? Is it just the world type, or can it be (ab)used as identifier? What do vanilla clients do with other values?
		if(playerWorld != null)
		{
			//TODO: Implement saving of worlds, or - well dump them
			Logger.warn("Switched world, dumping old world data!");
		}
		
		playerWorld = new WorldStorage();
	}
	
	/**
	 * Called when a new complete chunk is sent.
	 */
	public void loadChunk(Chunk chunk)
	{
		if(playerWorld == null)
		{
			Logger.perr("Received load chunks packet before a respawn packet.");
			return; //Ignore
		}
		
		playerWorld.loadChunk(chunk);
	}
	
	/**
	 * Called when a chunk should be marked as unloaded.
	 */
	public void unloadChunk(int x, int z)
	{
		if(playerWorld == null)
		{
			Logger.perr("Received unload chunk packet before a respawn packet.");
			return; //Ignore
		}
		
		playerWorld.unloadChunk(x, z);
	}
}
