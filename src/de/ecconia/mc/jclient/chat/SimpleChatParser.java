package de.ecconia.mc.jclient.chat;

import java.util.Map;

import de.ecconia.mc.jclient.chat.parser.ChatParser;
import de.ecconia.mc.jclient.tools.json.JSONArray;
import de.ecconia.mc.jclient.tools.json.JSONObject;

public class SimpleChatParser
{
	public static String toSimpleMessage(JSONObject obj)
	{
		return ChatParser.deunicode(parse(obj));
	}
	
	public static String parse(JSONObject obj)
	{
		String ret = "";
		
		Map<String, Object> entries = obj.getEntries();
		
		ret += entries.get("text");
		JSONArray extra = (JSONArray) entries.get("extra");
		if(extra != null)
		{
			ret += parse(extra);
		}
		
		return ret;
	}
	
	public static String parse(JSONArray obj)
	{
		String ret = "";
		
		for(Object o : obj.getEntries())
		{
			if(o instanceof JSONObject)
			{
				ret += parse((JSONObject) o);
			}
			else
			{
				throw new ChatFormatException("An array did not contain an object as child, but: " + o.getClass().getSimpleName());
			}
		}
		
		return ret;
	}
}
