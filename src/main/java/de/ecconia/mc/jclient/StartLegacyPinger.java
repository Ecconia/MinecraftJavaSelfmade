package de.ecconia.mc.jclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.ecconia.mc.jclient.network.packeting.MessageBuilder;

public class StartLegacyPinger
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Socket connection = new Socket("s.redstone-server.info", 25565);
		
		OutputStream os = connection.getOutputStream();
//		sendPingType0(os);
		sendPingType1(os);
//		sendPingTypeX(os);
		
		InputStream is = connection.getInputStream();
		
		int init = getByte(is);
		if(init != 255)
		{
			System.out.println("Malformed whatever... First byte not 255!");
			System.exit(0);
		}
		
		String s = "";
		int l = getUShort(is);
		for(int i = 0; i < l; i++)
		{
			s += (char) getUShort(is);
		}
		
		try
		{
			getByte(is);
			System.out.println("WARNING: More bytes to read!");
		}
		catch(IOException e)
		{
		}
		
		s = s.replace('\0', 'Ø');
		System.out.println("First ping type splits on §, the second on \\0 indicated by Ø.");
		System.out.println("Ping message: >" + s + "<");
		
		//IDE statisfaction:
		connection.close();
	}
	
	//### Unwrapped methods from legacy classes ###
	
	public static int getUShort(InputStream is) throws IOException
	{
		int i = getByte(is);
		i = i << 8;
		i += getByte(is);
		
		return i;
	}
	
	public static int getByte(InputStream is) throws IOException
	{
		return is.read();
	}
	
	//### Ping Types ###
	
	public static void sendPingType0(OutputStream out)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addByte(254);
		
		mb.write(out);
	}
	
	public static void sendPingType1(OutputStream out)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addByte(254);
		mb.addByte(1);
		
		mb.write(out);
	}
	
	public static void sendPingTypeX(OutputStream out)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addByte(254); //Server Ping List
		mb.addByte(1); //Always 1
		
		mb.addByte(250); //Plugin Message
		mb.addCString("MC|PingHost"); //Plugin message content
		
		String hostname = "localhost";
		//7 = port(4) + protocol(1) + hostname_length(2)
		mb.addShort(7 + hostname.length() * 2); //Length of "rest of data"
		
		mb.addByte(73); //Protocol version
		mb.addCString(hostname); //Hostname
		mb.addInt(25565); //Port
		
		mb.write(out);
	}
}
