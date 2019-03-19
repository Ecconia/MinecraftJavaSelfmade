package de.ecconia.mc.jclient.network.connector.inner;

import java.io.IOException;
import java.io.InputStream;

import de.ecconia.mc.jclient.network.DirtyIOException;
import de.ecconia.mc.jclient.network.DirtyStreamEndException;

public class NormalReader implements Reader
{
	private final InputStream in;
	
	private long bytesRead = 0;
	
	public NormalReader(InputStream in)
	{
		this.in = in;
	}
	
	@Override
	public byte readByte()
	{
		try
		{
			int i = in.read();
			
			if(i == -1)
			{
				throw new DirtyStreamEndException();
			}
			
			bytesRead++;
			return (byte) i;
		}
		catch(IOException e)
		{
			throw new DirtyIOException(e);
		}
	}
	
	@Override
	public byte[] readBytes(int amount)
	{
		try
		{
			byte[] bytes = new byte[amount];
			
			int pointer = 0;
			int remaining = amount;
			
			while(remaining > 0)
			{
				int amountRead = in.read(bytes, pointer, remaining);
				if(amountRead == -1)
				{
					throw new DirtyStreamEndException();
				}
				
				remaining -= amountRead;
				pointer += amountRead;
			}
			
			bytesRead += amount;
			return bytes;
		}
		catch(IOException e)
		{
			throw new DirtyIOException(e);
		}
	}
	
	@Override
	public long getBytesRead()
	{
		return bytesRead;
	}
}
