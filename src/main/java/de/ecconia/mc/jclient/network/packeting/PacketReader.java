package de.ecconia.mc.jclient.network.packeting;

public class PacketReader
{
	private final byte[] data;
	private int offset = 0;
	
	public PacketReader(byte[] data)
	{
		this.data = data;
	}
	
	private int next()
	{
		return data[offset++] & 255;
	}
	
	private int nextUnsigned()
	{
		return data[offset++] & 255;
	}
	
	//Normal data type readers:

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
		long i = next();
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

	public int remaining()
	{
		return data.length - offset;
	}
}
