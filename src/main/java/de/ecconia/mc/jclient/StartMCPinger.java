package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.packeting.MessageBuilder;
import de.ecconia.mc.jclient.network.packeting.PacketReader;

public class StartMCPinger
{
	public static void main(String[] args)
	{
		Connector con = new Connector("s.redstone-server.info", 25565, (connector) -> {
			MessageBuilder mb = new MessageBuilder();
			
			mb.addCInt(404);
			mb.addString("s.redstone-server.info");
			mb.addShort(25565);
			mb.addCInt(1);
			
			mb.prependCInt(0);
			connector.sendPacket(mb.asBytes());
			
			mb = new MessageBuilder();
			
			mb.prependCInt(0);
			connector.sendPacket(mb.asBytes());
		});
		
		con.setHandler(bytes -> {
			PacketReader reader = new PacketReader(bytes);
			int id = reader.readCInt();
			System.out.println(">>> Packet with ID:" + id + " Size:" + reader.remaining());
			
			System.out.println("JsonResponse: " + reader.readString());
			
			System.out.println("Quit.");
			System.exit(0);
		});
		
		con.connect();
	}
}
