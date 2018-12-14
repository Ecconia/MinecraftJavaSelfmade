package old.packet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

import de.ecconia.mc.jclient.data.IntBytes;
import de.ecconia.mc.jclient.network.tools.compression.Compressor;
import old.reading.DirtyIOException;

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
	
	public void prepandByte(int i)
	{
		bytes.addFirst((byte) i);
	}
	
	public void prepandSize()
	{
		int size = bytes.size();
		System.out.println("Size: " + size);
		prepandCInt(size);
	}
	
	public void prepandCInt(int i)
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
			prepandCInt(0);
		}
		else
		{
			bytes.clear();
			addCInt(ret.getInt());
			addBytes(ret.getBytes());
		}
	}
}
