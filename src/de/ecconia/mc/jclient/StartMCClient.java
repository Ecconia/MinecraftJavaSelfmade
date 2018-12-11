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
import old.cred.Credentials;
import old.packet.MessageBuilder;
import old.reading.helper.ArrayProvider;
import old.reading.helper.Provider;
import old.sessions.AuthServer;

public class StartMCClient
{
	private static int state = 0;
	
	public static void main(String[] args)
	{
		Connector con = new Connector("s.redstone-server.info", 25565);
		
		con.setHandler(bytes -> {
			Provider p = new ArrayProvider(bytes);
			int id = p.readCInt();
			System.out.println(">>> Packet with ID:" + id + " Size:" + p.remainingBytes());
			
			if(state == 0)
			{
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
					
					state = 2;
				}
			}
			else if(state == 2)
			{
				//State for play packet.
				if(id == 3)
				{
					System.out.println("Server requests compression:");
					int compressionLevel = p.readCInt();
					System.out.println("Compression above " + compressionLevel + " bytes.");
					if(p.remainingBytes() > 0)
					{
						System.out.println("WARNING: Compression package had more content.");
					}
					
					con.setCompression(compressionLevel);
				}
				else if(id == 27)
				{
					System.out.println("Packet: Kick!");
					System.out.println("Message: " + p.readString());
				}
				else if(id == 33)
				{
					System.out.println("Packet: KeepAlive");
					byte[] ping = p.readBytes(8);
					
					MessageBuilder mb = new MessageBuilder();
					mb.addBytes(ping);
					mb.prepandCInt(14);
					con.sendPacket(mb.asBytes());
				}
				else if(id == 14)
				{
					System.out.println("Packet: Chat");
					String jsonMessage = p.readString();
					System.out.println("Json: " + jsonMessage);
					System.out.println("Loc: " + p.readByte());
					
					if(jsonMessage.contains(Credentials.USERNAME) && !jsonMessage.contains("joined the game") && !jsonMessage.contains("Discord"))
					{
						System.out.println("Answering...");
						MessageBuilder mb = new MessageBuilder();
						mb.addString("Yes? (Automated message)");
						mb.prepandCInt(2);
						con.sendPacket(mb.asBytes());
					}
				}
			}
		});
		
		new Thread(() -> {
			try
			{
				Thread.sleep(800);
			}
			catch(InterruptedException e)
			{
			}
			
			MessageBuilder mb = new MessageBuilder();
			
			mb.addCInt(404);
			mb.addString("afkserver.com");
			mb.addShort(25565);
			mb.addCInt(2);
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
			
			mb = new MessageBuilder();
			mb.addString(Credentials.USERNAME);
			
			mb.prepandCInt(0);
			con.sendPacket(mb.asBytes());
		}).start();
		
		con.connect();
	}
}
