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
	
	public int[][][] toBlockArray()
	{
		int[][][] blocks = new int[16][16][256];
		
		for(int i = 0; i < 16; i++)
		{
			int yOffset = i * 16;
			
			SubChunk subChunk = getChunkMap()[i];
			if(subChunk.getBitsPerBlock() == 0)
			{
				for(int y = 0; y < 16; y++)
				{
					for(int x = 0; x < 16; x++)
					{
						for(int z = 0; z < 16; z++)
						{
							blocks[x][z][y + yOffset] = 0;
						}
					}
				}
			}
			else
			{
				long[] longs = subChunk.getBlocks();
				
				int bitsPerBlock = subChunk.getBitsPerBlock();
				int maxBit = 1 << bitsPerBlock;
				
				long longProbeBit = 1;
				int longProbeBitNumber = 1;
				int longNumber = 0;
				
				for(int y = 0; y < 16; y++)
				{
					for(int z = 0; z < 16; z++)
					{
						for(int x = 0; x < 16; x++)
						{
							int tmp = 0;
							
							for(int cBit = 1; cBit < maxBit; cBit <<= 1)
							{
								if(longProbeBitNumber > 64)
								{
									longProbeBit = 1;
									longProbeBitNumber = 1;
									longNumber++;
								}
								
								if((longs[longNumber] & longProbeBit) > 0)
								{
									tmp |= cBit;
								}
								
								//Shift:
								longProbeBit <<= 1;
								longProbeBitNumber++;
							}
							
							blocks[x][z][y + yOffset] = tmp;
						}
					}
				}
				
				if(subChunk.getPalette() != null)
				{
					DebugPalette debug = new DebugPalette(subChunk.getPalette());
					debug.test(blocks, yOffset);
					
					for(int y = 0; y < 16; y++)
					{
						for(int z = 0; z < 16; z++)
						{
							for(int x = 0; x < 16; x++)
							{
								int val = blocks[x][z][y + yOffset];
								blocks[x][z][y + yOffset] = subChunk.getPalette()[val];
							}
						}
					}
				}
			}
		}
		
		return blocks;
	}
}
