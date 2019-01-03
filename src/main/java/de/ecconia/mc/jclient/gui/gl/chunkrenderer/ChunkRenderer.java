package de.ecconia.mc.jclient.gui.gl.chunkrenderer;

import com.jogamp.opengl.GL2;

import de.ecconia.mc.jclient.tools.McMathHelper;

public abstract class ChunkRenderer
{
	protected final int posX;
	protected final int posZ;
	
	protected int offsetX = 0;
	protected int offsetZ = 0;
	
	public ChunkRenderer(int x, int z)
	{
		posX = x;
		posZ = z;
		
		offsetX = McMathHelper.toStartPos(x);
		offsetZ = McMathHelper.toStartPos(z);
	}
	
	public abstract void render(GL2 gl);
}
