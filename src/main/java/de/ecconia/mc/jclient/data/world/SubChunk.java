package de.ecconia.mc.jclient.data.world;

public class SubChunk
{
	private int bitsPerBlock;
	private int[] palette;
	private long[] longs;
	
	public SubChunk()
	{
		bitsPerBlock = 0;
		palette = null;
		longs = new long[0];
	}
	
	public SubChunk(int bitsPerBlock, int[] palette, long[] dataLongs)
	{
		this.bitsPerBlock = bitsPerBlock;
		this.palette = palette;
		this.longs = dataLongs;
	}
	
	public int getBitsPerBlock()
	{
		return bitsPerBlock;
	}
	
	public long[] getBlocks()
	{
		return longs;
	}
	
	public int[] getPalette()
	{
		return palette;
	}
	
	public boolean isEmpty()
	{
		return bitsPerBlock == 0;
	}
	
	public void update(int x, int y, int z, int blockdata)
	{
		//TODO: Create method to modify existing longs
		int[][][] blocks = toBlockArray();
		
		if(palette != null)
		{
			Integer palleteIndex = changePalette(x, y, z, blockdata, blocks);
			if(palleteIndex == null)
			{
				//All good.
				return;
			}
			
			blockdata = palleteIndex;
		}
		else if(bitsPerBlock == 0)
		{
			//Create new pallete, the sub-chunk had been empty up to now.
			bitsPerBlock = 4;
			palette = new int[2];
			palette[1] = blockdata;
			
			//Apply the first index of the new palette.
			blocks[x][z][y] = 1;
			createLongsFromBlockArray(blocks);
			
			//TBI: Directly update the longs, or convert the blocks array back.
			//this.blocks = new long[256]; //Init the long array, if not replaced by converting.
			return;
		}
		
		//The blockdata will be stored as is, cause there is no palette.
		blocks[x][z][y] = blockdata;
		createLongsFromBlockArray(blocks);
		
		//TBI: Directly update the longs, or convert the blocks array back.
	}
	
	private Integer changePalette(int x, int y, int z, int newBlockData, int[][][] blocks)
	{
		int oldBlockDataIndex = blocks[x][z][y];
		int oldBlockData = palette[oldBlockDataIndex];
		
		//No changes to pallete, why did the update even occur?
		if(oldBlockData == newBlockData)
		{
			return oldBlockDataIndex;
		}
		
		//Wether the new blockdata already exists in the palette:
		boolean haveToAddToPalette;
		//Wether the removed blockdata has to be removed from palette:
		boolean haveToRemoveFromPalette = false;
		
		//Find the new block state in the palette:
		Integer newBlockDataIndex = paletteIndex(newBlockData);
		haveToAddToPalette = newBlockDataIndex == null;
		
		//Check how often the to-be-removed block exists, if it wasn't AIR:
		if(oldBlockData != 0)
		{
			int foundIt = 0;
			loop: for(int cx = 0; cx < 16; cx++)
			{
				for(int cy = 0; cy < 16; cy++)
				{
					for(int cz = 0; cz < 16; cz++)
					{
						if(blocks[cx][cz][cy] == oldBlockDataIndex)
						{
							if(foundIt++ == 1)
							{
								break loop;
							}
						}
					}
				}
			}
			
			//The block only exists one time, it has to be removed from the palette.
			haveToRemoveFromPalette = foundIt == 1;
		}
		
		if(haveToRemoveFromPalette && haveToAddToPalette)
		{
			palette[oldBlockDataIndex] = newBlockData;
			return oldBlockDataIndex;
		}
		
		if(haveToRemoveFromPalette && !haveToAddToPalette)
		{
			//TBI: Ignore shrinking the bits?
			int[] newPalette = new int[palette.length - 1];
			
			System.arraycopy(palette, 0, newPalette, 0, oldBlockDataIndex);
			if(oldBlockDataIndex < palette.length - 1)
			{
				System.arraycopy(palette, oldBlockDataIndex + 1, newPalette, oldBlockDataIndex, palette.length - oldBlockDataIndex - 1);
			}
			palette = newPalette;
			
			//Refactor all values above oldBlockDataIndex (minus 1):
			for(int cx = 0; cx < 16; cx++)
			{
				for(int cy = 0; cy < 16; cy++)
				{
					for(int cz = 0; cz < 16; cz++)
					{
						int index = blocks[cx][cz][cy];
						if(index > oldBlockDataIndex)
						{
							blocks[cx][cz][cy] = index - 1;
						}
					}
				}
			}
			
			createLongsFromBlockArray(blocks);
			
			//Also subtract this index, cause an entry got removed (minus 1)
			if(oldBlockDataIndex > newBlockDataIndex)
			{
				return newBlockDataIndex;
			}
			else
			{
				return newBlockDataIndex - 1;
			}
		}
		
		if(!haveToRemoveFromPalette && haveToAddToPalette)
		{
			//Have to extend the palette, lets pray it doesn't need to be extended.
			int maxDirectory = (int) Math.pow(2, bitsPerBlock);
			boolean haveToExtendBits = palette.length == maxDirectory;
			
			//Increase the palette data by one, to add the new blockstate
			newBlockDataIndex = palette.length;
			int[] newPalette = new int[newBlockDataIndex + 1];
			System.arraycopy(palette, 0, newPalette, 0, newBlockDataIndex);
			newPalette[newBlockDataIndex] = newBlockData;
			palette = newPalette;
			
			if(haveToExtendBits)
			{
				bitsPerBlock++;
				if(bitsPerBlock > 8)
				{
					bitsPerBlock = 14;
				}
				
				blocks[x][z][y] = newBlockDataIndex;
				createLongsFromBlockArray(blocks);
				//Return null, abort further processing.
				return null;
			}
			else
			{
				return newBlockDataIndex;
			}
		}
		
		if(!haveToRemoveFromPalette && !haveToAddToPalette)
		{
			//Don't need to add it, nor remove something? Great return the new index.
			return newBlockDataIndex;
		}
		
		//Never happens:
		return 0;
	}
	
