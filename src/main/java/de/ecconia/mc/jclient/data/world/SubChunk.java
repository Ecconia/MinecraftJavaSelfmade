package de.ecconia.mc.jclient.data.world;

public class SubChunk
{
	private final int bitsPerBlock;
	private final int[] palette;
	private final long[] blocks;
	
	public SubChunk()
	{
		bitsPerBlock = 0;
		palette = null;
		blocks = new long[0];
	}
	
	public SubChunk(int bitsPerBlock, int[] palette, long[] dataLongs)
	{
		this.bitsPerBlock = bitsPerBlock;
		this.palette = palette;
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
	
	public int[] getPalette()
	{
		return palette;
	}
}
