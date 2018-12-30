package de.ecconia.mc.jclient.data.world;

import java.util.HashMap;
import java.util.Map;

import de.ecconia.mc.jclient.Logger;

public class WorldStorage
{
	private final ChunkStorage chunks = new ChunkStorage();
	
	public Chunk getChunk(int x, int z)
	{
		return chunks.get(x, z);
	}
	
	//### ###
	
	public void loadChunk(Chunk chunk)
	{
		int x = chunk.getX();
		int z = chunk.getZ();
		
		Chunk oldChunk = chunks.put(x, z, chunk);
		
		if(oldChunk != null && oldChunk.isLoaded())
		{
			Logger.perr("Received load chunk packet, but the old chunk was still loaded. (" + x + ", " + z + ")");
		}
	}

	public void unloadChunk(int x, int z)
	{
		Chunk chunk = chunks.get(x, z);
		
		if(chunk == null)
		{
			Logger.perr("Received chunk unload packet, but chunk was never loaded. (" + x + ", " + z + ")");
			return;
		}
		
		if(!chunk.isLoaded())
		{
			Logger.perr("Received chunk unload packet, but chunk was already unloaded. (" + x + ", " + z + ")");
			return;
		}
		
		chunk.unload();
	}
	
	//TBI: Primitive chunk storage model, optimize? How, why? 
	private static class ChunkStorage
	{
		private final Map<Integer, Map<Integer, Chunk>> xList = new HashMap<>();
		
		public Chunk put(int x, int z, Chunk chunk)
		{
			Map<Integer, Chunk> zList = xList.get(x);
			if(zList == null)
			{
				zList = new HashMap<>();
				xList.put(x, zList);
			}
			
			return zList.put(z, chunk);
		}
		
		public Chunk get(int x, int z)
		{
			Map<Integer, Chunk> zList = xList.get(x);
			if(zList == null)
			{
				return null;
			}
			
			Chunk c = zList.get(z);
			return c;
		}
	}
}
