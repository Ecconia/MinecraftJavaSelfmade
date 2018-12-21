package de.ecconia.mc.jclient.network.connector.inner;

import de.ecconia.mc.jclient.network.tools.encryption.SyncCryptUnit;

public class DecryptionReader implements Reader
{
	private final Reader r;
	private final SyncCryptUnit cripter;
	
	/**
	 * Wraps a Reader and decrypts all requests.
	 * @param r - the Reader which will be decrypted
	 */
	public DecryptionReader(Reader r, SyncCryptUnit cripter)
	{
		this.r = r;
		this.cripter = cripter;
	}
	
	@Override
	public byte readByte()
	{
		return cripter.decryptByte(r.readByte());
	}
	
	@Override
	public byte[] readBytes(int amount)
	{
		return cripter.decryptBytes(r.readBytes(amount));
	}
	
	@Override
	public long getBytesRead()
	{
		return r.getBytesRead();
	}
}
