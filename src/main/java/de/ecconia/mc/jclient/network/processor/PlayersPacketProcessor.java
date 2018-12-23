package de.ecconia.mc.jclient.network.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
			
			int mode = reader.readCInt();
			if(mode < 0 || mode > 4)
			{
				logData("WARNING: PlayerList operation unkown: " + mode);
				System.out.println("WARNING: PlayerList operation unkown: " + mode);
				return;
			}
			
			if(mode == 0)
			{
				logData("Add player(s) to registry:");
			}
			else if(mode == 1)
			{
				logData("Update gamemode for player(s):");
			}
			else if(mode == 2)
			{
				logData("Update ping for player(s):");
			}
			else if(mode == 3)
			{
				logData("Update display name for player(s):");
			}
			else if(mode == 4)
			{
				logData("Remove player(s) from registry:");
			}
			
			int playerAmount = reader.readCInt();
			for(int i = 0; i < playerAmount; i++)
			{
				UUID uuid = reader.readUUID();
				logData("> Player: " + uuid);
				if(mode == 0)
				{
					String name = reader.readString();
					logData("> -Name: " + name);
					int propertyAmount = reader.readCInt();
					for(int j = 0; j < propertyAmount; j++)
					{
						String pName = reader.readString();
						String pValue = reader.readString();
						boolean isSigned = reader.readBoolean();
						String signature = null;
						if(isSigned)
						{
							signature = reader.readString();
						}
						logData("> -Prop: " + pName + " = " + pValue + " :: " + signature);
					}
					
					int gamemode = reader.readCInt();
					logData("> -Gamemode: " + gamemode);
					
					int ping = reader.readCInt();
					logData("> -Ping: " + ping);
					
					boolean hasDisplayName = reader.readBoolean();
					String displayname = hasDisplayName ? reader.readString() : null;
					logData("> -Display: " + displayname);
				}
				else if(mode == 1)
				{
					int gamemode = reader.readCInt();
					logData("> -Gamemode: " + gamemode);
				}
				else if(mode == 2)
				{
					int ping = reader.readCInt();
					logData("> -Ping: " + ping);
				}
				else if(mode == 3)
				{
					boolean hasDisplayName = reader.readBoolean();
					String displayname = hasDisplayName ? reader.readString() : null;
					logData("> -Display: " + displayname);
				}
				else if(mode == 4)
				{
					//Remove.
				}
			}
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
