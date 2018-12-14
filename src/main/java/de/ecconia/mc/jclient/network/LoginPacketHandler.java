package de.ecconia.mc.jclient.network;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.connector.Connector;
import de.ecconia.mc.jclient.network.connector.PacketHandler;
import de.ecconia.mc.jclient.network.tools.encryption.AsyncCryptTools;
import de.ecconia.mc.jclient.network.tools.encryption.SyncCryptUnit;
import de.ecconia.mc.jclient.network.web.AuthServer;
import de.ecconia.mc.jclient.tools.PrintUtils;
import old.packet.MessageBuilder;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;

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
			Provider p = new ArrayProvider(bytes);
			int id = p.readCInt();
			System.out.println(">>> Packet with ID:" + id + " Size:" + p.remainingBytes());
			
			//State for login packets
			if(id == 0)
			{
				//Error packet!
				System.out.println("Packet: Error");
				System.out.println("String: " + p.readString());
			}
			else if(id == 1)
			{
				//Encryption request packet!
				System.out.println("Packet: Encryption request");
				
				String serverCode;
				byte[] pubkeyBytes;
				byte[] verifyToken;
				{
					serverCode = p.readString();
					System.out.println("> ServerID: >" + serverCode + "<");
					
					int lengthPubKey = p.readCInt();
					System.out.println("> Pubkey (" + lengthPubKey + "):");
					pubkeyBytes = p.readBytes(lengthPubKey);
					PrintUtils.printBytes(pubkeyBytes);
					
					int lengthVerifyToken = p.readCInt();
					System.out.println("> Verify token (" + lengthVerifyToken + "):");
					verifyToken = p.readBytes(lengthVerifyToken);
					PrintUtils.printBytes(verifyToken);
				}
				
				SecretKey sharedKey = SyncCryptUnit.generateKey();
				PublicKey serverPublicKey = AsyncCryptTools.bytesToPublicKey(pubkeyBytes);
				String serverHash = AsyncCryptTools.generateHashFromBytes(serverCode, sharedKey.getEncoded(), serverPublicKey.getEncoded());
				
				//Auth-Server request.
				System.out.println();
				System.out.println(">>Contacting auth server...");
				AuthServer.join(serverHash);
				System.out.println(">>Done.");
				
				byte[] sharedSecret = AsyncCryptTools.encryptBytes(serverPublicKey, sharedKey.getEncoded());
				byte[] clientVerifyToken = AsyncCryptTools.encryptBytes(serverPublicKey, verifyToken);
				
				//##########################################
				
				MessageBuilder mb = new MessageBuilder();
				mb.addCInt(sharedSecret.length);
				mb.addBytes(sharedSecret);
				mb.addCInt(clientVerifyToken.length);
				mb.addBytes(clientVerifyToken);
				
				mb.prepandCInt(1);
				con.sendPacket(mb.asBytes());
				
				con.enableEncryption(sharedKey);
				
				System.out.println();
				System.out.println("Established connection, logged in.");
				System.out.println("Now switching to Play protocol.");
				System.out.println();
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
