package de.ecconia.mc.jclient.network.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import de.ecconia.mc.jclient.Credentials;

public class AuthServer
{
	public static void join(String serverHash)
	{
		String accessToken = Credentials.accessToken;
		String uuid = Credentials.uuid;
		
		String link = "https://sessionserver.mojang.com/session/minecraft/join";
		String message = "{"
			+ "\"accessToken\":\"" + accessToken + "\","
			+ "\"selectedProfile\":\"" + uuid.toString().replace("-", "") + "\","
			+ "\"serverId\":\"" + serverHash + "\""
			+ "}";
		
//		System.out.println("Link: " + link);
//		System.out.println("Message: " + message);
		
		String response = request(link, message);
		if(!response.isEmpty())
		{
			System.out.println("Auth server send something on join attempt: " + response);
			System.out.println("Thats probably an error, termination incomming.");
			throw new Error("Auth server couldn't shut up.");
		}
	}
	
	public static String request(String url, String content)
	{
		byte[] payload = content.getBytes();
		
		try
		{
			URLConnection con = new URL(url).openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestProperty("Accept-Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Content-Length", Integer.toString(payload.length));
			
			OutputStream requestStream = con.getOutputStream();
			requestStream.write(payload, 0, payload.length);
			requestStream.close();
			
			BufferedReader inStream;
			int responseCode = ((HttpURLConnection) con).getResponseCode();
			if(responseCode == 200)
			{
				inStream = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			}
			else if(responseCode == 204)
			{ // 204 No content.
				return "";
			}
			else
			{
				inStream = new BufferedReader(new InputStreamReader(((HttpURLConnection) con).getErrorStream(), "UTF-8"));
			}
			String res = inStream.readLine();
			inStream.close();
			return res;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
