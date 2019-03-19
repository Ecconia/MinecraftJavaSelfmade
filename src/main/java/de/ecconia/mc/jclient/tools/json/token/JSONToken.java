package de.ecconia.mc.jclient.tools.json.token;

import java.util.ArrayList;
import java.util.List;

public class JSONToken
{
	private final JSONType type;
	private Character content;
	
	private JSONToken(JSONType type, char content)
	{
		this.type = type;
		this.content = content;
	}
	
	private JSONToken(JSONType type)
	{
		this.type = type;
	}
	
	public Character getContent()
	{
		return content;
	}
	
	public JSONType getType()
	{
		return type;
	}
	
	@Override
	public String toString()
	{
		return type.name() + (content != null ? "<" + content + ">" : "");
	}
	
	//### Parser ###
	
	public static JSONTokenIterator parse(String s, boolean debug)
	{
		List<JSONToken> tokens = new ArrayList<>();
		
		boolean inQuote = false;
		boolean nextEscapted = false;
		boolean thisEscaped = false;
		
		for(char c : s.toCharArray())
		{
			if(inQuote)
			{
				thisEscaped = false;
				if(nextEscapted)
				{
					thisEscaped = true;
					nextEscapted = false;
				}
				else if(c == '\\')
				{
					nextEscapted = true;
				}
				
				if(!thisEscaped && c == '"')
				{
					tokens.add(new JSONToken(JSONType.QUOTE));
					inQuote = false;
				}
				else
				{
					tokens.add(new JSONToken(JSONType.TEXT, c));
				}
			}
			else
			{
				switch(c)
				{
				case ':':
					tokens.add(new JSONToken(JSONType.PAIR_SEPARATOR));
					continue;
				case '{':
					tokens.add(new JSONToken(JSONType.OBJECT_OPEN));
					continue;
				case '}':
					tokens.add(new JSONToken(JSONType.OBJECT_CLOSE));
					continue;
				case '[':
					tokens.add(new JSONToken(JSONType.ARRAY_OPEN));
					continue;
				case ']':
					tokens.add(new JSONToken(JSONType.ARRAY_CLOSE));
					continue;
				case ',':
					tokens.add(new JSONToken(JSONType.SEPARATOR));
					continue;
				case '\n':
				case ' ':
				case '\t':
					tokens.add(new JSONToken(JSONType.SPACE, c));
					continue;
				case '"':
					inQuote = true;
					tokens.add(new JSONToken(JSONType.QUOTE));
					continue;
				default:
					tokens.add(new JSONToken(JSONType.UNKNOWN, c));
					continue;
				}
			}
		}
		
		return new JSONTokenIterator(tokens, debug);
	}
}
