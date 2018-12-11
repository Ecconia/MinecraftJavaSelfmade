package de.ecconia.mc.jclient;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import de.ecconia.mc.jclient.connection.Connector;
import de.ecconia.mc.jclient.connection.PacketHandler;
import old.packet.MessageBuilder;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;
import old.sessions.AuthServer;

public class LoginPacketHandler implements PacketHandler
{
	private final Connector con;
	
	public LoginPacketHandler(Connector con)
	{
		this.con = con;
	}
	
	@Override
	public void onPacketReceive(byte[] bytes)
	{
		try {
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
					System.out.println("ServerID: >" + serverCode + "<");
					
					int lengthPubKey = p.readCInt();
					System.out.println("Pubkey (" + lengthPubKey + "):");
					pubkeyBytes = p.readBytes(lengthPubKey);
					PrintUtils.printBytes(pubkeyBytes);
					
					int lengthVerifyToken = p.readCInt();
					System.out.println("Verify token (" + lengthVerifyToken + "):");
					verifyToken = p.readBytes(lengthVerifyToken);
					PrintUtils.printBytes(verifyToken);
					
					if(p.remainingBytes() > 0)
					{
						System.out.print("Remaining content: ");
						PrintUtils.printBytes(p.readBytes(p.remainingBytes()));
					}
				}
				
				System.out.println("Generating encryption things 1...");
				
				SecretKey sharedKey;
				try
				{
					KeyGenerator gen = KeyGenerator.getInstance("AES");
					gen.init(128);
					sharedKey = gen.generateKey();
				}
				catch(Exception e)
				{
					throw new RuntimeException("Cannot generate shared secret: " + e.getMessage());
				}
				
				PublicKey serverPublicKey;
				try
				{
					serverPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubkeyBytes));
				}
				catch(Exception e)
				{
					throw new RuntimeException("Could not convert public server key: " + e.getMessage());
				}
				
				String serverHash;
				try
				{
					MessageDigest digest = MessageDigest.getInstance("SHA-1");
					digest.update(serverCode.getBytes("ISO_8859_1"));
					digest.update(sharedKey.getEncoded());
					digest.update(serverPublicKey.getEncoded());
					serverHash = new BigInteger(digest.digest()).toString(16);
				}
				catch(Exception e)
				{
					throw new RuntimeException("Could not generate server hash: " + e.getMessage());
				}
				
				//Auth-Server request.
				System.out.println("Contacting Auth-Servers...");
				AuthServer.join(serverHash);
				
				System.out.println("Generating encryption things 2...");
				
				byte[] sharedSecret;
				try
				{
					Cipher cipher = Cipher.getInstance(serverPublicKey.getAlgorithm().equals("RSA") ? "RSA/ECB/PKCS1Padding" : "AES/CFB8/NoPadding");
					cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
					sharedSecret = cipher.doFinal(sharedKey.getEncoded());
				}
				catch(Exception e)
				{
					throw new RuntimeException("Could not encrypt server encryption data: " + e.getMessage());
				}
				
				byte[] clientVerifyToken;
				try
				{
					Cipher cipher = Cipher.getInstance(serverPublicKey.getAlgorithm().equals("RSA") ? "RSA/ECB/PKCS1Padding" : "AES/CFB8/NoPadding");
					cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
					clientVerifyToken = cipher.doFinal(verifyToken);
				}
				catch(Exception e)
				{
					throw new RuntimeException("Could not encrypt server encryption data: " + e.getMessage());
				}
				
				MessageBuilder mb = new MessageBuilder();
				mb.addCInt(sharedSecret.length);
				mb.addBytes(sharedSecret);
				mb.addCInt(clientVerifyToken.length);
				mb.addBytes(clientVerifyToken);
				
				mb.prepandCInt(1);
				con.sendPacket(mb.asBytes());
				System.out.println("Sent answer to server.");
				System.out.println("Enabling encryption...");
				
				con.enableEncryption(sharedKey);
				
				con.setHandler(new PlayPacketHandler(con));
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR: Exception while reading packet: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}
