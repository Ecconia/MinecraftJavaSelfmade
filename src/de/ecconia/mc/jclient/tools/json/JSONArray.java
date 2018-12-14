package de.ecconia.mc.jclient.tools.json;

import java.util.ArrayList;
import java.util.List;

public class JSONArray extends JSONNode
{
	List<Object> entries = new ArrayList<>();
	
	public void add(Object obj)
	{
		entries.add(obj);
	}
	
	@Override
	public void print(String prefix)
	{
		System.out.println('[');
		String innerPrefix = prefix + "·   ";
		for(Object obj : entries)
		{
			System.out.print(innerPrefix);
			if(obj instanceof String)
			{
				System.out.println("\"" + ((String) obj).replace("\n", "\\n") + "\"");
			}
			else
			{
				JSONNode node = (JSONNode) obj;
				node.print(innerPrefix);
			}
		}
		System.out.println(prefix + "]");
	}
	
	public List<Object> getEntries()
	{
		return entries;
	}
}
