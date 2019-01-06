package de.ecconia.mc.jclient.data.world;

import java.util.ArrayList;
import java.util.List;

public class DebugPalette
{
	private final int[] palette;
	
	public DebugPalette(int[] paletteInts)
	{
		palette = paletteInts;
	}
	
	private int highest = 0;
	private boolean[] types;
	
	public void test(int[][][] blocks, int yOffset)
	{
		for(int a = 0; a < 16; a++)
		{
			for(int b = 0; b < 16; b++)
			{
				for(int c = 0; c < 16; c++)
				{
					int t = blocks[a][b][c + yOffset];
					if(t > highest)
					{
						highest = t;
					}
				}
			}
		}
		
		types = new boolean[highest + 1];
		
		for(int a = 0; a < 16; a++)
		{
			for(int b = 0; b < 16; b++)
			{
				for(int c = 0; c < 16; c++)
				{
					types[blocks[a][b][c + yOffset]] = true;
				}
			}
		}
		
		List<Integer> issues = new ArrayList<>();
		boolean dirty = false;
		//Skip 0 cause AIR and always present.
		for(int i = 0; i < types.length; i++)
		{
			if(!types[i])
			{
				dirty = true;
				issues.add(i);
			}
		}
		
		if(palette.length != types.length)
		{
			dirty = true;
		}
		
		if(palette[0] != 0)
		{
			dirty = true;
		}
		
		if(dirty)
		{
			System.out.println(">>>> Subchunk has issues!");
			System.out.println("Types: " + types.length + "/" + palette.length);
			for(int i : issues)
			{
				System.out.println("Skipped: " + i + " -> " + palette[i]);
			}
			if(palette[0] != 0)
			{
				System.out.println("First entry in palette is not AIR, but: " + palette[0]);
			}
//			for(int i = 1; i < types.length; i++)
//			{
//				System.out.println("\t" + i + " -> " + palette[i]);
//			}
		}
//		else
//		{
//			System.out.println(">>>> No issue.");
//		}
	}
}
