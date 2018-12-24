package de.ecconia.mc.jclient.network.handler;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import de.ecconia.mc.jclient.network.processor.GenericPacketProcessor;
import de.ecconia.mc.jclient.network.processor.PingPacketProcessor;
import de.ecconia.mc.jclient.network.processor.PlayersPacketProcessor;
import de.ecconia.mc.jclient.network.processor.WorldPacketProcessor;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;

public class PlayPacketHandler implements PacketHandler
{
	private final PacketThread genericThread;
	private final PacketThread playersThread;
	private final PacketThread worldThread;
	private final PacketThread pingThread;
	private final Connector con;
	
	public PlayPacketHandler(Connector con, PrimitiveDataDude dataDude)
	{
		this.con = dataDude.getCon();
		this.pingThread = new PingPacketProcessor(dataDude);
		this.worldThread = new WorldPacketProcessor(dataDude);
		this.playersThread = new PlayersPacketProcessor(dataDude);
		this.genericThread = new GenericPacketProcessor(dataDude);
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try
		{
			Provider p = new ArrayProvider(bytes);
			int id = p.readCInt();
			
			GenericPacket packet = new GenericPacket(id, p);
			
			if(id == 0x03)
			{
				logPacket("Compression request");
				int compressionLevel = p.readCInt();
				logData("> Compression above " + compressionLevel + " bytes.");
				if(p.remainingBytes() > 0)
				{
					logData("> WARNING: Compression package had more content.");
				}
				
				con.setCompression(compressionLevel);
			}
			else if(id == 0x21)
			{
				//Priority packet, has to be handled as fast as possible without anything in the way.
				pingThread.handle(packet);
			}
			else if(id == 0x22 || id == 0x0B || id == 0x0F || id == 0x1F)
			{
				//Chunk/Block packets.
				worldThread.handle(packet);
			}
			else if(id == 0x47 || id == 0x30)
			{
				//Team/PlayerList entries
				playersThread.handle(packet);
			}
			else
			{
				//Garbage collection :P All packets currently not used and to be used later on.
				genericThread.handle(packet);
			}
		}
		catch(Exception e)
		{
			System.out.println("> ERROR: Exception while reading/enqueuing packet: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	private void logPacket(String name)
	{
//		System.out.println(">>> P: " + name);
		L.writeLineOnChannel("Packets", name);
	}
	
	private void logData(String message)
	{
		L.writeLineOnChannel("Content", message);
	}
}
