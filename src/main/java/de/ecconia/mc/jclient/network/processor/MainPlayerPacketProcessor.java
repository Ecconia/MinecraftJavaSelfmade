package de.ecconia.mc.jclient.network.processor;

import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.MessageBuilder;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;

public class MainPlayerPacketProcessor extends PacketThread
{
	public MainPlayerPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("MainPlayerPacketThread", dataDude);
	}
	
	@Override
	protected void process(GenericPacket packet)
	{
		PacketReader reader = new PacketReader(packet.getBytes());
		int id = packet.getId();
		
		if(id == 0x32)
		{
			logPacket("Player teleport");
			
			double x = reader.readDouble();
			double y = reader.readDouble();
			double z = reader.readDouble();
			float yaw = reader.readFloat();
			float pitch = reader.readFloat();
			
			int flags = reader.readByte();
			
			int tpID = reader.readCInt();
			
			logData("Player Teleport: (" + x + ", " + y + ", " + z + " | " + yaw + ", " + pitch + ") Masq: " + Integer.toBinaryString(flags) + " ID: " + tpID);
			
			dataDude.getCurrentServer().getMainPlayer().serverLocation(x, y, z, yaw, pitch);
			
			MessageBuilder mb = new MessageBuilder();
			mb.addCInt(tpID);
			mb.prependCInt(0x00); //Teleport confirm packet.
			dataDude.getCon().sendPacket(mb.asBytes());
		}
		else if(id == 0x2e)
		{
			logPacket("Player abilities");
			//Byte -> Invunerable/Flying/AllowFlying/InstaBreak
			//Float -> Fly speed
			//Float -> FOV (movement speed)
		}
		else if(id == 0x44)
		{
			logPacket("Update health");
		}
		else if(id == 0x43)
		{
			logPacket("Set XP");
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
	
	private void logData(String message)
	{
		L.writeLineOnChannel("Content", message);
	}
}
