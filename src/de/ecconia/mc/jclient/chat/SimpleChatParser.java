package de.ecconia.mc.jclient.chat;

import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ecconia.mc.jclient.chat.json.JSONArray;
import de.ecconia.mc.jclient.chat.json.JSONObject;
import de.ecconia.mc.jclient.chat.json.parser.JSONParser;

public class SimpleChatParser
{
	public static String parseString(String jsonMessage)
	{
		JSONObject messageObject = (JSONObject) JSONParser.parse(jsonMessage);
		
		String oMessage = parse(messageObject);
		
		return deunicode(oMessage);
	}
	
//	public static void main(String[] args)
//	{
//		String in = "\\u003da\\u003d";
//		String out = deunicode(in);
//		System.out.println(out);
//	}
	
	private static Pattern p = Pattern.compile("(\\\\\\\\|\\\\u[0-9a-fA-F]{4})|\\\\n");
	
	public static String deunicode(String in)
	{
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
			
			m.appendReplacement(sb, Matcher.quoteReplacement(out));
		}
		
		m.appendTail(sb);
		
		return sb.toString();
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
