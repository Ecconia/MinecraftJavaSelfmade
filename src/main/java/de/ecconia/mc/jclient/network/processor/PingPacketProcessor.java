package de.ecconia.mc.jclient.network.processor;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import old.packet.MessageBuilder;
import old.reading.helper.Provider;

public class PingPacketProcessor extends PacketThread
{
	public PingPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("PingPacketThread", dataDude);
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
		L.writeLineOnChannel("Packets", name);
	}
}
