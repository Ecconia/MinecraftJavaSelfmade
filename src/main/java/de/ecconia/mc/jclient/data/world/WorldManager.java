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
		
		if(world3DHandler != null)
		{
			world3DHandler.reset();
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
			Logger.perr("Received load chunk packet before a respawn packet.");
			return; //Ignore
		}
		
		if(world3DHandler != null)
		{
			world3DHandler.loadChunk(chunk);
		}
		
		playerWorld.loadChunk(chunk);
	}
	
	/**
	 * Called when chunks subchunks get updated instead of the whole one.
	 */
	public void updateChunk(Chunk chunk)
	{
		if(playerWorld == null)
		{
			Logger.perr("Received update chunk packet before a respawn packet.");
			return; //Ignore
		}
		
		playerWorld.updateChunk(chunk);
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

	//3D handler:
	private World3DHandler world3DHandler;
	
	public interface World3DHandler
	{
		void reset();
		void loadChunk(Chunk chunk);
	}
	
	public void addNew3DHandler(World3DHandler handler)
	{
		world3DHandler = handler;
	}
}
