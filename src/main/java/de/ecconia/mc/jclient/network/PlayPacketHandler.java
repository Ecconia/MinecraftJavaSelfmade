package de.ecconia.mc.jclient.network;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;

public class PlayPacketHandler implements PacketHandler
{
	private final PacketThread genericThread;
	private final PacketThread pingThread;
	
	public PlayPacketHandler(Connector con, PrimitiveDataDude dataDude)
	{
		this.pingThread = new PingPacketProcessor("PinfPacketThread", dataDude);
		this.genericThread = new GenericPacketProcessor("GenericPacketThread", dataDude);
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try
		{
			Provider p = new ArrayProvider(bytes);
			int id = p.readCInt();
			
			GenericPacket packet = new GenericPacket(id, p);
			
			if(id == 33)
			{
				//Priority packet, has to be handled as fast as possible without anything in the way.
				pingThread.handle(packet);
			}
			else
			{
				//Garbage collection :P All packets currently not used and to be used later on.
				genericThread.handle(packet);
			}
		}
		catch(Exception e)
		{
			System.out.println("> ERROR: Exception while reading packet: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
