package de.ecconia.mc.jclient.tools.math;

public class McMathHelper
{
	public static int toChunkPos(int position)
	{
		if(position < 0)
		{
			position -= 15;
		}
		
		return position / 16;
	}
	
	//Return smaller side of chunk.
	public static int toStartPos(int chunk)
	{
		return chunk * 16;
	}
	
	public static int toBlockPos(double pos)
	{
		return (int) Math.floor(pos);
	}
	
	public static void main(String[] args)
	{
		System.out.println(toChunkPos(-32));
		System.out.println(toChunkPos(-17));
		System.out.println(toChunkPos(-16));
		System.out.println(toChunkPos(-1));
		System.out.println(toChunkPos(0));
		System.out.println(toChunkPos(15));
		System.out.println(toChunkPos(16));
		System.out.println(toChunkPos(31));
		System.out.println();
		System.out.println(toStartPos(-3));
		System.out.println(toStartPos(-2));
		System.out.println(toStartPos(-1));
		System.out.println(toStartPos(0));
		System.out.println(toStartPos(1));
		System.out.println(toStartPos(2));
		System.out.println(toStartPos(3));
		System.out.println();
		
		System.out.println("-166: " + toStartPos(-166));
		System.out.println("-165: " + toStartPos(-165));
		System.out.println("164: " + toStartPos(164));
		System.out.println("165: " + toStartPos(165));
		
	}
}
