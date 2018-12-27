package de.ecconia.mc.jclient.tools;

public class CIntUntils
{
	public static IntBytes readCInt(byte[] bytes)
	{
		int pointer = 0;
		int value = 0;
		
		byte read;
		do
		{
			read = bytes[pointer];
			value |= (read & 127) << (pointer * 7);
			
			//Increment after calculation.
			if(pointer++ > 5)
			{
				throw new RuntimeException("VarInt too big");
			}
		}
		while((read & 128) == 128);
		
		//Remove first bytes.
		byte[] output = new byte[bytes.length - pointer];
		System.arraycopy(bytes, pointer, output, 0, output.length);
		
		return new IntBytes(value, output);
	}
	
	public static byte[] prependCInt(byte[] bytes, int i)
	{
		byte[] buffer = new byte[6];
		int pointer = 0;
		
		while((i & -128) != 0)
		{
			buffer[pointer++] = (byte) (i & 127 | 128);
			i >>>= 7;
		}
		
		buffer[pointer++] = (byte) i;
		
		byte[] output = new byte[bytes.length + pointer];
		System.arraycopy(buffer, 0, output, 0, pointer);
		System.arraycopy(bytes, 0, output, pointer, bytes.length);
		
		return output;
	}
}
