package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.connection.Connector;
import old.cred.Credentials;
import old.packet.MessageBuilder;

public class StartMCClient
{
	public static void main(String[] args)
	{
		Connector con = new Connector("s.redstone-server.info", 25565);
		
		con.setHandler(new LoginPacketHandler(con));
		
		//Send Login packets:
		new Thread(() -> {
			try
			{
				//TODO: Imrpove somehow?
				Thread.sleep(800);
			}
			catch(InterruptedException e)
			{
			}
			
			MessageBuilder mb = new MessageBuilder();
			
			mb.addCInt(404);
			mb.addString("afkserver.com");
			mb.addShort(25565);
			mb.addCInt(2);
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
			
			mb = new MessageBuilder();
			mb.addString(Credentials.USERNAME);
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
		}).start();
		
		con.connect();
	}
}
