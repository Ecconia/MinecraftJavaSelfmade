package de.ecconia.mc.jclient.network.handler;

import de.ecconia.mc.jclient.Logger;
import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import de.ecconia.mc.jclient.network.processor.GenericPacketProcessor;
import de.ecconia.mc.jclient.network.processor.MainPlayerPacketProcessor;
import de.ecconia.mc.jclient.network.processor.PingPacketProcessor;
import de.ecconia.mc.jclient.network.processor.PlayersPacketProcessor;
import de.ecconia.mc.jclient.network.processor.WorldPacketProcessor;
import de.ecconia.mc.jclient.tools.CIntUntils;
import de.ecconia.mc.jclient.tools.IntBytes;

public class PlayPacketHandler implements PacketHandler
{
	private final PacketThread genericThread;
	private final PacketThread playersThread;
	private final PacketThread playerThread;
	private final PacketThread worldThread;
	private final PacketThread pingThread;
	private final Connector con;
	
	public PlayPacketHandler(PrimitiveDataDude dataDude)
	{
		this.con = dataDude.getCon();
		this.pingThread = new PingPacketProcessor(dataDude);
		this.worldThread = new WorldPacketProcessor(dataDude);
		this.playersThread = new PlayersPacketProcessor(dataDude);
		this.genericThread = new GenericPacketProcessor(dataDude);
		this.playerThread = new MainPlayerPacketProcessor(dataDude);
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try
		{
			IntBytes ret = CIntUntils.readCInt(bytes);
			int id = ret.getInt();
			bytes = ret.getBytes();
			
			GenericPacket packet = new GenericPacket(id, bytes);
			
			if(id == 0x03)
			{
				PacketReader reader = new PacketReader(bytes);
				logPacket("Compression request");
				int compressionLevel = reader.readCInt();
				logData("> Compression above " + compressionLevel + " bytes.");
				if(reader.remaining() > 0)
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
			else if(id == 0x32 || id == 0x2e || id == 0x44 || id == 0x43)
			{
				playerThread.handle(packet);
			}
			else if(id == 0x22 || id == 0x0B || id == 0x0F || id == 0x1F)
			{
				//Chunk/Block packets.
				worldThread.handle(packet);
			}
			else if(id == 0x47 || id == 0x30 || id == 0x4e)
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
			Logger.ex("while reading/enqueuing packet", e);
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
