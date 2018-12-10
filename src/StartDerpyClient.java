import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import reading.DirtyStreamEndException;
import reading.helper.Provider;
import reading.helper.StreamProvider;
import reading.reader.PlayReader;
import sending.BasicRequests;

public class StartDerpyClient
{
	public static void main(String[] args)
	{
		System.out.println("Connecting to a MC server...");
		long start = System.currentTimeMillis();
		System.out.println();
		
		try
		{
			//A little bit of advertising, the redstone-server is my main server.
			//A great server to play with others and build/learn redstone.
			Socket connection = new Socket("s.redstone-server.info", 25565);
//			Socket connection = new Socket("localhost", 25565);
			
			InputStream is = connection.getInputStream();
			OutputStream os = connection.getOutputStream();
			
//			BasicRequests.sendPingType0(os);
//			BasicRequests.sendPingType1(os);
//			BasicRequests.sendPingTypeX(os);
//			BasicRequests.sendPingTypeNormal(os);
			BasicRequests.tryLogin(os);
			
			System.out.println();
			
//			new LegacyPingReader(is);
//			new NormalPingReader(is);
			new PlayReader(is, os);
//			justPrint(is);
			
			System.out.println();
			connection.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception: " + e.getClass().getSimpleName() + " Message: " + e.getMessage());
			System.out.println();
			e.printStackTrace();
		}
		
		System.out.println("Disconnected.");
		System.out.println("Duration: " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
	}
	
	/**
	 * Should print everything until the connection is closed.
	 * @throws IOException 
	 */
	public static void justPrint(InputStream is) throws IOException
	{
		Provider p = new StreamProvider(is);
		
		System.out.println("Printing everything:");
		
		int before;
		try
		{
			before = p.getByte();
			System.out.println("> " + safe(before) + " (" + before + ")");
			
			while(true)
			{
				int next = p.getByte();
				
				int merged = (before << 8) + next;
				
				System.out.println("> " + safe(next) + " " + safe(merged) + " (" + next + " " + merged + ")");
				
				before = next;
			}
		}
		catch(DirtyStreamEndException x)
		{
			System.out.println(">>> EndOfLine!");
		}
	}
	
	public static String safe(int i)
	{
		if(i == 0)
		{
			return "Ø";
		}
		else
		{
			return "" + (char) i;
		}
	}
}
