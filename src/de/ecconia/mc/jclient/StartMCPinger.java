package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.connection.Connector;
import old.packet.MessageBuilder;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;

public class StartMCPinger
{
	public static void main(String[] args)
	{
		Connector con = new Connector("s.redstone-server.info", 25565);
		
		con.setHandler(bytes -> {
			Provider p = new ArrayProvider(bytes);
			int id = p.readCInt();
			System.out.println(">>> Packet with ID:" + id + " Size:" + p.remainingBytes());
			
			System.out.println("JsonResponse: " + p.readString());
			
			System.out.println("Quit.");
			System.exit(0);
		});
		
		new Thread(() -> {
			try
			{
				Thread.sleep(800);
			}
			catch(InterruptedException e)
			{
			}
			
			MessageBuilder mb = new MessageBuilder();
			
			mb.addCInt(404);
			mb.addString("s.redstone-server.info");
			mb.addShort(25565);
			mb.addCInt(1);
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
			
			mb = new MessageBuilder();
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
		}).start();
		
		con.connect();
	}
}
