package de.ecconia.mc.jclient.chat.json.parser;

import java.util.ArrayList;
import java.util.List;

import de.ecconia.mc.jclient.chat.json.JSONException;

public class JSONToken
{
	private final Type type;
	private Character content;
	
	private JSONToken(Type type, char content)
	{
		this.type = type;
		this.content = content;
	}
	
	private JSONToken(Type type)
	{
		this.type = type;
	}
	
	public Character getContent()
	{
		return content;
	}
	
	public Type getType()
	{
		return type;
	}
	
	@Override
	public String toString()
	{
		return type.name() + (content != null ? "<" + content + ">": "");
	}
	
	//### Parser ###
	
	public enum Type
	{
		OBJECT_OPEN,
		OBJECT_CLOSE,
		ARRAY_OPEN,
		ARRAY_CLOSE,
		QUOTE,
		SEPARATOR,
		PAIR_SEPARATOR,
		TEXT,
		UNKNOWN,
		SPACE,
	}
	
	public static TokenIterator parse(String s)
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
					tokens.add(new JSONToken(Type.QUOTE));
					inQuote = false;
				}
				else
				{
					tokens.add(new JSONToken(Type.TEXT, c));
				}
			}
			else
			{
				switch(c)
				{
				case ':':
					tokens.add(new JSONToken(Type.PAIR_SEPARATOR));
					continue;
				case '{':
					tokens.add(new JSONToken(Type.OBJECT_OPEN));
					continue;
				case '}':
					tokens.add(new JSONToken(Type.OBJECT_CLOSE));
					continue;
				case '[':
					tokens.add(new JSONToken(Type.ARRAY_OPEN));
					continue;
				case ']':
					tokens.add(new JSONToken(Type.ARRAY_CLOSE));
					continue;
				case ',':
					tokens.add(new JSONToken(Type.SEPARATOR));
					continue;
				case '\n':
				case ' ':
				case '\t':
					tokens.add(new JSONToken(Type.SPACE, c));
					continue;
				case '"':
					inQuote = true;
					tokens.add(new JSONToken(Type.QUOTE));
					continue;
				default:
					tokens.add(new JSONToken(Type.UNKNOWN, c));
					continue;
				}
			}
		}
		
		return new TokenIterator(tokens);
	}
	
	public static class TokenIterator
	{
		private final List<JSONToken> list;
		private int pointer = 0;
		
		private String debug = "Debug: ";
		
		private TokenIterator(List<JSONToken> list)
		{
			this.list = list;
		}
		
		public boolean isEnd()
		{
			return list.size() == pointer;
		}
		
		public JSONToken next()
		{
			try
			{
				JSONToken t = list.get(pointer++);
				debug += t.getType() + (t.getContent() != null ? "<" + t.getContent() + ">" : "") + " ";
				
				return t;
			}
			catch(IndexOutOfBoundsException e)
			{
				throw new JSONException("Unexpected end of JSON.");
			}
		}
		
		public String history()
		{
			return debug;
		}

		public void undo()
		{
			debug += "undo";
			pointer--;
		}
	}
}
