package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.gui.tabs.ChatPane;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.tools.PrintUtils;
import old.packet.MessageBuilder;

public class PrimitiveDataDude
{
	private final Connector con;
	private ChatPane chatWindow;
	
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
	
	public void sendChat(String text)
	{
		if(text.charAt(0) == '%')
		{
			if(text.equals("%move"))
			{
				MessageBuilder mb = new MessageBuilder();
				mb.addDouble(2.0D);
				mb.addDouble(0.0D);
				mb.addDouble(0.0D);
				mb.addBoolean(false);
				mb.prependCInt(0x10);
				PrintUtils.printBytes(mb.asBytes());
				con.sendPacket(mb.asBytes());
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
