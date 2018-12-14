package de.ecconia.mc.jclient.tools.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JSONObject extends JSONNode
{
	private final Map<String, Object> entries = new HashMap<>();
	
	public void put(String key, Object obj)
	{
		entries.put(key, obj);
	}
	
	@Override
	public void print(String prefix)
	{
		System.out.println('{');
		String innerPrefix = prefix + "·   ";
		for(Entry<String, Object> entry : entries.entrySet())
		{
			String key = entry.getKey().replace("\n", "\\n");
			Object obj = entry.getValue();
			
			System.out.print(innerPrefix + "\"" + key + "\": ");
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
		System.out.println(prefix + "}");
	}

	public Map<String, Object> getEntries()
	{
		return entries;
	}
}
