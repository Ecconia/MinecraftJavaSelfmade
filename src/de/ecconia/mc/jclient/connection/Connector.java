package de.ecconia.mc.jclient.connection;

import javax.crypto.SecretKey;

public class Connector
{
	//TODO: Add issue/error handling.
	public Connector(String domain, int port)
	{
		
	}
	
	/**
	 * Set the packet listener
	 */
	public void setHandler(PacketHandler handler)
	{
		
	}
	
	/**
	 * Connect to the server.
	 */
	public void connect()
	{
		
	}
	
	/**
	 * Enables compression for all packages.
	 * @param maxPacketSize - the maximum size a packet may have unencrypted
	 */
	public void setCompression(Integer maxPacketSize)
	{
		
	}
	
	/**
	 * Enables encryption for any traffic.
	 * @param key - the symetric key used for encryption
	 */
	public void enableEncryption(SecretKey key)
	{
		
	}
	
	/**
	 * Sends bytes to the server.
	 * Will compress and prepand size and encrypt the packet.
	 * @param packet - the content of the packet which will be sent
	 */
	public void sendPacket(byte[] packet)
	{
		
	}
}
