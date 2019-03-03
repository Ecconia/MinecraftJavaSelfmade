package de.ecconia.mc.jclient.data.world;

public interface WorldObserver
{
	public void loadChunk(Chunk chunk);
	
	public void unloadChunk(int x, int z);
	
	public void dirtyChunk(Chunk chunk);
	
	public void switchWorld();
}
