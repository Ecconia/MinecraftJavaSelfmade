package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.connection.Connector;
import old.cred.Credentials;
import old.packet.MessageBuilder;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;

public class StartMCClient
{
	public static void main(String[] args)
	{
		Connector con = new Connector("s.redstone-server.info", 25565);
		
		{
			MessageBuilder mb = new MessageBuilder();
			
			mb.addCInt(404);
			mb.addString("afkserver.com");
			mb.addShort(25565);
			mb.addCInt(1);
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
			
			mb = new MessageBuilder();
			mb.addString(Credentials.USERNAME);
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
		}
		
		con.setHandler(bytes -> {
			Provider p = new ArrayProvider(bytes);
			System.out.println("Received Packet with ID:" + p.readCInt() + " Size:" + p.remainingBytes());
		});
	}
}
