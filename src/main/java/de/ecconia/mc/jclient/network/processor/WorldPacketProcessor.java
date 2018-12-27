package de.ecconia.mc.jclient.network.processor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.gl.Simple3D;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.gui.tabs.ChunkMap;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;

public class WorldPacketProcessor extends PacketThread
{
	private final ChunkMap cMap;
	private final ChunkStorage chunks = new ChunkStorage();
	
	public WorldPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("WorldPacketThread", dataDude);
		
		cMap = new ChunkMap();
		L.addCustomPanel("Chunks", cMap);
		L.addCustomPanel("3D", new Simple3D(this, dataDude));
	}
	
	@Override
	protected void process(GenericPacket packet)
	{
		PacketReader reader = new PacketReader(packet.getBytes());
		int id = packet.getId();
		
		if(id == 0x22)
		{
			logPacket("Chunk Data");
			
			logData("Loaded chunk:");
			int x = reader.readInt();
			int y = reader.readInt();
			logData("> X: " + x + " Y: " + y);
			
			boolean wholeChunk = reader.readBoolean();
			logData("> Whole chunk: " + wholeChunk);
			
			if(x == -168 && y == 168)
			{
				byte[] bytes = reader.readBytes(reader.remaining());
				
				try(FileOutputStream fos = new FileOutputStream("exportedChunk"))
				{
					fos.write(bytes);
				}
				catch(FileNotFoundException e)
				{
					System.out.println("FNF: " + e);
				}
				catch(IOException e)
				{
					System.out.println("IOE: " + e);
				}
				
				return;
			}
			
			int subChunkBitMap = reader.readCInt();
			logData("> Subchunk map: " + asBin(subChunkBitMap, 16));
			
			cMap.load(x, y);
			
			Chunk chunk = new Chunk(x, y);
			
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
				
				logData(">> SubChunk: " + i);
				
				//BPB
				int bitsPerBlock = dataReader.readUByte();
				logData(">>>> Bits-Per-Block: " + bitsPerBlock);
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
//						System.out.println("ID-Map: " + j + " -> " + palette[j]);
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
				logData("REMAINING: " + dataReader.remaining());
			}
			
			int nbtAmount = reader.readCInt();
			logData(">> NBT Entries: " + nbtAmount + " Size: " + reader.remaining() + " bytes");
			
			chunks.put(x, y, chunk);
		}
		else if(id == 0x0B)
		{
			logPacket("Block changed");
		}
		else if(id == 0x0F)
		{
			logPacket("Block change multi");
		}
		else if(id == 0x1F)
		{
			logPacket("Unload chunk");
			
			logData("Unloaded chunk:");
			logData("> X: " + reader.readInt() + " Y: " + reader.readInt());
		}
		else
		{
			System.out.println(Thread.currentThread().getName() + " received packet it was not surposed to get: " + id);
		}
	}
	
	private String asBin(int i, int length)
	{
		String bin = Integer.toBinaryString(i);
		
		while(bin.length() < length)
		{
			bin = '0' + bin;
		}
		
		return bin;
	}
	
	private SubChunk[] prepareSubChunkMap(int input)
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
	
	private void logPacket(String name)
	{
		L.writeLineOnChannel("P: World", name);
	}
	
	private void logData(String message)
	{
		L.writeLineOnChannel("C: World", message);
	}
	
	private static class ChunkStorage
	{
		private final Map<Integer, Map<Integer, Chunk>> xList = new HashMap<>();
		
		public void put(int x, int y, Chunk chunk)
		{
			Map<Integer, Chunk> yList = xList.get(x);
			if(yList == null)
			{
				yList = new HashMap<>();
				xList.put(x, yList);
			}
			
			chunk = yList.put(y, chunk);
			if(chunk != null)
			{
				//Its just a simple update, not really important later on.
				System.out.println("WARNING, chunk has been overwritten!");
			}
		}
		
		public Chunk get(int x, int y)
		{
			Map<Integer, Chunk> yList = xList.get(x);
			if(yList == null)
			{
				return null;
			}
			
			Chunk c = yList.get(y);
			return c;
		}
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
	
	public int[][][] getProcessedChunk(int cx, int cz)
	{
		Chunk chunk = chunks.get(cx, cz);
		int[][][] blocks = new int[16][16][256];
		
		if(chunk == null)
		{
			L.writeLineOnChannel("3D-Text", "Chunk (" + cx + ", " + cz + ") is not loaded yet. Can not display it.");
			return blocks;
		}
		else
		{
			L.writeLineOnChannel("3D-Text", "Chunk (" + cx + ", " + cz + ") will now be processed to display it.");
		}
		
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
				
//				for(int y = 0; y < 16; y++)
//				{
//					for(int x = 0; x < 16; x++)
//					{
//						for(int z = 0; z < 16; z++)
//						{
//							//TODO: Optimize access!
//							blocks[x][z][y + yOffset] = 0;
//						}
//					}
//				}
				
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

//0 -> Air
//33 -> Bedrock
//10 -> Dirt
