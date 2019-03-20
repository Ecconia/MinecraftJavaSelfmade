package de.ecconia.mc.jclient.resourcegen.blockdata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import de.ecconia.mc.jclient.tools.json.JSONArray;
import de.ecconia.mc.jclient.tools.json.JSONNode;
import de.ecconia.mc.jclient.tools.json.JSONObject;
import de.ecconia.mc.jclient.tools.json.JSONParser;

public class ReadBlockDataJSON
{
	private static Set<String> properties = new HashSet<>();
	
	public static void main(String[] args)
	{
		Map<Integer, BlockDataEntry> bdes = readBlockDataEntries();
		for(Entry<Integer, BlockDataEntry> entry : bdes.entrySet())
		{
			BlockDataEntry bde = entry.getValue();
			System.out.println(entry.getKey() + " - " + bde.getBlockName() + (bde.isDefault() ? " [def]" : "") + ": " + bde.getProperties().stream().map(KeyValue::toString).collect(Collectors.joining(", ")));
		}
	}
	
	private static Map<Integer, BlockDataEntry> readBlockDataEntries()
	{
		File f = new File("files/v1.13.2/blocks.json");
		if(!f.exists())
		{
			die("BlockData file does not exist.");
		}
		
		String jsonString = readFile(f);
		//System.out.println("Done reading.");
		JSONNode node = JSONParser.parse(jsonString);
		//System.out.println("Done parsing.");
		//node.debugTree("");
		
		if(!(node instanceof JSONObject))
		{
			die("File does not have a JSONObject as root element.");
		}
		
		//Start to read the parsed JSON:
		int prefixLength = "minecraft:".length();
		
		Map<Integer, BlockDataEntry> blockdataentries = new HashMap<>();
		
		JSONObject root = (JSONObject) node;
		for(Entry<String, Object> entry : root.getEntries().entrySet())
		{
			String blockName = entry.getKey().substring(prefixLength);
			JSONObject body = (JSONObject) entry.getValue();
			
			JSONObject properties = (JSONObject) body.getEntries().get("properties");
			if(properties != null)
			{
				for(Entry<String, Object> property : properties.getEntries().entrySet())
				{
					String propertyName = property.getKey();
					JSONArray valueArray = (JSONArray) property.getValue();
					List<String> values = new ArrayList<>();
					for(Object value : valueArray.getEntries())
					{
						values.add((String) value);
					}
					ReadBlockDataJSON.properties.add(propertyName + "{" + String.join(", ", values) + "}");
				}
			}
			
			JSONArray states = (JSONArray) body.getEntries().get("states");
			for(Object stateObj : states.getEntries())
			{
				JSONObject state = (JSONObject) stateObj;
				int id = ((Number) state.getEntries().get("id")).intValue();
				boolean def = state.getEntries().get("default") != null && ((Boolean) state.getEntries().get("default")) == true;
				List<KeyValue> propertiesList = new ArrayList<>();
				
				if(state.getEntries().get("properties") != null)
				{
					for(Entry<String, Object> stateEntry : ((JSONObject) state.getEntries().get("properties")).getEntries().entrySet())
					{
						KeyValue kv = new KeyValue(stateEntry.getKey(), (String) stateEntry.getValue());
						propertiesList.add(kv);
					}
				}
				
				BlockDataEntry blockDataEntry = new BlockDataEntry(blockName, propertiesList, def);
				blockdataentries.put(id, blockDataEntry);
			}
		}
		
		return blockdataentries;
	}
	
	public static class BlockDataEntry
	{
		private final String blockName;
		private final boolean isDefault;
		private final List<KeyValue> properties;
		
		public BlockDataEntry(String blockName, List<KeyValue> properties, boolean isDefault)
		{
			this.blockName = blockName;
			this.isDefault = isDefault;
			this.properties = properties;
		}
		
		public String getBlockName()
		{
			return blockName;
		}
		
		public List<KeyValue> getProperties()
		{
			return properties;
		}
		
		public boolean isDefault()
		{
			return isDefault;
		}
	}
	
	public static class KeyValue
	{
		private final String key;
		private final String value;
		
		public KeyValue(String key, String value)
		{
			this.key = key;
			this.value = value;
		}
		
		public String getKey()
		{
			return key;
		}
		
		public String getValue()
		{
			return value;
		}
		
		@Override
		public String toString()
		{
			return key + " = " + value;
		}
	}
	
	private static String readFile(File file)
	{
		try(FileReader reader = new FileReader(file))
		{
			//Assume this fits
			int fileSize = (int) file.length();
			//Assume that each byte could be a character.
			char[] data = new char[fileSize];
			int amountRead = reader.read(data);
			//If smaller: UFT was expanded, if same: All content has been read.
			//Errr does not make much sense?
			if(amountRead <= 0 || amountRead > fileSize)
			{
				die("The file has size " + fileSize + " but " + amountRead + " chars have been read, either there was the use of >8 UFT or ¿it could not read the file? -> Anyway improve/confirm implmentation!");
			}
			
			return new String(data, 0, amountRead);
		}
		catch(IOException e)
		{
			die("IOException, while reading the blockdata file: " + e.getMessage());
			return null; //Compiler statisfaction.
		}
	}
	
	public static void die(String message)
	{
		System.out.println("Terminating: " + message);
		System.exit(0);
	}
}
