package de.ecconia.mc.jclient.network.tools.encryption;

@SuppressWarnings("serial")
public class CipherException extends RuntimeException
{
	public CipherException(String message, Throwable t)
	{
		super(message, t);
	}
}
