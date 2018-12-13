package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.connection.Connector;
import de.ecconia.mc.jclient.connection.PacketHandler;
import old.packet.MessageBuilder;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;

public class PlayPacketHandler implements PacketHandler
{
	private final Connector con;
	private final PrimitiveDataDude dataDude;
	
	public PlayPacketHandler(Connector con, PrimitiveDataDude dataDude)
	{
		this.con = con;
		this.dataDude = dataDude;
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try
		{
			Provider p = new ArrayProvider(bytes);
			int id = p.readCInt();
			System.out.println(">>> Packet with ID:" + id + " Size:" + p.remainingBytes());
			
			//State for play packet.
			if(id == 3)
			{
				System.out.println("Server requests compression:");
				int compressionLevel = p.readCInt();
				System.out.println("Compression above " + compressionLevel + " bytes.");
				if(p.remainingBytes() > 0)
				{
					System.out.println("WARNING: Compression package had more content.");
				}
				
				con.setCompression(compressionLevel);
			}
			else if(id == 27)
			{
				System.out.println("Packet: Kick!");
				System.out.println("Message: " + p.readString());
			}
			else if(id == 33)
			{
				System.out.println("Packet: KeepAlive");
				byte[] ping = p.readBytes(8);
				
				MessageBuilder mb = new MessageBuilder();
				mb.addBytes(ping);
				mb.prepandCInt(14);
				con.sendPacket(mb.asBytes());
			}
			else if(id == 14)
			{
				System.out.println("Packet: Chat");
				String jsonMessage = p.readString();
				System.out.println("Json: " + jsonMessage);
				System.out.println("Loc: " + p.readByte());
				
				dataDude.newChatJSON(jsonMessage);
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR: Exception while reading packet: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
