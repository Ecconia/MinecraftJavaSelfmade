package de.ecconia.mc.jclient.network.processor;

import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.MessageBuilder;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;

public class PingPacketProcessor extends PacketThread
{
	public PingPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("PingPacketThread", dataDude);
	}
	
	@Override
	protected void process(GenericPacket packet)
	{
		PacketReader reader = new PacketReader(packet.getBytes());
		int id = packet.getId();
		
		if(id == 0x21)
		{
			logPacket("Ping");
			byte[] ping = reader.readBytes(8);
			
			//TODO: Parse and send as Long using SendHelper
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
