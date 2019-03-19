package de.ecconia.mc.jclient.network.packeting;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import de.ecconia.mc.jclient.network.DirtyIOException;
import de.ecconia.mc.jclient.network.tools.compression.Compressor;
import de.ecconia.mc.jclient.tools.IntBytes;

public class MessageBuilder
{
	private final LinkedList<Byte> bytes = new LinkedList<>();
	
	public void addString(String s)
	{
		byte[] string = s.getBytes();
		
		addCInt(string.length);
		addBytes(string);
	}
	
	public void addCString(String string)
	{
		addShort(string.length());
		for(int i = 0; i < string.length(); i++)
		{
			addShort(string.charAt(i));
		}
	}
	
	public void addBytes(byte[] bytes)
	{
		for(byte b : bytes)
		{
			this.bytes.addLast(b);
		}
	}
	
	public void addCInt(int i)
	{
		while((i & -128) != 0)
		{
			addByte(i & 127 | 128);
			i >>>= 7;
		}
		
		addByte(i);
	}
	
	public void addByte(int b)
	{
		bytes.addLast((byte) b);
	}
	
	public void prependByte(int i)
	{
		bytes.addFirst((byte) i);
	}
	
	public void prependSize()
	{
		int size = bytes.size();
		System.out.println("Size: " + size);
		prependCInt(size);
	}
	
	public void prependCInt(int i)
	{
		int c = 0;
		
		while((i & -128) != 0)
		{
			bytes.add(c++, (byte) (i & 127 | 128));
			i >>>= 7;
		}
		
		bytes.add(c++, (byte) (i));
	}
	
	public void addMessage(MessageBuilder message)
	{
		bytes.addAll(message.getBytes());
	}
	
	private LinkedList<Byte> getBytes()
	{
		return bytes;
	}
	
	public byte[] asBytes()
	{
		byte[] bytes = new byte[this.bytes.size()];
		Iterator<Byte> it = this.bytes.iterator();
		for(int i = 0; i < bytes.length; i++)
		{
			byte b = it.next();
			bytes[i] = b;
		}
		
		return bytes;
	}
	
	public void fromBytes(byte[] bytes)
	{
		this.bytes.clear();
		addBytes(bytes);
	}
	
	public void write(OutputStream out)
	{
		byte[] bytes = asBytes();
		
		System.out.println("Sending: ");
		for(byte b : bytes)
		{
			System.out.print(" " + Integer.toHexString(b & 0xFF));//+ "(" + (Byte.toUnsignedInt(b)) + "/" + (b == 0 ? 'Ø' : (char)b) + ")");
		}
		System.out.println(" <");
		
		try
		{
			out.write(bytes);
		}
		catch(IOException e)
		{
			throw new DirtyIOException(e);
		}
	}
	
	//########################################################
	//Primitive types:
	
	public void addShort(int i)
	{
		bytes.add((byte) ((i >> 8) & 255));
		bytes.add((byte) (i & 255));
	}
	
	public void addInt(int i)
	{
		bytes.add((byte) ((i >> 24) & 255));
		bytes.add((byte) ((i >> 16) & 255));
		bytes.add((byte) ((i >> 8) & 255));
		bytes.add((byte) (i & 255));
	}
	
	@Deprecated
	public void compress(Compressor c)
	{
		IntBytes ret = c.compress(asBytes());
		if(ret.getInt() == 0)
		{
			prependCInt(0);
		}
		else
		{
			bytes.clear();
			addCInt(ret.getInt());
			addBytes(ret.getBytes());
		}
	}
	
	public void addBoolean(boolean b)
	{
		addByte(b ? 1 : 0);
	}
	
	public void addDouble(double d)
	{
		long bits = Double.doubleToRawLongBits(d);
		addLong(bits);
	}
	
	public void addLong(long value)
	{
		bytes.add((byte) ((value >> 56) & 255));
		bytes.add((byte) ((value >> 48) & 255));
		bytes.add((byte) ((value >> 40) & 255));
		bytes.add((byte) ((value >> 32) & 255));
		bytes.add((byte) ((value >> 24) & 255));
		bytes.add((byte) ((value >> 16) & 255));
		bytes.add((byte) ((value >> 8) & 255));
		bytes.add((byte) (value & 255));
	}
	
	public void addCLong(long value)
	{
		do
		{
			byte temp = (byte) (value & 0b01111111);
			// Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
			value >>>= 7;
			if(value != 0)
			{
				temp |= 0b10000000;
			}
			addByte(temp);
		}
		while(value != 0);
	}

	public void addFloat(float f)
	{
		int bits = Float.floatToRawIntBits(f);
		addInt(bits);
	}

	public void addLocation(int x, int y, int z)
	{
		addLong(
			(((long)x & 0x3FFFFFF) << 38) |
			(((long)y & 0xFFF) << 26) |
			((long)z & 0x3FFFFFF));
	}
	
	public void addUUID(UUID uuid)
	{
		addLong(uuid.getMostSignificantBits());
		addLong(uuid.getLeastSignificantBits());
	}
}
