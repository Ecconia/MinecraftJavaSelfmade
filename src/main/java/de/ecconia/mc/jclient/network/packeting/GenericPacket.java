package de.ecconia.mc.jclient.network.packeting;

public class GenericPacket
{
	private final int id;
	private final byte[] bytes;
	
	public GenericPacket(int id, byte[] bytes)
	{
		this.id = id;
		this.bytes = bytes;
	}
	
	public int getId()
	{
		return id;
	}
	
	public byte[] getBytes()
	{
		return bytes;
	}
}
