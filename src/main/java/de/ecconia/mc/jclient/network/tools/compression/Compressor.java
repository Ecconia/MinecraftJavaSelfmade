package de.ecconia.mc.jclient.network.tools.compression;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import de.ecconia.mc.jclient.tools.IntBytes;

public class Compressor
{
	private final Inflater inflater = new Inflater();
	private final Deflater deflater = new Deflater();
	
	private int packetThreshold;
	
	public Compressor(int packetThreshold)
	{
		this.packetThreshold = packetThreshold;
	}
	
	public void setPacketThreshold(int packetThreshold)
	{
		this.packetThreshold = packetThreshold;
	}
	
	public byte[] uncompress(int originalSize, byte[] bytes)
	{
		if(originalSize == 0)
		{
			return bytes;
		}
		else
		{
			if(originalSize < packetThreshold)
			{
				throw new CompressionException("Packet has malformed compression (too small).", null);
			}
			else if(originalSize > 2097152)
			{
				throw new CompressionException("Packet has malformed compression (too big for packet).", null);
			}
			else
			{
				inflater.setInput(bytes);
				
				try
				{
					byte[] output = new byte[originalSize];
					
					inflater.inflate(output);
					inflater.reset();
					
					return output;
				}
				catch(DataFormatException e)
				{
					throw new CompressionException("Could not uncompress packet.", e);
				}
			}
		}
	}
	
	public IntBytes compress(byte[] bytes)
	{
		if(bytes.length < packetThreshold)
		{
			return new IntBytes(0, bytes);
		}
		else
		{
			int size = bytes.length;
			deflater.setInput(bytes, 0, size);
			deflater.finish();
			
			byte[] buffer = new byte[size];
			int newSize = deflater.deflate(buffer);
			
			if(!deflater.finished())
			{
				throw new CompressionException("After compressing a packet, the deflater was not finished.", null);
			}
			
			if(newSize < 2)
			{
				throw new CompressionException("After compressing a packet, the deflater returned " + newSize + ".", null);
			}
			
			deflater.reset();
			
			byte[] outputBuffer = new byte[size];
			//Copy the compressed bytes into a new byte array with correct size.
			System.arraycopy(buffer, 0, outputBuffer, 0, newSize);
			return new IntBytes(size, outputBuffer);
		}
	}
}
