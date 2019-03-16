package de.ecconia.mc.jclient;

import java.util.ArrayList;
import java.util.List;

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
	
	public static synchronized void ex(String when, Throwable t)
	{
		StackTraceElement catchOrigin = Thread.currentThread().getStackTrace()[2];
		String catcher = " @(" + catchOrigin.getClassName() + ":" + catchOrigin.getLineNumber() + ")";
		System.out.println("Exception " + when + ": " + (t.getMessage() != null ? t.getMessage() : "") + catcher);
		
		List<Throwable> issues = new ArrayList<>();
		
		issues.add(t);
		while(t.getCause() != null)
		{
			issues.add(t);
			t = t.getCause();
		}
		
		for(int i = issues.size() - 1; i >= 0; i--)
		{
			t = issues.get(i);
			
			System.out.println(" -" + t.getClass().getSimpleName() + ": " + (t.getMessage() != null ? t.getMessage() + " " : ""));
			for(StackTraceElement el : t.getStackTrace())
			{
				System.out.println("-> " + el.getClassName() + "." + el.getMethodName() + "(" + el.getFileName() + ":" + el.getLineNumber() + ")");
			}
		}
	}
}