	private void createLongsFromBlockArray(int[][][] blocks)
	{
		int longAmount = 64 * bitsPerBlock;
		longs = new long[longAmount];
		
		int maxBit = 1 << bitsPerBlock;
		
		long longSetBit = 1;
		int longSetBitNumber = 1;
		int longNumber = 0;
		
		for(int y = 0; y < 16; y++)
		{
			for(int z = 0; z < 16; z++)
			{
				for(int x = 0; x < 16; x++)
				{
					int value = blocks[x][z][y];
					
					for(int cBit = 1; cBit < maxBit; cBit <<= 1)
					{
						if(longSetBitNumber > 64)
						{
							longSetBit = 1;
							longSetBitNumber = 1;
							longNumber++;
						}
						
						if((value & cBit) != 0)
						{
							this.longs[longNumber] |= longSetBit;
						}
						
						//Shift:
						longSetBit <<= 1;
						longSetBitNumber++;
					}
				}
			}
		}
	}
	
	private Integer paletteIndex(int value)
	{
		for(int i = 0; i < palette.length; i++)
		{
			if(palette[i] == value)
			{
				return i;
			}
		}
		
		return null;
	}
	
	private int[][][] toBlockArray()
	{
		int[][][] blocks = new int[16][16][16];
		
		if(bitsPerBlock == 0)
		{
			for(int y = 0; y < 16; y++)
			{
				for(int x = 0; x < 16; x++)
				{
					for(int z = 0; z < 16; z++)
					{
						blocks[x][z][y] = 0;
					}
				}
			}
		}
		else
		{
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
							
							if((this.longs[longNumber] & longProbeBit) != 0)
							{
								tmp |= cBit;
							}
							
							//Shift:
							longProbeBit <<= 1;
							longProbeBitNumber++;
						}
						
						blocks[x][z][y] = tmp;
					}
				}
			}
		}
		
		return blocks;
	}
	
	public void printData()
	{
		System.out.println("Bits per: " + bitsPerBlock);
		int p = 0;
		for(long l : longs)
		{
			String tmp = "";
			String sBits = Long.toBinaryString(l);
			while(sBits.length() < 64)
			{
				sBits = '0' + sBits;
			}
			char[] bits = sBits.toCharArray();
			for(int ii = bits.length-1; ii >= 0; ii--)
			{
				char bit  = bits[ii];
				if(p++ == bitsPerBlock)
				{
					p = 1;
					tmp = ' ' + tmp;
				}
				tmp = bit + tmp;
			}
			System.out.println(tmp);
		}
		
		System.out.println("Palette:");
		for(int ii = 0; ii < palette.length; ii++)
		{
			System.out.println(ii + ": " + palette[ii]);
		}
	}
}
