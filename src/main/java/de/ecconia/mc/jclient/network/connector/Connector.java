package de.ecconia.mc.jclient.network.connector;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.SecretKey;

import de.ecconia.mc.jclient.data.IntBytes;
import de.ecconia.mc.jclient.network.connector.inner.DecryptionReader;
import de.ecconia.mc.jclient.network.connector.inner.NormalReader;
import de.ecconia.mc.jclient.network.connector.inner.Reader;
import de.ecconia.mc.jclient.network.tools.compression.Compressor;
import de.ecconia.mc.jclient.network.tools.encryption.SyncCryptUnit;
import de.ecconia.mc.jclient.tools.CIntUntils;
import old.reading.DirtyIOException;

public class Connector
{
	private final String domain;
	private final int port;
	
	private PacketHandler handler;
	
	private Socket s;
	private Reader r;
	private SyncCryptUnit crypter;
	private Compressor compressor;
	
	//TODO: Add issue/error handling.
	public Connector(String domain, int port)
	{
		this.domain = domain;
		this.port = port;
	}
	
	/**
	 * Set the packet listener
	 */
	public void setHandler(PacketHandler handler)
	{
		this.handler = handler;
	}
	
	/**
	 * Connect to the server.
	 */
	public void connect()
	{
		//Start the connection
		try
		{
			s = new Socket(domain, port);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
			return;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		try
		{
			r = new NormalReader(s.getInputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		System.out.println(">>> Starting to listen for incomming packets.");
		while(true)
		{
			readPacket();
		}
	}
	
	/**
	 * Enables compression for all packages.
	 * @param maxPacketSize - the maximum size a packet may have unencrypted
	 */
	public void setCompression(Integer maxPacketSize)
	{
		if(maxPacketSize == null)
		{
			compressor = null;
		}
		else if(compressor == null)
		{
			compressor = new Compressor(maxPacketSize);
		}
		else
		{
			compressor.setPacketThreshold(maxPacketSize);
		}
	}
	
	/**
	 * Enables encryption for any traffic.
	 * @param key - the symetric key used for encryption
	 */
	public void enableEncryption(SecretKey key)
	{
		crypter = new SyncCryptUnit(key);
		r = new DecryptionReader(r, crypter);
	}
	
	/**
	 * Sends bytes to the server.
	 * Will compress and prepend size and encrypt the packet.
	 * @param packet - the content of the packet which will be sent
	 */
	//TODO: THREADSAFE!
	//Just one Thread should be able to access this method, all others should talk to that thread.
	public void sendPacket(byte[] packet)
	{
		if(compressor != null)
		{
			//Compress packet
			IntBytes ret = compressor.compress(packet);
			//Prepend original size, or 0
			packet = CIntUntils.prependCInt(ret.getBytes(), ret.getInt());
		}
		
		//Prepend size
		packet = CIntUntils.prependCInt(packet, packet.length);
		
		if(crypter != null)
		{
			//Encrypt packet
			packet = crypter.encryptBytes(packet);
		}
		
		try
		{
			//Send packet
			s.getOutputStream().write(packet);
			s.getOutputStream().flush();
		}
		catch(IOException e)
		{
			throw new DirtyIOException(e);
		}
	}
	
	//### Internal ###
	
	private void readPacket()
	{
		int packetSize = readCInt();
		byte[] packet = r.readBytes(packetSize);
		
		if(compressor != null)
		{
			IntBytes ret = CIntUntils.readCInt(packet);
			packet = compressor.uncompress(ret.getInt(), ret.getBytes());
		}
		
//		System.out.println(">> Received Packet:");
//		PrintUtils.printBytes(packet);
		
		handler.onPacketReceive(packet);
	}
	
	public int readCInt()
	{
		int iteration = 0;
		int value = 0;
		
		byte read;
		do
		{
			read = r.readByte();
			value |= (read & 127) << iteration++ * 7;
			
			if(iteration > 5)
			{
				throw new RuntimeException("VarInt too big");
			}
		}
		while((read & 128) == 128);
		
		return value;
	}
}
