package de.ecconia.mc.jclient.network.handler;

import de.ecconia.mc.jclient.Logger;
import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
import de.ecconia.mc.jclient.network.packeting.PacketReader;

public class JoinPacketWrapper implements PacketHandler
{
	private final Connector con;
	private final PrimitiveDataDude dataDude;
	
	public JoinPacketWrapper(PrimitiveDataDude dataDude)
	{
		this.con = dataDude.getCon();
		this.dataDude = dataDude;
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try
		{
			PacketReader reader = new PacketReader(bytes);
			int id = reader.readCInt();
			
			if(id == 0)
			{
				//Connection refused packet.
				System.out.println("Disconnection packet while joining in:");
				System.out.println("Message: " + reader.readString());
				
				//TODO: con.disconnect();
			}
			else if(id == 37)
			{
				dataDude.connectedToServer();
				PacketHandler handler = new PlayPacketHandler(dataDude);
				con.setHandler(handler);
				handler.onPacketReceive(bytes);
			}
			else
			{
				Logger.warn("Received packet with ID " + id + " before the join packet.");
				
				dataDude.connectedToServer();
				PacketHandler handler = new PlayPacketHandler(dataDude);
				con.setHandler(handler);
				handler.onPacketReceive(bytes);
			}
		}
		catch(Exception e)
		{
			Logger.ex("while reading packet", e);
		}
	}
}
