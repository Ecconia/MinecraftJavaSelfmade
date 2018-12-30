package de.ecconia.mc.jclient;

public class Logger
{
	/**
	 * Called when the server sent packets at the wrong time.
	 */
	public static void perr(String message)
	{
		System.out.println("PROTOCOL-ERROR: Remote server is dummy!");
		System.out.println("-> " + message);
	}
	
	/**
	 * Called when something happens, which is an issue but no mistake.
	 */
	public static void warn(String message)
	{
		System.out.println("WARNING: " + message);
	}
}
