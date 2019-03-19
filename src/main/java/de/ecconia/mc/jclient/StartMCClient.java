package de.ecconia.mc.jclient;

import de.ecconia.mc.jclient.gui.gl.Simple3D;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.main.Credentials;
import de.ecconia.mc.jclient.main.FatalException;
import de.ecconia.mc.jclient.main.Logger;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.handler.LoginPacketHandler;
import de.ecconia.mc.jclient.network.packeting.MessageBuilder;

public class StartMCClient
{
	private static CD cd = null;
	
	public static void main(String[] args)
	{
		try
		{
			//Load credentials from trusted location.
			Credentials.load();
			//Create the debugging window.
			L.init();
			//Create the 3D panel, causes the shaders and other things to be loaded earlier.
			Simple3D simple3D = new Simple3D();
			
			cd = new CD("s.redstone-server.info");
//			cd = new CD("localhost");
//			cd = new CD("localhost", false);
			
			Connector con = new Connector(cd.domain, cd.port, (connector) -> {
				MessageBuilder mb = new MessageBuilder();
				
				mb.addCInt(cd.version);
				mb.addString(cd.domain);
				mb.addShort(666);
				mb.addCInt(2);
				
				mb.prependCInt(0);
				connector.sendPacket(mb.asBytes());
				
				mb = new MessageBuilder();
				mb.addString(Credentials.username);
				
				mb.prependCInt(0);
				connector.sendPacket(mb.asBytes());
			});
			
			PrimitiveDataDude dataDude = new PrimitiveDataDude(con);
			simple3D.attachServer(dataDude);
			
			con.setHandler(new LoginPacketHandler(dataDude));
			con.connect();
		}
		catch(FatalException e)
		{
			Logger.ex("somewhere while runtime", e);
		}
	}
	
	private static class CD
	{
		public String domain;
		public int port = 25565;
		public int version = 404;
		
		public CD(String domain)
		{
			int index = domain.indexOf(':');
			if(index == -1)
			{
				this.domain = domain;
			}
			else
			{
				this.domain = domain.substring(0, index);
				this.port = Integer.parseInt(domain.substring(index + 1));
			}
		}
		
		@SuppressWarnings("unused") //May be used may not be used.
		public CD(String domain, int version)
		{
			this(domain);
			
			this.version = version;
		}
	}
}
