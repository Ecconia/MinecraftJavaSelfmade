package de.ecconia.mc.jclient.network;

import java.util.UUID;

import de.ecconia.mc.jclient.network.connector.Sender;
import de.ecconia.mc.jclient.network.packeting.MessageBuilder;

public class SendHelper
{
	//0x2a - useItem
	public static final int HandMain = 0;
	public static final int HandOff = 1;
	
	//0x03 - client action
	public static final int ClientActionRestart = 0;
	public static final int ClientActionRequestStats = 1;
	
	//0x19 - player action
	public static final int PlayerMovementActionSneakingStart = 0;
	public static final int PlayerMovementActionSneakingStop = 1;
	public static final int PlayerMovementActionLeaveBed = 2;
	public static final int PlayerMovementActionSprintingStart = 3;
	public static final int PlayerMovementActionSprintingStop = 4;
	public static final int PlayerMovementActionHorseJumpStart = 5;
	public static final int PlayerMovementActionHorseJumpStop = 6;
	public static final int PlayerMovementActionHorseInvOpen = 7;
	public static final int PlayerMovementActionStartElytraing = 8;
	
	//0x18 - player action
	public static final int PlayerActionDiggingStart = 0;
	public static final int PlayerActionDiggingCancel = 1;
	public static final int PlayerActionDiggingFinish = 2;
	public static final int PlayerActionDropItemStack = 3;
	public static final int PlayerActionDropItem = 4;
	public static final int PlayerActionShootArray = 5;
	public static final int PlayerActionFinishEating = 5;
	public static final int PlayerActionSwapItemInHand = 6;
	
	//0x18 - player action
	//0x29 - place block
	public static final int FaceYM = 0;
	public static final int FaceYP = 1;
	public static final int FaceZM = 2;
	public static final int FaceZP = 3;
	public static final int FaceXM = 4;
	public static final int FaceXP = 5;
	public static final int FaceUp = 0;
	public static final int FaceDown = 1;
	public static final int FaceNorth = 2;
	public static final int FaceSouth = 3;
	public static final int FaceWest = 4;
	public static final int FaceEast = 5;
	
	public static void confirmTeleport(Sender sender, int id)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(id);
		mb.prependCInt(0x00);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void queryBlockNBT(Sender sender, int transactionID, int x, int y, int z)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(transactionID);
		mb.addLocation(x, y, z);
		mb.prependCInt(0x01);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void chat(Sender sender, String message)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addString(message);
		mb.prependCInt(0x02);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void clientActions(Sender sender, int action)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(action);
		mb.prependCInt(0x03);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void clientSettings(Sender sender, String locale, int viewDistance, int chatMode, boolean chatColors, int visibleSkinParts, int mainHand)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addString(locale); //TBI: Max "16"
		mb.addByte(viewDistance);
		mb.addCInt(chatMode);
		mb.addBoolean(chatColors);
		mb.addByte(visibleSkinParts);
		mb.addCInt(mainHand);
		mb.prependCInt(0x04);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void tabComplete(Sender sender, int id, String command)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(id);
		mb.addString(command);
		mb.prependCInt(0x05);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void confirmTransaction(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x06);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void enchantItem(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x07);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void clickWindow(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x08);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void closeWindow(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x09);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void pluginMessage(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x0a);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void editBook(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x0b);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void queryEntityNBT(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x0c);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void useEntity(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x0d);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void keepAlive(Sender sender, long id)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addLong(id);
		mb.prependCInt(0x0e);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void playerPosition(Sender sender, boolean onGound)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addBoolean(onGound);
		mb.prependCInt(0x0F);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void playerPosition(Sender sender, boolean onGound, double x, double y, double z)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addDouble(x);
		mb.addDouble(y);
		mb.addDouble(z);
		mb.addBoolean(onGound);
		mb.prependCInt(0x10);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void playerPosition(Sender sender, boolean onGound, double x, double y, double z, float neck, float rotation)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addDouble(x);
		mb.addDouble(y);
		mb.addDouble(z);
		mb.addFloat(rotation);
		mb.addFloat(neck);
		mb.addBoolean(onGound);
		mb.prependCInt(0x11);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void playerPosition(Sender sender, boolean onGound, float neck, float rotation)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addFloat(rotation);
		mb.addFloat(neck);
		mb.addBoolean(onGound);
		mb.prependCInt(0x12);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void vehicleMove(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x13);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void steerBoat(Sender sender, boolean left, boolean right)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addBoolean(left);
		mb.addBoolean(right);
		mb.prependCInt(0x14);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void pickItem(Sender sender, int slot)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(slot);
		mb.prependCInt(0x15);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void craftRecipeRequest(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x16);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	//TBI: What is wrong with this packet?
	public static void playerAbilities(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x17);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void playerAction(Sender sender, int action, int x, int y, int z, int face)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(action);
		mb.addLocation(x, y, z);
		mb.addByte(face);
		mb.prependCInt(0x18);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void playerMovementAction(Sender sender, int playerEntityID, int action, int jumpBoost)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(playerEntityID);
		mb.addCInt(action);
		mb.addCInt(jumpBoost);
		mb.prependCInt(0x19);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void steerVehicle(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x1a);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void receiptBookData(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x1b);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void nameItem(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x1c);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void resourcePackStatus(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x1d);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void advancmentTab(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x1e);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void selectTrade(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x1f);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void updateBeaconEffect(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x20);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void hotbarSelect(Sender sender, int slot)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addShort(slot);
		mb.prependCInt(0x21);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void updateCommandBlock(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x22);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void updateCommandBlockMinecart(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x23);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void creativeInventoryAction(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x24);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void updateStructureBlock(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		//TODO
		mb.prependCInt(0x25);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void updateSign(Sender sender, int x, int y, int z, String line1, String line2, String line3, String line4)
	{
		//TBI: Warning, lines can be max 384 chars/¿bytes? long.
		MessageBuilder mb = new MessageBuilder();
		mb.addLocation(x, y, z);
		mb.addString(line1);
		mb.addString(line2);
		mb.addString(line3);
		mb.addString(line4);
		mb.prependCInt(0x26);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void animation(Sender sender, int hand)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(hand);
		mb.prependCInt(0x27);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void spectate(Sender sender, UUID entityUUID)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addUUID(entityUUID);
		mb.prependCInt(0x28);
		sender.sendPacket(mb.asBytes());
		
		throw new RuntimeException("Missing Impl");
	}
	
	public static void placeBlock(Sender sender, int x, int y, int z, int face, int hand, float cursorX, float cursorY, float cursorZ)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addLocation(x, y, z);
		mb.addCInt(face);
		mb.addCInt(hand);
		mb.addFloat(cursorX);
		mb.addFloat(cursorY);
		mb.addFloat(cursorZ);
		mb.prependCInt(0x29);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void useItem(Sender sender, int hand)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(hand);
		mb.prependCInt(0x2a);
		sender.sendPacket(mb.asBytes());
	}
}
