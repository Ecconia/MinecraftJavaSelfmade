package old.reading.helper;

import de.ecconia.mc.jclient.encryption.SyncCryptUnit;

public class EncrpytionProvider extends Provider
{
	private final SyncCryptUnit crypter;
	private final Provider p;
	
	public EncrpytionProvider(SyncCryptUnit crypter, Provider p)
	{
		this.p = p;
		this.crypter = crypter;
	}
	
	public byte decryptByte(byte b)
	{
		return crypter.decryptByte(b);
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
