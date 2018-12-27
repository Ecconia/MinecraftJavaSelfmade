package de.ecconia.mc.jclient.tools;

public final class IntBytes
{
	private final int i;
	private final byte[] b;
	
	public IntBytes(int i, byte[] b)
	{
		this.i = i;
		this.b = b;
	}
	
	public byte[] getBytes()
	{
		return b;
	}
	
	public int getInt()
	{
		return i;
	}
}
