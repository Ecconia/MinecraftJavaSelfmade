package de.ecconia.mc.jclient.network.packeting;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;

public abstract class PacketThread extends Thread
{
	//TODO: Refactor to something less overblown.
	private final BlockingQueue<GenericPacket> queue = new LinkedBlockingQueue<>();
	
	protected final PrimitiveDataDude dataDude;
	protected final Connector con;
	
	public PacketThread(String name, PrimitiveDataDude dataDude)
	{
		super(name);
		
		this.dataDude = dataDude;
		this.con = dataDude.getCon();
		
		start();
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				GenericPacket packet = queue.take();
				process(packet);
			}
			catch(InterruptedException e)
			{
				System.out.println("Could not handle packet...");
				e.printStackTrace(System.out);
			}
		}
	}
	
	public void handle(GenericPacket packet) throws InterruptedException
	{
		queue.put(packet);
	}
	
	protected abstract void process(GenericPacket packet);
}
