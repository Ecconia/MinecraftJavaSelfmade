package de.ecconia.mc.jclient.data.world;

public class Chunk
{
	private final int x;
	private final int z;
	
	private boolean loaded;
	
	private SubChunk[] chunkMap;
	
	public Chunk(int x, int z)
	{
		this.x = x;
		this.z = z;
		
		loaded = true;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public void setChunkMap(SubChunk[] chunkMap)
	{
		this.chunkMap = chunkMap;
	}
	
	public SubChunk[] getChunkMap()
	{
		return chunkMap;
	}
	
	public void unload()
	{
		loaded = false;
	}

	public boolean isLoaded()
	{
		return loaded;
	}
}
