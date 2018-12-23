package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.handler.LoginPacketHandler;
import old.packet.MessageBuilder;

public class StartMCClient
{
	public static void main(String[] args)
	{
		Credentials.load();
		L.init();
		
		Connector con = new Connector("s.redstone-server.info", 25565, (connector) ->  {
			MessageBuilder mb = new MessageBuilder();
			
			mb.addCInt(404);
			mb.addString("The-Cake-Is-A-Lie-Or-The-URL-I-Choose");
			mb.addShort(666);
			mb.addCInt(2);
			
			mb.prependCInt(0);
			connector.sendPacket(mb.asBytes());
			
			mb = new MessageBuilder();
			mb.addString(Credentials.username);
			
			mb.prependCInt(0);
			connector.sendPacket(mb.asBytes());
		});
		
		PrimitiveDataDude dataDude = new PrimitiveDataDude(con);
		
		con.setHandler(new LoginPacketHandler(con, dataDude));
		con.connect();
	}
}
