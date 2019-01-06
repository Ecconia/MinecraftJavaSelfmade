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
	
	public static void important(String message)
	{
		System.out.println("#######################################");
		System.out.println("IMPORTANT: " + message);
		System.out.println("#######################################");
	}
	
	public static void ex(String when, Throwable e)
	{
		StackTraceElement root = Thread.currentThread().getStackTrace()[2];
		System.out.println("EXCEPTION " + when + ": " + e.getClass().getSimpleName() + " @(" + root.getClassName() + ":" + root.getLineNumber() + ")");
		if(e.getMessage() != null)
		{
			System.out.println("- Message: " + e.getMessage());
		}
		for(StackTraceElement el : e.getStackTrace())
		{
			System.out.println(" -> " + el.getClassName() + "." + el.getMethodName() + "(" + el.getFileName() + ":" + el.getLineNumber() + ")");
		}
	}
}
