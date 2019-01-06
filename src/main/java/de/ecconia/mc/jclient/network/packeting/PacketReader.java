package de.ecconia.mc.jclient.network.packeting;

import java.util.UUID;

public class PacketReader
{
	private final byte[] data;
	private int offset = 0;
	
	public PacketReader(byte[] data)
	{
		this.data = data;
	}
	
	public int remaining()
	{
		return data.length - offset;
	}
	
	private int next()
	{
		return data[offset++] & 255;
	}
	
	private int nextUnsigned()
	{
		return data[offset++] & 255;
	}
	
	public byte[] readBytes(int amount)
	{
		byte[] bytes = new byte[amount];
		
		System.arraycopy(data, offset, bytes, 0, amount);
		offset += amount;
		
		return bytes;
	}
	
	//Normal data type readers:
	
	public String readString()
	{
		return new String(readBytes(readCInt()));
	}
	
	public int readByte()
	{
		return next();
	}
	
	public int readShort()
	{
		int i = next();
		i = i << 8;
		i += nextUnsigned();
		return i;
	}
	
	public int readInt()
	{
		int i = next();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		
		return i;
	}
	
	public long readLong()
	{
		long i = nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		i = i << 8;
		i += nextUnsigned();
		
		return i;
	}
	
	public boolean readBoolean()
	{
		return next() == 1;
	}
	
	public double readDouble()
	{
		return Double.longBitsToDouble(readLong());
	}
	
	public float readFloat()
	{
		return Float.intBitsToFloat(readInt());
	}
	
	//MC custom types:
	
	public int readCInt()
	{
		int ret = 0;
		int iterations = 0;
		
		int read;
		do
		{
			read = next();
			ret |= (read & 127) << iterations++ * 7;
			
			if(iterations > 5)
			{
				throw new RuntimeException("VarInt too big");
			}
		}
		while((read & 128) == 128);
		
		return ret;
	}
	
	public int readUByte()
	{
		return nextUnsigned();
	}
	
	public UUID readUUID()
	{
		return new UUID(readLong(), readLong());
	}
}
