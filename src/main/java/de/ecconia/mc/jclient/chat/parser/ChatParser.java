package de.ecconia.mc.jclient.chat.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ecconia.mc.jclient.chat.ChatFormatException;
import de.ecconia.mc.jclient.tools.json.JSONArray;
import de.ecconia.mc.jclient.tools.json.JSONObject;

public class ChatParser
{
	private final static Pattern p = Pattern.compile("(\\\\\\\\|\\\\u[0-9a-fA-F]{4})|\\\\n|\\\\\"");
	
	public static String deunicode(String in)
	{
		if(in.isEmpty())
		{
			return in;
		}
		
		Matcher m = p.matcher(in);
		StringBuffer sb = new StringBuffer(in.length());
		
		while(m.find())
		{
			String part = m.group(0);
			
			String out = "";
			if(part.startsWith("\\u"))
			{
				out = String.valueOf((char) new BigInteger(part.substring(2), 16).intValue());
			}
			else if(part.equals("\\\\"))
			{
				out = "\\";
			}
			else if(part.equals("\\n"))
			{
				out = "\n";
			}
			else if(part.equals("\\\""))
			{
				out = "\"";
			}
			
			m.appendReplacement(sb, Matcher.quoteReplacement(out));
		}
		
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	//#########################################################################
	
	public static ChatSegment parse(JSONObject jsonMessage)
	{
		Map<String, Object> entries = jsonMessage.getEntries();
		
		String text = (String) entries.get("text");
		if(text == null)
		{
			throw new ChatFormatException("Segment does not have a text field.");
		}
		
		String colorName = (String) entries.get("color");
		ChatColor color = null;
		if(colorName != null)
		{
			color = ChatColor.getColorByName(colorName);
		}
		
		JSONArray extra = (JSONArray) entries.get("extra");
		List<ChatSegment> extraList;
		if(extra == null)
		{
			extraList = new ArrayList<>(0);
		}
		else
		{
			extraList = new ArrayList<>(extra.getEntries().size());
			for(Object o : extra.getEntries())
			{
				extraList.add(parse((JSONObject) o));
			}
		}
		
		return new ChatSegment(deunicode(text), color, extraList);
	}
	
	public static ChatSegment parse(String text)
	{
		//TODO: Test!!! (Does split do as it should... its a b* most of the time.
		String[] parts = text.split("§");
		
		List<ChatSegment> extra = new ArrayList<>(parts.length);
		extra.add(new ChatSegment(parts[0]));
		
		for(int i = 1; i < parts.length; i++)
		{
			ChatColor color = ChatColor.getColorByChar(parts[i].charAt(0));
			if(color == null && "klmnor".indexOf(parts[i].charAt(0)) == -1)
			{
				System.out.println("WARNING: color char '" + parts[i].charAt(0) + "' not found.");
				throw new ChatFormatException("WARNING: color char '" + parts[i].charAt(0) + "' not found.");
			}
			extra.add(new ChatSegment(parts[i].substring(1), color));
		}
		
		return new ChatSegment(extra);
	}
}
