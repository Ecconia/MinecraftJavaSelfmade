package reading.helper;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class EncrpytionProvider extends Provider
{
	private final Cipher decryptCipher;
	private final Provider p;
	
	public EncrpytionProvider(SecretKey key, Provider p) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
	{
		this.p = p;
		
		decryptCipher = Cipher.getInstance("AES/CFB8/NoPadding");
		decryptCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));
	}
	
	public byte decryptByte(byte b)
	{
		byte i[] = {b};
		byte o[] = new byte[1];
		
		try
		{
			decryptCipher.update(i, 0, 1, o, 0);
			
			return o[0];
		}
		catch(ShortBufferException e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public int getByte()
	{
		return decryptByte((byte) p.getByte());
	}
	
	@Override
	public int remainingBytes()
	{
		return p.remainingBytes();
	}
	
	@Override
	public void reset()
	{
		throw new UnsupportedOperationException("Cannot reset an InputStream.");
	}
}
