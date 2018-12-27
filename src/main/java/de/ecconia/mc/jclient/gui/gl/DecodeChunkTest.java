package de.ecconia.mc.jclient.gui.gl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.packeting.PacketReader;

public class DecodeChunkTest
{
	private static Chunk readchunk;
	
	public static void main(String[] args)
	{
		try(FileInputStream fis = new FileInputStream("exportedChunk"))
		{
			int size = fis.available();
			System.out.println("Size: " + size + " bytes");
			byte[] bytes = new byte[size];
			int read = fis.read(bytes);
			if(read != size)
			{
				System.out.println("Error, failed reading chunk from file!");
				return;
			}
			else
			{
				processChunk(new PacketReader(bytes));
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		L.addCustomPanel("3D", new YA3DTest());
	}
	
	private static void processChunk(PacketReader reader)
	{
		boolean wholeChunk = true;
		
		int subChunkBitMap = reader.readCInt();
		
		Chunk chunk = new Chunk(-168, 168);
		
		int chunkDataSize = reader.readCInt();
		//For now lets read all the bytes, cause its defined this way.
		PacketReader dataReader = new PacketReader(reader.readBytes(chunkDataSize));
		SubChunk[] subChunks = prepareSubChunkMap(subChunkBitMap);
		
		for(int i = 0; i < 16; i++)
		{
			if(subChunks[i] != null)
			{
				continue;
			}
			
			//BPB
			int bitsPerBlock = dataReader.readUByte();
			if(bitsPerBlock < 4)
			{
				bitsPerBlock = 4;
			}
			//Palette
			if(bitsPerBlock <= 8)
			{
				int length = dataReader.readCInt();
				int[] palette = new int[length];
				for(int j = 0; j < length; j++)
				{
					palette[j] = dataReader.readCInt();
//					System.out.println("ID-Map: " + j + " -> " + palette[j]);
				}
			}
			else
			{
				//No fields, bits-per-block is: 14 (For Forge it may be more)
				bitsPerBlock = 14;
			}
			
			//Data Array Length:
			int dataArrayLength = dataReader.readCInt();
			//Long array:
			long[] dataLongs = new long[dataArrayLength];
			for(int j = 0; j < dataArrayLength; j++)
			{
				dataLongs[j] = dataReader.readLong();
			}
			
			//Seems that all the 4-8 bits allways fill up the longs.
			//System.out.println(">>> Bits: " + bitsPerBlock + " -> " + (4096 * bitsPerBlock) + "/" + (dataLongs.length * 8 * 8));
			subChunks[i] = new SubChunk(bitsPerBlock, dataLongs);
			
			//Read Block light.
			for(int j = 0; j < 2048; j++)
			{
				dataReader.readByte();
			}
			
			//TODO: If overworld!
			if(true)
			{
				//Read Sky light.
				for(int j = 0; j < 2048; j++)
				{
					dataReader.readByte();
				}
			}
		}
		
		chunk.setChunkMap(subChunks);
		
		if(wholeChunk)
		{
			int[] biomeMap = new int[256];
			for(int i = 0; i < 256; i++)
			{
				biomeMap[i] = dataReader.readInt();
			}
			
			//TODO: Use this map...
		}
		
		if(dataReader.remaining() != 0)
		{
			System.out.println("WARNING: didn't finish reading chunk! Rem: " + dataReader.remaining());
		}
		
//		int nbtAmount = reader.readCInt();
		
		readchunk = chunk;
	}
	
	private static String asBin(int i, int length)
	{
		String bin = Integer.toBinaryString(i);
		
		while(bin.length() < length)
		{
			bin = '0' + bin;
		}
		
		return bin;
	}
	
	private static SubChunk[] prepareSubChunkMap(int input)
	{
		SubChunk[] map = new SubChunk[16];
		
		int masq = 1;
		
		for(int i = 0; i < 16; i++)
		{
			if((input & masq) == 0)
			{
				map[i] = new SubChunk();
			}
			
			masq <<= 1;
		}
		
		return map;
	}
	
	private static class Chunk
	{
		private SubChunk[] chunkMap;
		
		public Chunk(int x, int y)
		{
		}
		
		public void setChunkMap(SubChunk[] chunkMap)
		{
			this.chunkMap = chunkMap;
		}
		
		public SubChunk[] getChunkMap()
		{
			return chunkMap;
		}
	}
	
	private static class SubChunk
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
	
	public static int[][][] getProcessedChunk()
	{
		Chunk chunk = readchunk;
		int[][][] blocks = new int[16][16][256];
		
		for(int i = 0; i < 16; i++)
		{
			int yOffset = i * 16;
			
			SubChunk subChunk = chunk.getChunkMap()[i];
			if(subChunk.getBitsPerBlock() == 0)
			{
				for(int y = 0; y < 16; y++)
				{
					for(int x = 0; x < 16; x++)
					{
						for(int z = 0; z < 16; z++)
						{
							//TODO: Optimize access!
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
					for(int x = 0; x < 16; x++)
					{
						for(int z = 0; z < 16; z++)
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
							
							//TODO: Optimize access!
							blocks[x][z][y + yOffset] = tmp;
						}
					}
				}
			}
		}
		
		return blocks;
	}
}
