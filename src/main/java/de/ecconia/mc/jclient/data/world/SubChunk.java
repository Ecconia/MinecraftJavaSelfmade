package de.ecconia.mc.jclient.data.world;

public class SubChunk
{
	public final int bitsPerBlock;
	public final long[] blocks;
	
	public SubChunk()
	{
		bitsPerBlock = 0;
		blocks = new long[0];
	}
	
	public SubChunk(int bitsPerBlock, long[] dataLongs)
	{
		this.bitsPerBlock = bitsPerBlock;
		this.blocks = dataLongs;
	}
	
	public int getBitsPerBlock()
	{
		return bitsPerBlock;
	}
	
	public long[] getBlocks()
	{
		return blocks;
	}
}
