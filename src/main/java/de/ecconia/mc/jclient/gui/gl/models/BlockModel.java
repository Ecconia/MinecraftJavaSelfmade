package de.ecconia.mc.jclient.gui.gl.models;

import java.util.Random;

public interface BlockModel
{
	public float[] getColor();
	
	public static class Color implements BlockModel
	{
		private static final Random r = new Random();
		private static final float[] c = {0f, 0.2f, 0.8f, 1f};
		
		private final float[] color = new float[3];
		
		public Color()
		{
			color[0] = c[r.nextInt(4)];
			color[1] = c[r.nextInt(4)];
			color[2] = c[r.nextInt(4)];
		}
		
		@Override
		public float[] getColor()
		{
			return color;
		}
	}
}
