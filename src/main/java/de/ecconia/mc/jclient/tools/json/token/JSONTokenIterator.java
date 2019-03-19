package de.ecconia.mc.jclient.tools.json.token;

import java.util.List;

import de.ecconia.mc.jclient.tools.json.JSONException;

public class JSONTokenIterator
{
	private final List<JSONToken> list;
	private int pointer = 0;
	
	private String debug = "Debug: ";
	private final boolean debugEnabled;
	
	public JSONTokenIterator(List<JSONToken> list, boolean debugEnabled)
	{
		this.list = list;
		this.debugEnabled = debugEnabled;
		this.debug = debugEnabled ? "Debug: " : "";
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
			if(debugEnabled)
			{
				debug += t.getType() + (t.getContent() != null ? "<" + t.getContent() + ">" : "") + " ";
			}
			
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
		if(debugEnabled)
		{
			debug += "undo";
		}
		pointer--;
	}
}
