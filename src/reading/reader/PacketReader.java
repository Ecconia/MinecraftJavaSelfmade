package reading.reader;

import java.io.InputStream;

import reading.DirtyStreamEndException;
import reading.helper.ArrayProvider;
import reading.helper.Provider;
import reading.helper.StreamProvider;

public class PacketReader
{
	protected Provider p;
	
	public PacketReader(InputStream is)
	{
		p = new StreamProvider(is);
	}
	
	public Provider readPacket()
	{
		return new ArrayProvider(p.readBytes(p.readCInt()));
	}
	
	public void justPrint()
	{
		System.out.println();
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
