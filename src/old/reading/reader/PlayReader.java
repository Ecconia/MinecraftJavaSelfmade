package old.reading.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import de.ecconia.mc.jclient.PrintUtils;
import de.ecconia.mc.jclient.compressing.Compressor;
import old.cred.Credentials;
import old.packet.MessageBuilder;
import old.reading.helper.ArrayProvider;
import old.reading.helper.EncrpytionProvider;
import old.reading.helper.Provider;
import old.sessions.AuthServer;

public class PlayReader extends PacketReader
{
	private final OutputStream os;
	private Compressor c;
	
	public PlayReader(InputStream is, OutputStream os) throws IOException
	{
		super(is);
		
		this.os = os;
		
		Provider packet = readPacket();
		int packetID = packet.readCInt();
		System.out.println("Recieved Packet with ID: " + packetID);
		System.out.println("Packet size: " + packet.remainingBytes());
		if(packetID == 0)
		{
			//Error packet:
			System.out.println();
			System.out.println("Error packet:");
			System.out.println("Message: " + packet.readString());
			System.out.println("Aborting.");
			return;
		}
		else if(packetID == 1)
		{
			String serverCode;
			byte[] pubkeyBytes;
			byte[] verifyToken;
			{
				System.out.println();
				System.out.println("Content known:");
				serverCode = packet.readString();
				System.out.println("ServerID: >" + serverCode + "<");
				
				int lengthPubKey = packet.readCInt();
				System.out.println("Pubkey length: " + lengthPubKey);
				pubkeyBytes = packet.readBytes(lengthPubKey);
				System.out.println("Pubkey:");
				PrintUtils.printBytes(pubkeyBytes);
				
				int lengthVerifyToken = packet.readCInt();
				System.out.println("Verify token length: " + lengthVerifyToken);
				verifyToken = packet.readBytes(lengthVerifyToken);
				System.out.println("Veryfy token: ");
				PrintUtils.printBytes(verifyToken);
				
				if(packet.remainingBytes() > 0)
				{
					System.out.print("Remaining content: ");
					PrintUtils.printBytes(packet.readBytes(packet.remainingBytes()));
				}
				else
				{
					System.out.println("Packet read.");
				}
			}
			
			/////////////////////////////////////////////////////////
			
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
			System.out.println("ServerHash: " + serverHash);
			AuthServer.join(serverHash);
			
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
			mb.prepandSize();
			mb.write(os);
			
			System.out.println();
			
			try
			{
				encrypter = Cipher.getInstance("AES/CFB8/NoPadding");
				encrypter.init(Cipher.ENCRYPT_MODE, sharedKey, new IvParameterSpec(sharedKey.getEncoded()));
				
				this.p = new EncrpytionProvider(sharedKey, p);
			}
			catch(InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e1)
			{
				System.out.println("Error initing encryption. KMS.");
				e1.printStackTrace();
				return;
			}
			
			int compressionLevel;
			
			//Read uncompressed packets.
			while(true)
			{
				Provider p = readPacket();
				int id = p.readCInt();
				if(id == 3)
				{
					System.out.println("Server requests compression:");
					compressionLevel = p.readCInt();
					System.out.println("Compression above " + compressionLevel + " bytes.");
					if(p.remainingBytes() > 0)
					{
						System.out.println("WARNING: Compression package had more content.");
					}
					break;
				}
				else
				{
					p.reset();
					debugPacket(p);
				}
			}
			
			c = new Compressor(compressionLevel);
			
			while(true)
			{
				//TODO: Finally remove, overcomplicated uncomression call, at least here.
				packet = readPacket();
				int originalSize = packet.readCInt();
				debugPacket(new ArrayProvider(c.uncompress(originalSize, packet.readBytes(packet.remainingBytes()))));
			}
		}
		else
		{
			System.out.println("Unknown packet ID");
		}
	}
	
	private Cipher encrypter;
	private byte[] outputBuffer = new byte[0];
	
	private void encrypt(MessageBuilder mb)
	{
		try
		{
			byte[] inputBytes = mb.asBytes();
			int size = inputBytes.length;
			
			int outputSize = encrypter.getOutputSize(size);
			
			if(this.outputBuffer.length < outputSize)
			{
				this.outputBuffer = new byte[outputSize];
			}
			
			int newSize = encrypter.update(inputBytes, 0, size, this.outputBuffer);
			//TODO: TBI: Investigation if following code is garbage:
			System.out.println("Size of encrypted before after. Equals: " + (newSize == outputSize));
			byte[] outputBytes = new byte[newSize];
			System.arraycopy(outputBuffer, 0, outputBytes, 0, newSize);
			mb.fromBytes(outputBytes);
		}
		catch(ShortBufferException e)
		{
			System.out.println("Could not encrypt packet.");
			e.printStackTrace();
		}
	}
	
	private void debugPacket(Provider p)
	{
		int size = p.remainingBytes();
		int id = p.readCInt();
		System.out.println("Packet[ID: " + id + ", Size:" + size + "]");
		size = p.remainingBytes();
		
		if(id == 33)
		{
			System.out.println("==> KEEP ALIVE!");
			byte[] ping = p.readBytes(8);
			
			MessageBuilder mb = new MessageBuilder();
			mb.addBytes(ping);
			mb.prepandCInt(14);
			mb.compress(c);
			mb.prepandSize();
			encrypt(mb);
			mb.write(os);
		}
		else if(id == 14)
		{
			System.out.println("==> CHAT:");
			String jsonMessage = p.readString();
			System.out.println("> " + jsonMessage);
			System.out.println("> " + p.readByte());
			
			if(jsonMessage.contains(Credentials.USERNAME) && !jsonMessage.contains("joined the game") && !jsonMessage.contains("Discord"))
			{
				MessageBuilder mb = new MessageBuilder();
				mb.addString("Yes? (Automated message)");
				mb.prepandCInt(2);
				mb.compress(c);
				mb.prepandSize();
				encrypt(mb);
				mb.write(os);
			}
		}
	}
}
