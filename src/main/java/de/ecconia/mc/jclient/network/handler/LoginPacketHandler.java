package de.ecconia.mc.jclient.network.handler;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import de.ecconia.mc.jclient.Logger;
import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.tools.encryption.AsyncCryptTools;
import de.ecconia.mc.jclient.network.tools.encryption.SyncCryptUnit;
import de.ecconia.mc.jclient.network.web.AuthServer;
import de.ecconia.mc.jclient.tools.PrintUtils;
import old.packet.MessageBuilder;

public class LoginPacketHandler implements PacketHandler
{
	private final Connector con;
	private final PrimitiveDataDude dataDude;
	
	public LoginPacketHandler(Connector con, PrimitiveDataDude dataDude)
	{
		this.con = con;
		this.dataDude = dataDude;
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try
		{
			PacketReader reader = new PacketReader(bytes);
			int id = reader.readCInt();
//			System.out.println(">>> Packet with ID:" + id + " Size:" + reader.remaining());
			
			//State for login packets
			if(id == 0)
			{
				//Error packet!
				System.out.println("Error packet while logging in:");
				System.out.println("Message: " + reader.readString());
			}
			else if(id == 1)
			{
				//Encryption request packet!
//				System.out.println("Packet: Encryption request");
				
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
//					System.out.println("> ServerID: >" + serverCode + "<");
					
					int lengthPubKey = reader.readCInt();
//					System.out.println("> Pubkey (" + lengthPubKey + "):");
					pubkeyBytes = reader.readBytes(lengthPubKey);
//					PrintUtils.printBytes(pubkeyBytes);
					
					int lengthVerifyToken = reader.readCInt();
//					System.out.println("> Verify token (" + lengthVerifyToken + "):");
					verifyToken = reader.readBytes(lengthVerifyToken);
//					PrintUtils.printBytes(verifyToken);
				}
				
				SecretKey sharedKey = SyncCryptUnit.generateKey();
				PublicKey serverPublicKey = AsyncCryptTools.bytesToPublicKey(pubkeyBytes);
				String serverHash = AsyncCryptTools.generateHashFromBytes(serverCode, sharedKey.getEncoded(), serverPublicKey.getEncoded());
				
				//Auth-Server request.
//				System.out.println();
//				System.out.println(">>Contacting auth server...");
				AuthServer.join(serverHash);
//				System.out.println(">>Done.");
				
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
				
//				System.out.println();
				System.out.println("Established connection, logged in.");
//				System.out.println("Now switching to Play protocol.");
//				System.out.println();
				System.out.println("-----------------------------------------");
				
				dataDude.connectedToServer();
				con.setHandler(new PlayPacketHandler(con, dataDude));
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR: Exception while reading packet: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
