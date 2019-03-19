package de.ecconia.mc.jclient.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import de.ecconia.mc.jclient.tools.json.JSONNode;
import de.ecconia.mc.jclient.tools.json.JSONObject;
import de.ecconia.mc.jclient.tools.json.JSONParser;

public class Credentials
{
	public static String accessToken;
	public static String username;
	//TODO: Store as UUID
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
				fw.write("\t\"_getByCommand\": \"Each argument can be replaced by the return value of a system command. Set the first char in the string to #, the rest will be executed as command.\",\n");
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
		
		JSONNode node = null;
		try
		{
			node = JSONParser.parse(content);
		}
		catch(Exception e)
		{
			System.out.println("user.json could not be parsed, please delete to create a new file on start.");
			System.exit(1);
		}
		
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
				
				String uuid = (String) value;
				
				boolean byCommand = false;
				if(!uuid.isEmpty() && uuid.charAt(0) == '#')
				{
					byCommand = true;
					uuid = runCommand(uuid.substring(1));
				}
				
				uuid = ((String) value).toLowerCase();
				
				if(!uuid.matches("^[a-f0-9]{32}$|^[a-f0-9]{8}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{12}$"))
				{
					System.out.println("Invalid UUID " + (byCommand ? "returned by command" : "in user.json"));
					System.exit(1);
				}
				
				Credentials.uuid = uuid;
			}
			else if("username".equals(key))
			{
				Object value = entry.getValue();
				if(!(value instanceof String))
				{
					System.out.println("The username in user.json has to be a String with length 3 to 16. No invalid characters.");
					System.exit(1);
				}
				
				String username = (String) value;
				
				boolean byCommand = false;
				if(!username.isEmpty() && username.charAt(0) == '#')
				{
					byCommand = true;
					username = runCommand(username.substring(1));
				}
				
				if(!username.matches("^[a-z_A-Z0-9]{3,16}$"))
				{
					System.out.println("Invalid username " + (byCommand ? "returned by command" : "in user.json"));
					System.exit(1);
				}
				
				Credentials.username = username;
			}
			else if("accessToken".equals(key))
			{
				Object value = entry.getValue();
				if(!(value instanceof String))
				{
					System.out.println("The accessToken in user.json has to be a String with length 32 or 36.");
					System.exit(1);
				}
				
				String accessToken = ((String) value);
				
				boolean byCommand = false;
				if(!accessToken.isEmpty() && accessToken.charAt(0) == '#')
				{
					byCommand = true;
					accessToken = runCommand(accessToken.substring(1));
				}
				
				accessToken = accessToken.toLowerCase();
				
				if(!accessToken.matches("^[a-f0-9]{32}$|^[a-f0-9]{8}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{12}$"))
				{
					System.out.println("Invalid accessToken " + (byCommand ? "returned by command" : "in user.json"));
					System.exit(1);
				}
				
				Credentials.accessToken = accessToken;
			}
			//Else ignore.
		}
		
		if(uuid == null || accessToken == null || username == null)
		{
			System.out.println("user.json does not contain username/accessToken/uuid please add or delete the file for a new copy.");
			System.exit(1);
		}
	}
	
	private static String runCommand(String command)
	{
		try
		{
			Process commandProcess = Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", command});
			
			try
			{
				commandProcess.waitFor();
			}
			catch(InterruptedException e)
			{
			}
			
			boolean failed = commandProcess.exitValue() != 0;
			InputStream is = failed ? commandProcess.getErrorStream() : commandProcess.getInputStream();
			
			int in;
			String value = "";
			while((in = is.read()) != -1)
			{
				if(in == '\n')
				{
					break;
				}
				
				value += (char) in;
			}
			
			is.close();
			
			if(failed)
			{
				System.out.println("Command returned error: " + command);
				System.out.println("Message: " + value);
				System.exit(0);
			}
			
			return value;
		}
		catch(IOException e)
		{
			System.out.println("Could not execute command >" + command + "<.");
			System.out.println("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			System.exit(0);
			return null;
		}
	}
}
