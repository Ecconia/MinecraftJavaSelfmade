package de.ecconia.mc.jclient.gui.gl.models;

import java.util.Random;

import com.jogamp.opengl.GL2;

import de.ecconia.mc.jclient.gui.gl.Helper3D;

public interface BlockModel
{
	public void draw(GL2 gl, int x, int y, int z);
	
	public static class Color implements BlockModel
	{
		private static final Random r = new Random();
		private static final float[] c = {0f, 0.2f, 0.8f, 1f};
		
		private final float red;
		private final float green;
		private final float blue;
		
		public Color()
		{
			red = c[r.nextInt(4)];
			green = c[r.nextInt(4)];
			blue = c[r.nextInt(4)];
		}
		
		@Override
		public void draw(GL2 gl, int x, int y, int z)
		{
			gl.glColor3f(red, green, blue);
			Helper3D.drawBlock(gl, x, y, z);
		}
	}
}
