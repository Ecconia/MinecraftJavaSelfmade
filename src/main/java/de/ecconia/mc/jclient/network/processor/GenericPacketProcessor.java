package de.ecconia.mc.jclient.network.processor;

import de.ecconia.mc.jclient.chat.ChatFormatException;
import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import de.ecconia.mc.jclient.tools.json.JSONException;

public class GenericPacketProcessor extends PacketThread
{
	public GenericPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("GenericPacketThread", dataDude);
	}
	
	@Override
	protected void process(GenericPacket packet)
	{
		PacketReader reader = new PacketReader(packet.getBytes());
		int id = packet.getId();
		
		if(id == 0x23)
		{
			logPacket("Effect");
		}
		else if(id == 0x28)
		{
			logPacket("Entity movement relative");
			
			int entityID = reader.readCInt();
			int xMovement = reader.readShort();
			int yMovement = reader.readShort();
			int zMovement = reader.readShort();
			boolean onGround = reader.readBoolean();
			
			logData("Entity " + entityID + " moved (" + xMovement + ", " + yMovement + ", " + zMovement + ") is " + (onGround ? "" : " not") + " on ground.");
		}
		else if(id == 0x29)
		{
			logPacket("Entity movement relative and looks");
			
			int entityID = reader.readCInt();
			int xMovement = reader.readShort();
			int yMovement = reader.readShort();
			int zMovement = reader.readShort();
			boolean onGround = reader.readBoolean();
			
			int yawAngle = reader.readByte();
			int pitchAngle = reader.readByte();
			
			logData("Entity " + entityID + " moved (" + xMovement + ", " + yMovement + ", " + zMovement + ") is " + (onGround ? "" : " not") + " on ground. Yaw: " + yawAngle + " Pitch: " + pitchAngle);
		}
		else if(id == 0x2A)
		{
			logPacket("Entity look direction");
			
			int entityID = reader.readCInt();
			
			int yawAngle = reader.readByte();
			int pitchAngle = reader.readByte();
			
			logData("Entity " + entityID + " changed look direction. Yaw: " + yawAngle + " Pitch: " + pitchAngle);
		}
		else if(id == 0x50)
		{
			logPacket("Entity teleport");
			
			int entityID = reader.readCInt();
			int xMovement = reader.readShort();
			int yMovement = reader.readShort();
			int zMovement = reader.readShort();
			boolean onGround = reader.readBoolean();
			
			int yawAngle = reader.readByte();
			int pitchAngle = reader.readByte();
			
			logData("Entity " + entityID + " teleported (" + xMovement + ", " + yMovement + ", " + zMovement + ") is " + (onGround ? "" : " not") + " on ground. Yaw: " + yawAngle + " Pitch: " + pitchAngle);
		}
		else if(id == 0x26)
		{
			logPacket("Map \"data\"");
		}
		else if(id == 0x42)
		{
			logPacket("Entity equip");
		}
		else if(id == 0x41)
		{
			logPacket("Entity velocity");
		}
		else if(id == 0x00)
		{
			logPacket("Spawn object");
		}
		else if(id == 0x39)
		{
			logPacket("Entity head look");
		}
		else if(id == 0x0A)
		{
			logPacket("Block action");
		}
		else if(id == 27)
		{
			logPacket("Disconnected by server");
			ParsedMessageContainer message = new ParsedMessageContainer(reader.readString());
			try
			{
				System.out.println(message.getRawJson());
				logData("Disconnected by Server: " + message.getRawJson());
				dataDude.systemMessage("Disconnected by server: " + message.getRawJson());
			}
			catch(JSONException | ChatFormatException e)
			{
				System.out.println("Disconnected by server, original JSON: " + message.getRawJson());
				e.printStackTrace(System.out);
			}
		}
		else if(id == 14)
		{
			logPacket("Chat");
			String jsonMessage = reader.readString();
			logData("Message in " + reader.readUByte() + ": " + jsonMessage);
			
			dataDude.newChatJSON(jsonMessage);
		}
		else if(id == 74)
		{
			logPacket("WorldTime");
			
			//STOP SPAMMING!
//			long worldAge = reader.readLong();
//			long dayTime = reader.readLong();
//			
//			logData("AgeOfWorld: " + worldAge + " Time: " + dayTime);
		}
		else if(id == 13)
		{
			logPacket("Server difficulty");
			//UByte -> difficulty
		}
		else if(id == 0x3d)
		{
			logPacket("Inventory Slot selected");
		}
		else if(id == 0x54)
		{
			logPacket("Declare Receipts (big packet)");
			//CInt -> Amount
			//[
			//String -> Identifier
			//String -> Where
			//.....
			//]
		}
		else if(id == 0x55)
		{
			logPacket("\"Tags\"");
			//Mapping for BlockID <-> BlockIdentifier?
		}
		else if(id == 0x1c)
		{
			logPacket("Entity status");
		}
		else if(id == 0x11)
		{
			logPacket("Declare command (:@)");
		}
		else if(id == 0x34)
		{
			logPacket("Unlock receipts");
		}
		else if(id == 0x3F)
		{
			logPacket("Entity metadata");
		}
		else if(id == 0x02)
		{
			logPacket("Spawn entity (thunderbolt)???");
			//CInt -> EntityID
			//Byte -> Type
			//Double -> x
			//Double -> y
			//Double -> z
		}
		else if(id == 25)
		{
			logPacket("\"Plugin\" Message");
			//String -> channel
			//Byte[] -> data
		}
		else if(id == 0x51)
		{
			logPacket("Advancements");
			//Dough - big one
		}
		else if(id == 0x52)
		{
			logPacket("Entity Properties");
		}
		else if(id == 0x15)
		{
			logPacket("Window item update");
		}
		else if(id == 0x17)
		{
			logPacket("Set Inventory Slot");
		}
		else if(id == 0x49)
		{
			logPacket("Spawn Position");
		}
		else if(id == 0x3b)
		{
			logPacket("World Border");
		}
		else if(id == 37)
		{
			//TBI: here or in the player one?
			logPacket("JoinPlayerState");
			
			//Int -> ID
			int eid = reader.readInt();
			System.out.println("EID = " + eid);
			dataDude.getCurrentServer().getMainPlayer().setEntityID(eid);

			//UByte -> Gamemode
			int gamemode = reader.readUByte();
			dataDude.getCurrentServer().getMainPlayer().setGameMode(gamemode);
			
			//Int -> Dimension Type
			int dimension = reader.readInt();
			dataDude.getCurrentServer().getWorldManager().respawn(dimension);
			
			//UByte -> Difficulty
			//UByte -> Max players (ignored)
			//String -> Map type...?
			//Boolean -> Show Debug
		}
		else if(id == 0x38)
		{
			logPacket("(Re)spawn to dimension");
			int dimension = reader.readInt();
			logData("Spawned in dimension: " + dimension);
			dataDude.getCurrentServer().getWorldManager().respawn(dimension);
		}
		else
		{
			logPacket("||> Packet: ID:" + id + "(0x" + (Integer.toHexString(id)) + ")" + " Size:" + reader.remaining());
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
