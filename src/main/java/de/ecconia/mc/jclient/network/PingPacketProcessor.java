package de.ecconia.mc.jclient.network;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import old.packet.MessageBuilder;
import old.reading.helper.Provider;

public class PingPacketProcessor extends PacketThread
{
	public PingPacketProcessor(String name, PrimitiveDataDude dataDude)
	{
		super(name, dataDude);
	}

	@Override
	protected void process(GenericPacket packet)
	{
		Provider p = packet.getProvider();
		int id = packet.getId();
		
		if(id == 33)
		{
			logPacket("Ping");
			byte[] ping = p.readBytes(8);
			
			MessageBuilder mb = new MessageBuilder();
			mb.addBytes(ping);
			mb.prependCInt(14);
			con.sendPacket(mb.asBytes());
		}
		else
		{
			System.out.println(Thread.currentThread().getName() + " received packet it was not surposed to get: " + id);
		}
	}
	
	private void logPacket(String name)
	{
//		System.out.println(">>> P: " + name);
		L.writeLineOnChannel("Packets", name);
	}
}
