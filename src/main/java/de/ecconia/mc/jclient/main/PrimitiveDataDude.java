package de.ecconia.mc.jclient.main;

import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.data.ServerData;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.gui.tabs.ChatPane;
import de.ecconia.mc.jclient.network.SendHelper;
import de.ecconia.mc.jclient.network.connector.Connector;

public class PrimitiveDataDude
{
	private ServerData currentServer;
	
	private final Connector con;
	private ChatPane chatWindow;
	
	public PrimitiveDataDude(Connector con)
	{
		this.con = con;
		
		currentServer = new ServerData(con);
	}
	
	public ServerData getCurrentServer()
	{
		return currentServer;
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
	
	public void sendChat(String text)
	{
		if(!text.isEmpty())
		{
			if(text.charAt(0) == '%')
			{
				if(text.equals("%move"))
				{
					double x = currentServer.getMainPlayer().getLocationX();
					double y = currentServer.getMainPlayer().getLocationY();
					double z = currentServer.getMainPlayer().getLocationZ();
					
					System.out.println("Sending Position: (" + x + ", " + y + ", " + z + ")");
					currentServer.getMainPlayer().clientLocation(x + 4, y, z);
				}
				//else if(text.equals("%3"))
				//{
				//	worldHandler.updateChunkCoords(chunkX, chunkZ);
				//}
			}
			else
			{
				SendHelper.chat(con, text);
			}
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
