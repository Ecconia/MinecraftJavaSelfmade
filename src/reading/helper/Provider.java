package reading.helper;

public abstract class Provider
{
	public abstract void reset();
	
	public abstract int getByte();
	
	public abstract int remainingBytes();
	
	public byte[] readBytes(int amount)
	{
		byte[] bytes = new byte[amount];
		
		for(int i = 0; i < amount; i++)
		{
			bytes[i] = (byte) getByte();
		}
		
		return bytes;
	}
	
	//##############################################################
	//Processors:
	
	/**
	 * Reads a basic compressed Integer.
	 */
	public int readCInt()
	{
//		System.out.println("-Reading CInt:");
		int ret = 0;
		int iterations = 0;
		
		byte read;
		do
		{
			read = (byte) getByte();
//			System.out.println(">Read: " + read);
			ret |= (read & 127) << iterations++ * 7;
			
			if(iterations > 5)
			{
				throw new RuntimeException("VarInt too big");
			}
		}
		while((read & 128) == 128);
		
//		System.out.println(">Got: " + ret + " iterations: " + iterations);
		
		return ret;
	}
	
	public String readString()
	{
		return new String(readBytes(readCInt()));
	}
	
	/**
	 * Read the simple String type, without special encoding.<br>
	 * Used in for example the legancy ping answers.
	 */
	public String readCString()
	{
		String s = "";
		
		for(int i = 0; i < getUShort(); i++)
		{
			s += (char) getUShort();
		}
		
		return s;
	}
	
	//################################################################
	//Simple types:
	
	public int getUShort()
	{
		int i = getByte();
		i = i << 8;
		i += getByte();
		
		return i;
	}

	public int readByte()
	{
		return getByte();
	}

	public long readLong()
	{
		long i = getByte();
		i = i << 8;
		i += getByte();
		i = i << 8;
		i += getByte();
		i = i << 8;
		i += getByte();
		i = i << 8;
		i += getByte();
		i = i << 8;
		i += getByte();
		i = i << 8;
		i += getByte();
		i = i << 8;
		i += getByte();
		
		return i;
	}
}
