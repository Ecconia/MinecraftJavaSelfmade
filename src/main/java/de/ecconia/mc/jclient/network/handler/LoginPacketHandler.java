package de.ecconia.mc.jclient.network.handler;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.main.Logger;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
import de.ecconia.mc.jclient.network.packeting.MessageBuilder;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.tools.encryption.AsyncCryptTools;
import de.ecconia.mc.jclient.network.tools.encryption.SyncCryptUnit;
import de.ecconia.mc.jclient.network.web.AuthServer;
import de.ecconia.mc.jclient.tools.PrintUtils;

public class LoginPacketHandler implements PacketHandler
{
	private final Connector con;
	private final PrimitiveDataDude dataDude;
	
	public LoginPacketHandler(PrimitiveDataDude dataDude)
	{
		this.con = dataDude.getCon();
		this.dataDude = dataDude;
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try
		{
			PacketReader reader = new PacketReader(bytes);
			int id = reader.readCInt();
			
			//State for login packets
			if(id == 0)
			{
				//Disconnection packet!
				System.out.println("Disconnection packet while logging in:");
				System.out.println("Message: " + reader.readString());
				
				//TODO: con.disconnect();
			}
			else if(id == 1)
			{
				//Encryption request packet!
				String serverCode;
				byte[] pubkeyBytes;
				byte[] verifyToken;
				{
					serverCode = reader.readString();
					if(!serverCode.isEmpty())
					{
						Logger.important("The server ID was not empty! >" + serverCode + "<");
						PrintUtils.printBytes(serverCode.getBytes());
					}
					
					int lengthPubKey = reader.readCInt();
					pubkeyBytes = reader.readBytes(lengthPubKey);
					
					int lengthVerifyToken = reader.readCInt();
					verifyToken = reader.readBytes(lengthVerifyToken);
				}
				
				SecretKey sharedKey = SyncCryptUnit.generateKey();
				PublicKey serverPublicKey = AsyncCryptTools.bytesToPublicKey(pubkeyBytes);
				String serverHash = AsyncCryptTools.generateHashFromBytes(serverCode, sharedKey.getEncoded(), serverPublicKey.getEncoded());
				
				//Auth-Server request.
				AuthServer.join(serverHash);
				
				byte[] sharedSecret = AsyncCryptTools.encryptBytes(serverPublicKey, sharedKey.getEncoded());
				byte[] clientVerifyToken = AsyncCryptTools.encryptBytes(serverPublicKey, verifyToken);
				
				//##########################################
				
				MessageBuilder mb = new MessageBuilder();
				mb.addCInt(sharedSecret.length);
				mb.addBytes(sharedSecret);
				mb.addCInt(clientVerifyToken.length);
				mb.addBytes(clientVerifyToken);
				
				mb.prependCInt(1);
				con.sendPacket(mb.asBytes());
				
				con.enableEncryption(sharedKey);
			}
			else if(id == 2)
			{
				//TODO: Validate that these two values are correct:
				//UUID uuid = UUID.fromString(reader.readString());
				//String username = reader.readString();
				
				System.out.println("Established connection, joining.");
				System.out.println("-----------------------------------------");
				
				dataDude.connectedToServer();
				con.setHandler(new PlayPacketHandler(dataDude));
			}
			else if(id == 3)
			{
				logPacket("Compression request");
				int compressionLevel = reader.readCInt();
				logData("> Compression above " + compressionLevel + " bytes.");
				
				con.setCompression(compressionLevel);
			}
			else if(id == 4)
			{
				int messageID = reader.readCInt();
				String identifier = reader.readString();
				byte[] data = reader.readBytes(reader.remaining());
				
				StringBuilder builder = new StringBuilder();
				for(byte b : data)
				{
					builder.append(' ');
					builder.append(b & 255);
				}
				
				System.out.println("Plugin login request: " + messageID + " " + identifier + " Data (" + data.length + "):" + builder.toString());
				
				MessageBuilder mb = new MessageBuilder();
				mb.addCInt(messageID);
				mb.addBoolean(false);

				mb.prependByte(2);
				con.sendPacket(mb.asBytes());
			}
			else
			{
				Logger.warn("Recived unexpected packet while login: " + id);
			}
		}
		catch(Exception e)
		{
			Logger.ex("while reading packet", e);
		}
	}
	
	private void logPacket(String name)
	{
		L.writeLineOnChannel("Packets", name);
	}
	
	private void logData(String message)
	{
		L.writeLineOnChannel("Content", message);
	}
}
