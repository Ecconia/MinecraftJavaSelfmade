package de.ecconia.mc.jclient.network.processor;

import de.ecconia.mc.jclient.data.world.Chunk;
import de.ecconia.mc.jclient.data.world.MultiBlockChange;
import de.ecconia.mc.jclient.data.world.MultiBlockChange.BlockChange;
import de.ecconia.mc.jclient.data.world.SubChunk;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.gui.tabs.ChunkMap;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import de.ecconia.mc.jclient.tools.math.McMathHelper;

public class WorldPacketProcessor extends PacketThread
{
	private final ChunkMap cMap;
	
	public WorldPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("WorldPacketThread", dataDude);
		
		cMap = new ChunkMap();
		L.addCustomPanel("Chunks", cMap);
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
			int z = reader.readInt();
			logData("> X: " + x + " Z: " + z);
			
			boolean wholeChunk = reader.readBoolean();
			logData("> Whole chunk: " + wholeChunk);
			
			int subChunkBitMap = reader.readCInt();
			logData("> Subchunk map: " + asBin(subChunkBitMap, 16));
			
			Chunk chunk = new Chunk(x, z);
			
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
				int[] palette = null;
				if(bitsPerBlock <= 8)
				{
					int length = dataReader.readCInt();
					palette = new int[length];
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
				subChunks[i] = new SubChunk(bitsPerBlock, palette, dataLongs);
				
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
			
			if(wholeChunk)
			{
				dataDude.getCurrentServer().getWorldManager().loadChunk(chunk);
				cMap.load(x, z);
			}
			else
			{
				dataDude.getCurrentServer().getWorldManager().updateChunk(chunk);
				cMap.update(x, z);
			}
		}
		else if(id == 0x1F)
		{
			logPacket("Unload chunk");
			
			logData("Unloaded chunk:");
			int x = reader.readInt();
			int z = reader.readInt();
			logData("> X: " + x + " Y: " + z);
			
			dataDude.getCurrentServer().getWorldManager().unloadChunk(x, z);
			cMap.unload(x, z);
		}
		else if(id == 0x0B)
		{
			logPacket("Block changed");
			
			long positionData = reader.readLong();
			
			int x = (int) (positionData >> 38);
			int y = (int) ((positionData >> 26) & 0xFFF);
			int z = (int) (positionData << 38 >> 38);
			
			int data = reader.readCInt();
			
			logData("Block change @(" + x + ", " + y + ", " + z + ") -> " + data);
			
			int cx = McMathHelper.toChunkPos(x);
			int cz = McMathHelper.toChunkPos(z);
			
			dataDude.getCurrentServer().getWorldManager().updateBlock(cx, cz, x - McMathHelper.toStartPos(cx), y, z - McMathHelper.toStartPos(cz), data);
		}
		else if(id == 0x0F)
		{
			logPacket("Block change multi");
			
			int cx = reader.readInt();
			int cz = reader.readInt();
			
			int amount = reader.readCInt();
			
			MultiBlockChange mbc = new MultiBlockChange(cx, cz, amount);
			
			logData("MultiBlock change (" + amount + "):");
			for(int i = 0; i < amount; i++)
			{
				int posXZ = reader.readByte();
				int posX = (0xF0 & posXZ) >> 4;
				int x = posX + McMathHelper.toStartPos(cx);
				int y = reader.readByte();
				int posZ = posXZ & 0x0F;
				int z = posZ + McMathHelper.toStartPos(cz);
				int data = reader.readCInt();
				
				logData("- @(" + x + ", " + y + ", " + z + ") -> " + data);
				
				mbc.add(new BlockChange(posX, y, posZ, data));
			}
			
			dataDude.getCurrentServer().getWorldManager().updateBlock(mbc);
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
}
