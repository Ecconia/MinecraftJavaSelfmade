package de.ecconia.mc.jclient.data.world;

import java.util.ArrayList;
import java.util.List;

import de.ecconia.mc.jclient.data.world.MultiBlockChange.BlockChange;
import de.ecconia.mc.jclient.main.Logger;

public class WorldManager
{
	private WorldStorage playerWorld;
	
	private final List<WorldObserver> observers = new ArrayList<>();
	
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
		Logger.important("World ID is: " + dimension);
		if(playerWorld != null)
		{
			//TODO: Implement saving of worlds, or - well dump them
			Logger.warn("Switched world, dumping old world data!");
		}
		
		playerWorld = new WorldStorage();
		
		for(WorldObserver observer : observers)
		{
			observer.switchWorld();
		}
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
		
		playerWorld.loadChunk(chunk);
		
		//Continue even if it was already loaded, cause chunk is overwritten now.
		for(WorldObserver observer : observers)
		{
			observer.loadChunk(chunk);
		}
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
		
		Chunk realChunk = playerWorld.updateChunk(chunk);
		if(realChunk != null)
		{
			for(WorldObserver observer : observers)
			{
				observer.dirtyChunk(realChunk);
			}
		}
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
		
		if(playerWorld.unloadChunk(x, z))
		{
			for(WorldObserver observer : observers)
			{
				observer.unloadChunk(x, z);
			}
		}
	}
	
	public void updateBlock(int chunkX, int chunkZ, int x, int y, int z, int blockdata)
	{
		Chunk chunk = getChunk(chunkX, chunkZ);
		if(chunk == null || !chunk.isLoaded())
		{
			Logger.perr("Received block change packet but chunk was not loaded: " + chunkX + " " + chunkZ);
			return; //Ignore
		}
		
		chunk.updateBlock(x, y, z, blockdata);
		for(WorldObserver observer : observers)
		{
			observer.dirtyChunk(chunk);
		}
	}
	
	public void updateBlock(MultiBlockChange mbc)
	{
		Chunk chunk = getChunk(mbc.getX(), mbc.getZ());
		if(chunk == null || !chunk.isLoaded())
		{
			Logger.perr("Received multi block change packet but chunk was not loaded: " + mbc.getX() + " " + mbc.getZ());
			return; //Ignore
		}
		
		for(BlockChange change : mbc.getChanges())
		{
			chunk.updateBlock(change.getX(), change.getY(), change.getZ(), change.getData());
		}
		
		for(WorldObserver observer : observers)
		{
			observer.dirtyChunk(chunk);
		}
	}
	
	public void observe(WorldObserver observer)
	{
		observers.add(observer);
	}
}
