package de.ecconia.mc.jclient.network.connector;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.crypto.SecretKey;

import de.ecconia.mc.jclient.Logger;
import de.ecconia.mc.jclient.network.connector.inner.DecryptionReader;
import de.ecconia.mc.jclient.network.connector.inner.NormalReader;
import de.ecconia.mc.jclient.network.connector.inner.Reader;
import de.ecconia.mc.jclient.network.tools.compression.Compressor;
import de.ecconia.mc.jclient.network.tools.encryption.SyncCryptUnit;
import de.ecconia.mc.jclient.tools.CIntUntils;
import de.ecconia.mc.jclient.tools.IntBytes;
import old.reading.DirtyIOException;

public class Connector implements Sender
{
	private final BlockingQueue<ByteArray> sendQueue = new LinkedBlockingQueue<>();
	
	private final ConnectionEstablishedHandler connectedHandler;
	private final String domain;
	private final int port;
	
	private PacketHandler handler;
	
	private Socket s;
	private Reader r;
	private SyncCryptUnit crypter;
	private Compressor compressor;
	
	//TODO: Add issue/error handling.
	public Connector(String domain, int port, ConnectionEstablishedHandler connectedHandler)
	{
		this.connectedHandler = connectedHandler;
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
			if(e.getMessage().equals(domain))
			{
				System.err.println("Could not connect to " + domain + ", connected to internet?");
			}
			else
			{
				Logger.ex("connecting to server", e);
			}
			
			return;
		}
		catch(ConnectException e)
		{
			if(e.getMessage().equals("Connection refused (Connection refused)"))
			{
				System.err.println("Could not connect to " + domain + ", wrong port?");
			}
			else
			{
				Logger.ex("connecting to server", e);
			}
			
			return;
		}
		catch(IOException e)
		{
			Logger.ex("connecting to server", e);
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
		
		Thread sendingThread = new Thread(() -> {
			while(true)
			{
				try
				{
					sendPacket(sendQueue.take());
				}
				catch(InterruptedException e)
				{
					System.out.println("Error while taking packet from sending queue:");
					e.printStackTrace(System.out);
				}
			}
		}, "SendingThread");
		sendingThread.setUncaughtExceptionHandler((t, ex) -> {
			Logger.ex("on Thread " + t.getName() + ", now its dead", ex);
		});
		sendingThread.start();
		
		Thread readingThread = new Thread(() -> {
			System.out.println(">>> Starting to listening for incomming packets.");
			while(true)
			{
				readPacket();
			}
		}, "ReadingThread");
		readingThread.setUncaughtExceptionHandler((t, ex) -> {
			Logger.ex("on Thread " + t.getName() + ", now its dead", ex);
			System.out.println("Total amount of bytes read: " + r.getBytesRead());
		});
		readingThread.start();
		
		connectedHandler.connected(this);
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
	public void sendPacket(byte[] packet)
	{
		try
		{
			sendQueue.put(new ByteArray(packet));
		}
		catch(InterruptedException e)
		{
			System.out.println("Error putting a packet into sending queue:");
			e.printStackTrace(System.out);
		}
	}
	
	//### Internal ###
	
	private void sendPacket(ByteArray bArray)
	{
		byte[] packet = bArray.getArray();
		
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
	
	private void readPacket()
	{
		int packetSize = readCInt();
		byte[] packet = r.readBytes(packetSize);
		
		if(compressor != null)
		{
			IntBytes ret = CIntUntils.readCInt(packet);
			packet = compressor.uncompress(ret.getInt(), ret.getBytes());
		}
		
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
	
	private static class ByteArray
	{
		byte[] array;
		
		public ByteArray(byte[] array)
		{
			this.array = array;
		}
		
		public byte[] getArray()
		{
			return array;
		}
	}
}
