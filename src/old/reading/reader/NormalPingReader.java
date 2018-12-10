package old.reading.reader;

import java.io.InputStream;

import old.reading.DirtyStreamEndException;
import old.reading.helper.Provider;

public class NormalPingReader extends PacketReader
{
	public NormalPingReader(InputStream is)
	{
		super(is);
		
		Provider packet = readPacket();
		
		int id = packet.readCInt();
		System.out.println("Packet ID: " + id);
		System.out.println("JSON: " + packet.readString());
		
		if(packet.remainingBytes() > 0)
		{
			System.out.println("WARNING: Packet is longer than expected...!");
		}
		
		System.out.println();
		System.out.println("Checking if there are more packages to read:");
		try
		{
			p.getByte();
			System.out.println("WARNING: More bytes to be read.");
		}
		catch(DirtyStreamEndException e)
		{
			System.out.println("Nope.");
		}
	}
}
