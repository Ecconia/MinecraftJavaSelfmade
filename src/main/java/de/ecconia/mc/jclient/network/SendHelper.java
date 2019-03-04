package de.ecconia.mc.jclient.network;

import de.ecconia.mc.jclient.network.connector.Sender;
import old.packet.MessageBuilder;

public class SendHelper
{
	public static final int HandMain = 0;
	public static final int HandOff = 1;
	
	public static void useItem(Sender sender)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addCInt(HandMain);
		mb.prependCInt(0x2A);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void sendPlayerPosition(Sender sender, boolean onGound)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addBoolean(onGound);
		mb.prependCInt(0x0F);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void sendPlayerPosition(Sender sender, boolean onGound, double x, double y, double z)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addDouble(x);
		mb.addDouble(y);
		mb.addDouble(z);
		mb.addBoolean(onGound);
		mb.prependCInt(0x10);
		sender.sendPacket(mb.asBytes());
	}
	
	public static void sendPlayerPosition(Sender sender, boolean onGound, double x, double y, double z, float neck, float rotation)
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
	
	public static void sendPlayerPosition(Sender sender, boolean onGound, float neck, float rotation)
	{
		MessageBuilder mb = new MessageBuilder();
		mb.addFloat(rotation);
		mb.addFloat(neck);
		mb.addBoolean(onGound);
		mb.prependCInt(0x12);
		sender.sendPacket(mb.asBytes());
	}
}
