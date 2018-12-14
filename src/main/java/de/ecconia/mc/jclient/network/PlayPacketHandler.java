package de.ecconia.mc.jclient.network;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
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
			
			//State for play packet.
			if(id == 3)
			{
				System.out.println(">>> Compression request");
				int compressionLevel = p.readCInt();
				System.out.println("> Compression above " + compressionLevel + " bytes.");
				if(p.remainingBytes() > 0)
				{
					System.out.println("> WARNING: Compression package had more content.");
				}
				
				con.setCompression(compressionLevel);
			}
			else if(id == 27)
			{
				System.out.println(">>> Disconnected by server");
				System.out.println("> Message: " + p.readString());
			}
			else if(id == 33)
			{
				System.out.println(">>> Ping");
				byte[] ping = p.readBytes(8);
				
				MessageBuilder mb = new MessageBuilder();
				mb.addBytes(ping);
				mb.prepandCInt(14);
				con.sendPacket(mb.asBytes());
			}
			else if(id == 14)
			{
				System.out.println(">>> Chat");
				String jsonMessage = p.readString();
				System.out.println("> Json: " + jsonMessage);
				System.out.println("> Loc: " + p.readByte());
				
				dataDude.newChatJSON(jsonMessage);
			}
			else if(id == 74)
			{
				System.out.println(">>> WorldTime");
				
			}
			else if(id == 13)
			{
				System.out.println(">>> Server difficulty");
				//UByte -> difficulty
			}
			else if(id == 0x2e)
			{
				System.out.println(">>> Player abilities");
				//Byte -> Invunerable/Flying/AllowFlying/InstaBreak
				//Float -> Fly speed
				//Float -> FOV (movement speed)
			}
			else if(id == 0x3d)
			{
				System.out.println(">>> Inventory Slot selected");
			}
			else if(id == 0x54)
			{
				System.out.println(">>> Declare Receipts (big packet)");
				//CInt -> Amount
				//[
				//String -> Identifier
				//String -> Where
				//.....
				//]
			}
			else if(id == 0x55)
			{
				System.out.println(">>> \"Tags\"");
				//Mapping for BlockID <-> BlockIdentifier?
			}
			else if(id == 0x1c)
			{
				System.out.println(">>> Entity status");
			}
			else if(id == 0x11)
			{
				System.out.println(">>> Declare command (:@)");
			}
			else if(id == 0x34)
			{
				System.out.println(">>> Unlock receipts");
			}
			else if(id == 0x47)
			{
				System.out.println(">>> Teams");
			}
			else if(id == 0x4e)
			{
				System.out.println(">>> Header/Footer PlayerList");
			}
			else if(id == 0x30)
			{
				System.out.println(">>> Player entry");
			}
			else if(id == 0x3F)
			{
				System.out.println(">>> Entity metadata");
			}
			else if(id == 0x22)
			{
//				System.out.println(">>> Chunk Data");
				//Obmitted, cause faster boot.
			}
			else if(id == 0x02)
			{
				System.out.println(">>> Spawn entity (thunderbolt)???");
				//CInt -> EntityID
				//Byte -> Type
				//Double -> x
				//Double -> y
				//Double -> z
			}
			else if(id == 0x44)
			{
				System.out.println(">>> Update health");
			}
			else if(id == 0x43)
			{
				System.out.println(">>> Set XP");
			}
			else if(id == 25)
			{
				System.out.println(">>> \"Plugin\" Message");
				//String -> channel
				//Byte[] -> data
			}
			else if(id == 0x51)
			{
				System.out.println(">>> Advancements");
				//Dough - big one
			}
			else if(id == 0x52)
			{
				System.out.println(">>> Entity Properties");
			}
			else if(id == 0x15)
			{
				System.out.println(">>> Window item update");
			}
			else if(id == 0x17)
			{
				System.out.println(">>> Set Inventory Slot");
			}
			else if(id == 0x49)
			{
				System.out.println(">>> Spawn Position");
			}
			else if(id == 0x32)
			{
				System.out.println(">>> Player Location/Head");
			}
			else if(id == 0x3b)
			{
				System.out.println(">>> World Border");
			}
			else if(id == 37)
			{
				System.out.println(">>> JoinPlayerState");
				//Int -> ID
				//UByte -> Gamemode
				//Int -> Dimension Type
				//UByte -> Difficulty
				//UByte -> Max players (ignored)
				//String -> Map type...?
				//Boolean -> Show Debug
			}
			else
			{
				System.out.println("||> Packet: ID:" + id + "(0x" + (Integer.toHexString(id)) + ")" + " Size:" + p.remainingBytes());
			}
		}
		catch(Exception e)
		{
			System.out.println("> ERROR: Exception while reading packet: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
