package de.ecconia.mc.jclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import de.ecconia.mc.jclient.tools.json.JSONNode;
import de.ecconia.mc.jclient.tools.json.JSONObject;
import de.ecconia.mc.jclient.tools.json.JSONParser;

public class Credentials
{
	public static String accessToken;
	public static String username;
	public static String uuid;
	
	public static void load()
	{
		File userfile = new File("user.json");
		if(!userfile.exists())
		{
			try
			{
				FileWriter fw = new FileWriter(userfile);
				fw.write("{\n");
				fw.write("\t\"uuid\": \"\",\n");
				fw.write("\t\"username\": \"\",\n");
				fw.write("\t\"accessToken\": \"\"\n");
				fw.write("}\n");
				fw.flush();
				fw.close();
			}
			catch(IOException e)
			{
				System.out.println("Could not save user.json file, please create it manually, or fix the bug.");
				System.exit(1);
			}
			
			System.out.println("user.json just got created, please add your info.");
			System.exit(1);
		}
		
		String content = "";
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(userfile));
			String in;
			while((in = br.readLine()) != null)
			{
				content += in;
			}
			
			br.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Exception while reading user.json .");
			System.exit(1);
		}
		
		JSONNode node = JSONParser.parse(content);
		if(!(node instanceof JSONObject))
		{
			System.out.println("user.json does not contain a JSON object, please delete to create a new file on start.");
			System.exit(1);
		}
		
		JSONObject userobject = (JSONObject) node;
		for(Entry<String, Object> entry : userobject.getEntries().entrySet())
		{
			String key = entry.getKey();
			if("uuid".equals(key))
			{
				Object value = entry.getValue();
				if(!(value instanceof String))
				{
					System.out.println("The uuid in user.json has to be a String with length 32 or 36.");
					System.exit(1);
				}
				
				String uuid = ((String) value).toLowerCase();
				
				if(!uuid.matches("^[a-f0-9]{32}$|^[a-f0-9]{8}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{12}$"))
				{
					System.out.println("Invalid UUID in user.json");
					System.exit(1);
				}
				
				Credentials.uuid = uuid;
			}
			else if("username".equals(key))
			{
				Object value = entry.getValue();
				if(!(value instanceof String) || !((String) value).matches("^[A-Za-z0-9_]{3,16}$"))
				{
					System.out.println("The username in user.json has to be a String with length 3 to 16. No invalid characters.");
					System.exit(1);
				}
				
				Credentials.username = (String) value;
			}
			else if("accessToken".equals(key))
			{
				Object value = entry.getValue();
				if(!(value instanceof String))
				{
					System.out.println("The accessToken in user.json has to be a String with length 32 or 36.");
					System.exit(1);
				}
				
				String accessToken = ((String) value).toLowerCase();
				
				if(!accessToken.matches("^[a-f0-9]{32}$|^[a-f0-9]{8}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{12}$"))
				{
					System.out.println("Invalid accessToken in user.json");
					System.exit(1);
				}
				
				Credentials.accessToken = accessToken;
			}
			//Else ignore.
		}
	}
}
