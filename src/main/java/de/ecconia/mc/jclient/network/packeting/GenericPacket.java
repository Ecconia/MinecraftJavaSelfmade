package de.ecconia.mc.jclient.network.packeting;

import old.reading.helper.Provider;

public class GenericPacket
{
	private final int id;
	//TODO: Get rid of provider.
	private final Provider provider;
	
	public GenericPacket(int id, Provider provider)
	{
		this.id = id;
		this.provider = provider;
	}
	
	public int getId()
	{
		return id;
	}
	
	public Provider getProvider()
	{
		return provider;
	}
}
