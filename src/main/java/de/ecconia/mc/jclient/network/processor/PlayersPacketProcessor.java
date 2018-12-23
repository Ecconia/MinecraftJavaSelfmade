package de.ecconia.mc.jclient.network.processor;

import java.util.ArrayList;
import java.util.List;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import old.reading.helper.Provider;

public class PlayersPacketProcessor extends PacketThread
{
	public PlayersPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("PlayersThread", dataDude);
	}
	
	@Override
	protected void process(GenericPacket packet)
	{
		Provider p = packet.getProvider();
		PacketReader reader = new PacketReader(p.readBytes(p.remainingBytes()));
		int id = packet.getId();
		
		if(id == 0x47)
		{
			logPacket("Teams");
			
			String teamName = reader.readString();
			logData("Team packet, Name: " + teamName);
			if(teamName.length() > 16)
			{
				logData("WARNING: Teamname is longer than 16 characters!");
				System.out.println("WARNING: Teamname is longer than 16 characters! (" + teamName + ")");
			}
			
			int mode = reader.readUByte();
			if(mode == 0)
			{
				logData("> OP: create");
				String displayNameJson = reader.readString();
				int friendlyFlags = reader.readUByte();
				String nameTagVisibility = reader.readString();
				String collisionRule = reader.readString();
				int formatting = reader.readCInt();
				String prefixJson = reader.readString();
				String suffixJson = reader.readString();
				int memberCount = reader.readCInt();
				List<String> members = new ArrayList<>();
				for(int i = 0; i < memberCount; i++)
				{
					members.add(reader.readString());
				}
				
				logData("> Display: " + displayNameJson);
				logData("> FriendlyFlag: 0b" + Integer.toBinaryString(friendlyFlags));
				logData("> NameTagVisibility: " + nameTagVisibility);
				logData("> Collision: " + collisionRule);
				logData("> Formatting: " + formatting);
				logData("> Prefix Json: " + prefixJson);
				logData("> Suffix Json: " + suffixJson);
				logData("> Members: " + (memberCount == 0 ? "-none-" : String.join(", ", members)));
			}
			else if(mode == 1)
			{
				logData("> OP: remove");
			}
			else if(mode == 2)
			{
				logData("> OP: update");
				
				String displayNameJson = reader.readString();
				int friendlyFlags = reader.readUByte();
				String nameTagVisibility = reader.readString();
				String collisionRule = reader.readString();
				int formatting = reader.readCInt();
				String prefixJson = reader.readString();
				String suffixJson = reader.readString();
				
				logData("> Display: " + displayNameJson);
				logData("> FriendlyFlag: 0b" + Integer.toBinaryString(friendlyFlags));
				logData("> NameTagVisibility: " + nameTagVisibility);
				logData("> Collision: " + collisionRule);
				logData("> Formatting: " + formatting);
				logData("> Prefix Json: " + prefixJson);
				logData("> Suffix Json: " + suffixJson);
			}
			else if(mode == 3)
			{
				logData("> OP: add player(s)");
				
				int memberCount = reader.readCInt();
				List<String> members = new ArrayList<>();
				for(int i = 0; i < memberCount; i++)
				{
					members.add(reader.readString());
				}
				
				logData("> Players: " + (memberCount == 0 ? "-none-" : String.join(", ", members)));
			}
			else if(mode == 4)
			{
				logData("> OP: remove player(s)");
				
				int memberCount = reader.readCInt();
				List<String> members = new ArrayList<>();
				for(int i = 0; i < memberCount; i++)
				{
					members.add(reader.readString());
				}
				
				logData("> Players: " + (memberCount == 0 ? "-none-" : String.join(", ", members)));
			}
			else
			{
				logData("WARNING: Team operation unkown: " + mode);
				System.out.println("WARNING: Team operation unkown: " + mode);
			}
		}
		else if(id == 0x30)
		{
			logPacket("Player entry");
		}
	}
	
	private void logPacket(String name)
	{
		L.writeLineOnChannel("Packets", name);
	}
	
	private void logData(String message)
	{
		L.writeLineOnChannel("C: Players", message);
	}
}
