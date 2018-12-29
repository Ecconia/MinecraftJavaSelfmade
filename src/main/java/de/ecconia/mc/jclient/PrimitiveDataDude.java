package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.gui.tabs.ChatPane;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.tools.McMathHelper;
import de.ecconia.mc.jclient.tools.PrintUtils;
import old.packet.MessageBuilder;

public class PrimitiveDataDude
{
	private final Connector con;
	private ChatPane chatWindow;
	
	private int x;
	private int y;
	private int z;
	
	public PrimitiveDataDude(Connector con)
	{
		this.con = con;
	}
	
	public void connectedToServer()
	{
		chatWindow = new ChatPane(this);
		L.addCustomPanel("Chat", chatWindow);
	}
	
	public void newChatJSON(String json)
	{
		ParsedMessageContainer messageContainer = new ParsedMessageContainer(json);
		
		chatWindow.addJSONLine(messageContainer);
		
		String plainMessage = null;
		try
		{
			plainMessage = messageContainer.getPlainMessage();
		}
		catch(Exception e)
		{
			System.out.println("Exception while parsing json message.");
			e.printStackTrace(System.out);
		}
		
		if(plainMessage != null)
		{
			//Custom code for the redstone-server
			String[] parts = plainMessage.split(":", 2);
			if(parts.length != 2)
			{
				//Its not a chat message, ignore for now.
				return;
			}
			
			if(parts[0].contains(Credentials.username))
			{
				//You are the author, ignore the message.
				return;
			}
			
			String content = parts[1];
			
			if(content.contains("runcolorcommand"))
			{
				System.out.println("Answering...");
				sendChat("/colors");
			}
			else if(plainMessage.contains(Credentials.username))
			{
				System.out.println("Answering...");
				sendChat("Yes? (Automated message)");
			}
		}
	}
	
	//#########################################################################
	//Position handler:
	
	private int chunkX = Integer.MAX_VALUE;
	private int chunkZ = Integer.MAX_VALUE;
	
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
	
	public void newPosition(int x, int y, int z)
	{
		System.out.println("Recieved position: (" + x + ", " + y + ", " + z + ")");
		playerPosHandler.updatePlayerCoords(x, y, z);
		
		int newChunkX = McMathHelper.toChunkPos(x);
		int newChunkZ = McMathHelper.toChunkPos(z);
		
		if(newChunkX != chunkX || newChunkZ != chunkZ)
		{
			chunkX = newChunkX;
			chunkZ = newChunkZ;
			
			System.out.println("New chunk location: " + chunkX + " " + chunkZ);
			
			//Event:
			worldHandler.updateChunkCoords(newChunkX, newChunkZ);
		}
	}
	
	public static interface UpdateChunkPos
	{
		public void updateChunkCoords(int x, int z);
	}
	
	public static interface UpdatePlayerPos
	{
		public void updatePlayerCoords(int x, int y, int z);
	}
	
	public void setPosition(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		
		newPosition(x, y, z);
	}
	
	//#########################################################################
	
	public void sendChat(String text)
	{
		if(text.charAt(0) == '%')
		{
			if(text.equals("%move"))
			{
				System.out.println("Sending Position: (" + x + ", " + y + ", " + z + ")");
				MessageBuilder mb = new MessageBuilder();
				
				x += 4;
				
				mb.addDouble(x);
				mb.addDouble(y);
				mb.addDouble(z);
				mb.addBoolean(true);
				mb.prependCInt(0x10);
				PrintUtils.printBytes(mb.asBytes());
				con.sendPacket(mb.asBytes());
			}
			else if(text.equals("%3"))
			{
				worldHandler.updateChunkCoords(chunkX, chunkZ);
			}
		}
		else
		{
			MessageBuilder mb = new MessageBuilder();
			mb.addString(text);
			mb.prependCInt(2);
			con.sendPacket(mb.asBytes());
		}
	}
	
	public void systemMessage(String text)
	{
		chatWindow.addSystemMessage(text);
	}
	
	public Connector getCon()
	{
		return con;
	}
}
