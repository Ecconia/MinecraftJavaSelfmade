package de.ecconia.mc.jclient.data.player;

import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.connector.Sender;
import de.ecconia.mc.jclient.tools.McMathHelper;
import old.packet.MessageBuilder;

public class MainPlayer
{
	private int currentChunkX = Integer.MAX_VALUE;
	private int currentChunkZ = Integer.MAX_VALUE;
	
	//TODO: Location object
	private double locationX = 0;
	private double locationY = 0;
	private double locationZ = 0;
	
	private int xp = 0;
	private int health = 0;
	
	//TBI: This is part of direct processing of data in the storage, good or bad thing? Maybe replace with listeners? Bruh rather not.
	private final Sender sender;
	
	public MainPlayer(Sender sender)
	{
		this.sender = sender;
	}
	
	public void serverLocation(double x, double y, double z, float yaw, float pitch)
	{
		locationX = x;
		locationY = y;
		locationZ = z;
		
		//TODO: Crop to relevant length:
		L.writeLineOnChannel("3D-Text", "Recieved position: (" + x + ", " + y + ", " + z + ")");
		
		newPosition(x, y, z);
	}
	
	public void clientLocation(double x, double y, double z)
	{
		locationX = x;
		locationY = y;
		locationZ = z;
		
		//TODO: Crop to relevant length:
		L.writeLineOnChannel("3D-Text", "Walking to: (" + x + ", " + y + ", " + z + ")");
		
		MessageBuilder mb = new MessageBuilder();
		mb.addDouble(locationX);
		mb.addDouble(locationY);
		mb.addDouble(locationZ);
		mb.addBoolean(true);
		mb.prependCInt(0x10);
		sender.sendPacket(mb.asBytes());
		
		newPosition(x, y, z);
	}
	
	public double getLocationX()
	{
		return locationX;
	}
	
	public double getLocationY()
	{
		return locationY;
	}
	
	public double getLocationZ()
	{
		return locationZ;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getXp()
	{
		return xp;
	}
	
	//#########################################################################
	
	//TBI: Here or elsewhere?
	private void newPosition(double x, double y, double z)
	{
		playerPosHandler.updatePlayerCoords(x, y, z);
		
		int newChunkX = McMathHelper.toChunkPos(McMathHelper.toBlockPos(x));
		int newChunkZ = McMathHelper.toChunkPos(McMathHelper.toBlockPos(z));
		
		if(newChunkX != currentChunkX || newChunkZ != currentChunkZ)
		{
			currentChunkX = newChunkX;
			currentChunkZ = newChunkZ;
			
			L.writeLineOnChannel("3D-Text", "New chunk location: " + currentChunkX + " " + currentChunkZ);
			
			//Event:
			worldHandler.updateChunkCoords(currentChunkX, currentChunkZ);
		}
	}
	
	//#########################################################################
	
	private UpdateChunkPos worldHandler;
	private UpdatePlayerPos playerPosHandler;
	
	public void setChunkPosHandler(UpdateChunkPos handler)
	{
		this.worldHandler = handler;
	}
	
	public void setPlayerPositionHandler(UpdatePlayerPos playerPosHandler)
	{
		this.playerPosHandler = playerPosHandler;
	}
	
	public static interface UpdateChunkPos
	{
		public void updateChunkCoords(int x, int z);
	}
	
	public static interface UpdatePlayerPos
	{
		public void updatePlayerCoords(double x, double y, double z);
	}
}
