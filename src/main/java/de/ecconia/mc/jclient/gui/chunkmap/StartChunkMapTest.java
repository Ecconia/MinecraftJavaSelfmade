package de.ecconia.mc.jclient.gui.chunkmap;

import de.ecconia.mc.jclient.gui.monitor.L;

public class StartChunkMapTest
{
	public static void main(String[] args)
	{
		L.init();
		
		ChunkMap cMap = new ChunkMap();
		
		L.addCustomPanel("Chunks", cMap);
		
		cMap.load(7, -1);
		cMap.load(20, 2);
		cMap.load(6, 3);
		cMap.load(3, 5);
	}
}
