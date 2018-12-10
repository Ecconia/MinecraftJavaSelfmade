package old.sending;

import java.io.OutputStream;

import old.cred.Credentials;
import old.packet.MessageBuilder;

public class BasicRequests
{
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
	
	public static void sendPingTypeNormal(OutputStream out)
	{
		//Send Handshake packet:
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(404); //Protocol Version. (404)
		mb.addString("localhost"); //Domain/IP
		mb.addShort(25565); //Port
		mb.addCInt(1); //1 - Type Status
		
		mb.prepandCInt(0);
		mb.prepandSize();
		mb.write(out);
		
		//Send Response packet:
		mb = new MessageBuilder();
		
		mb.prepandCInt(0);
		mb.prepandSize();
		mb.write(out);
	}
	
	public static void tryLogin(OutputStream out)
	{
		//Send Handshake packet:
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(404); //Protocol Version. (404)
		mb.addString("localhost"); //Domain/IP
		mb.addShort(25565); //Port
		mb.addCInt(2); //2 - Type Login
		
		mb.prepandCInt(0); //Packet ID
		mb.prepandSize();
		mb.write(out);
		
		//Send login packet:
		mb = new MessageBuilder();
		mb.addString(Credentials.USERNAME);
		
		mb.prepandCInt(0); //Packet ID
		mb.prepandSize();
		mb.write(out);
	}
}
