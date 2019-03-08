package de.ecconia.mc.jclient.data.player;

import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.SendHelper;
import de.ecconia.mc.jclient.network.connector.Sender;
import de.ecconia.mc.jclient.tools.math.McMathHelper;

public class MainPlayer
{
	private int currentChunkX = Integer.MAX_VALUE;
	private int currentChunkZ = Integer.MAX_VALUE;
	
	//TODO: Location object
	private float locationX = 0;
	private float locationY = 0;
	private float locationZ = 0;
	private float neck = 0;
	private float rotation = 0;
	
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
		locationX = (float) x;
		locationY = (float) y;
		locationZ = (float) z;
		
		//TODO: Crop to relevant length:
		L.writeLineOnChannel("3D-Text", "Recieved position: (" + x + ", " + y + ", " + z + ")");
		
		newPosition(x, y, z);
	}
	
	public void clientLocation(double x, double y, double z)
	{
		locationX = (float) x;
		locationY = (float) y;
		locationZ = (float) z;
		
		//TODO: Crop to relevant length:
		L.writeLineOnChannel("3D-Text", "Walking to: (" + x + ", " + y + ", " + z + ")");
		
		SendHelper.playerPosition(sender, true, locationX, locationY, locationZ);
		
		newPosition(x, y, z);
	}
	
	public float getLocationX()
	{
		return locationX;
	}
	
	public float getLocationY()
	{
		return locationY;
	}
	
	public float getLocationZ()
	{
		return locationZ;
	}
	
	public float getNeck()
	{
		return neck;
	}
	
	public float getRotation()
	{
		return rotation;
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
		int newChunkX = McMathHelper.toChunkPos(McMathHelper.toBlockPos(x));
		int newChunkZ = McMathHelper.toChunkPos(McMathHelper.toBlockPos(z));
		
		if(newChunkX != currentChunkX || newChunkZ != currentChunkZ)
		{
			currentChunkX = newChunkX;
			currentChunkZ = newChunkZ;
			
			L.writeLineOnChannel("3D-Text", "New chunk location: " + currentChunkX + " " + currentChunkZ);
			
			//TBI: This method doesn't even matter anymore...
		}
	}
	
	//#########################################################################
	
	public void setNeck(float neck)
	{
		SendHelper.playerPosition(sender, true, neck, rotation);
		this.neck = neck;
	}

	public void setRotation(float rotation)
	{
		SendHelper.playerPosition(sender, true, neck, rotation);
		this.rotation = rotation;
	}
	
	//#########################################################################
	
	//### Player's Entity ID ###
	private int playerEntityID;
	
	public void setEntityID(int eid)
	{
		playerEntityID = eid;
	}
	
	public int getPlayerEntityID()
	{
		return playerEntityID;
	}
	
	//### Player's Entity ID ###
	private int playerGamemode;
	
	public void setGameMode(int gm)
	{
		playerGamemode = gm;
	}
	
	public int getPlayerGamemode()
	{
		return playerGamemode;
	}
}
