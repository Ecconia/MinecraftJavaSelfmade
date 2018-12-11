package de.ecconia.mc.jclient.encryption.x;

@SuppressWarnings("serial")
public class CipherException extends RuntimeException
{
	public CipherException(String message, Throwable t)
	{
		super(message, t);
	}
}
