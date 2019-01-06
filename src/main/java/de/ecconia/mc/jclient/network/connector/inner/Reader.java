package de.ecconia.mc.jclient.network.connector.inner;

public interface Reader
{
	/**
	 * Reads a single byte from the InputStream.
	 * Depending on the Implementation it is decrypted already.
	 * @return byte - read from InputStream
	 */
	public byte readByte();
	
	/**
	 * Reads <code>amount</code> bytes from the InputStream.
	 * Depending on the Implementation they are decrypted already.
	 * @param amount - amount of bytes to read from InputStream
	 * @return bytes - read from InputStream
	 */
	public byte[] readBytes(int amount);
	
	/**
	 * Debug method to get the total amount of bytes read from the InputStream.
	 * @return long - bytes read
	 */
	public long getBytesRead();
}
