package old.reading.reader;

import java.io.InputStream;

import old.reading.DirtyStreamEndException;
import old.reading.helper.Provider;
import old.reading.helper.StreamProvider;

public class LegacyPingReader
{
	private final Provider p;
	
	public LegacyPingReader(InputStream is)
	{
		p = new StreamProvider(is);
		
		int init = p.getByte();
		if(init != 255)
		{
			System.out.println("Malformed whatever... First byte not 255!");
		}
		
		String s = "";
		int l = p.getUShort();
		for(int i = 0; i < l; i++)
		{
			s += (char) p.getUShort();
		}
		
		try
		{
			p.getByte();
			System.out.println("WARNING: More bytes to read!");
		}
		catch(DirtyStreamEndException e)
		{
		}
		
		s = s.replace('\0', 'Ø');
		System.out.println("Received message: >" + s + "<");
		System.out.println("First ping type splits on §, the second on \\0 indicated by Ø.");
	}
}
