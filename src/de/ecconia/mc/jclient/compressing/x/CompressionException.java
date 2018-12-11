package de.ecconia.mc.jclient.compressing.x;

@SuppressWarnings("serial")
public class CompressionException extends RuntimeException
{
	public CompressionException(String message, Throwable t)
	{
		super(message, t);
	}
}
