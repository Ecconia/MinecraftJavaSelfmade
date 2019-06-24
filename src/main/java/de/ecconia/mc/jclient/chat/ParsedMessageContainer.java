package de.ecconia.mc.jclient.chat;

import de.ecconia.java.json.JSONObject;
import de.ecconia.java.json.JSONParser;
import de.ecconia.mc.jclient.chat.parser.ChatParser;
import de.ecconia.mc.jclient.chat.parser.ChatSegment;

public class ParsedMessageContainer
{
	private final String json;
	
	private JSONObject jsonObject;
	private String plainMessage;
	private ChatSegment segment;
	
	public ParsedMessageContainer(String json)
	{
		this.json = json;
	}
	
	public String getRawJson()
	{
		return json;
	}
	
	public JSONObject getJSONObject()
	{
		if(jsonObject == null)
		{
			jsonObject = (JSONObject) JSONParser.parse(json);
		}
		
		return jsonObject;
	}
	
	public String getPlainMessage()
	{
		if(plainMessage == null)
		{
			plainMessage = SimpleChatParser.toSimpleMessage(getJSONObject());
		}
		
		return plainMessage;
	}
	
	public ChatSegment getChatSegment()
	{
		if(segment == null)
		{
			segment = ChatParser.parse(getJSONObject());
		}
		
		return segment;
	}
}
