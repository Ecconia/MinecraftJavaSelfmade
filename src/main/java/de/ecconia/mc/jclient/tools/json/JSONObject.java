package de.ecconia.mc.jclient.tools.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JSONObject extends JSONNode
{
	private final Map<String, Object> entries = new HashMap<>();
	
	public void put(String key, Object obj)
	{
		entries.put(key, obj);
	}
	
	public Map<String, Object> getEntries()
	{
		return entries;
	}
	
	@Override
	public String printJSON()
	{
		String tmp = "";
		Iterator<Entry<String, Object>> it = entries.entrySet().iterator();
		
		if(it.hasNext())
		{
			Entry<String, Object> entry = it.next();
			tmp += '"' + entry.getKey() + "\":" + printJSON(entry.getValue());
			
			while(it.hasNext())
			{
				entry = it.next();
				tmp += ",\"" + entry.getKey() + "\":" + printJSON(entry.getValue());
			}
		}
		
		return '{' + tmp + '}';
	}
	
	@Override
	public void debugTree(String prefix)
	{
		System.out.println('{');
		String innerPrefix = prefix + "·   ";
		for(Entry<String, Object> entry : entries.entrySet())
		{
			String key = entry.getKey().replace("\n", "\\n");
			Object obj = entry.getValue();
			
			System.out.print(innerPrefix + "\"" + key + "\": ");
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
		System.out.println(prefix + "}");
	}
}
