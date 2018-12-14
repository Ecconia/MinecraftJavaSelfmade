package de.ecconia.mc.jclient.tools;

public class PrintUtils
{
	public static void printBytes(byte[] bytes)
	{
		for(int i = 0; i < bytes.length; i++)
		{
			int b = bytes[i];
			int lower = b & 15;
			int upper = (b >> 4) & 15;
			System.out.print(" " + makeNibble(upper) + makeNibble(lower));
		}
		System.out.println();
	}
	
	private static char makeNibble(int i)
	{
		String letters = "0123456789ABCDEF";
		return letters.charAt(i);
	}
}
