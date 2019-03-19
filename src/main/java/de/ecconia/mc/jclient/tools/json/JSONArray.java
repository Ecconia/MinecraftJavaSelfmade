package de.ecconia.mc.jclient.tools.json;

import java.util.ArrayList;
import java.util.List;

public class JSONArray extends JSONNode
{
	private final List<Object> entries = new ArrayList<>();
	
	public void add(Object obj)
	{
		entries.add(obj);
	}
	
	public List<Object> getEntries()
	{
		return entries;
	}
	
	@Override
	public String printJSON()
	{
		String tmp = "";
		if(!entries.isEmpty())
		{
			tmp += printJSON(entries.get(0));
			
			for(int i = 1; i < entries.size(); i++)
			{
				tmp += ',' + printJSON(entries.get(i));
			}
		}
		
		return '[' + tmp + ']';
	}
	
	@Override
	public void debugTree(String prefix)
	{
		System.out.println('[');
		String innerPrefix = prefix + "·   ";
		for(Object obj : entries)
		{
			System.out.print(innerPrefix);
			if(obj == null)
			{
				System.out.println("null");
			}
			else if(obj instanceof Boolean)
			{
				System.out.println(obj);
			}
			else if(obj instanceof Number)
			{
				System.out.println(obj);
			}
			else if(obj instanceof String)
			{
				System.out.println("\"" + ((String) obj).replace("\n", "\\n") + "\"");
			}
			else
			{
				JSONNode node = (JSONNode) obj;
				node.debugTree(innerPrefix);
			}
		}
		System.out.println(prefix + "]");
	}
}
