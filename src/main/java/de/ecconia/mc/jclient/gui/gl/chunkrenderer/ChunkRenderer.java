package de.ecconia.mc.jclient.gui.gl.chunkrenderer;

import com.jogamp.opengl.GL3;

import de.ecconia.mc.jclient.gui.gl.helper.Deleteable;
import de.ecconia.mc.jclient.tools.math.McMathHelper;

public abstract class ChunkRenderer implements Deleteable
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
	
	public int getPosX()
	{
		return posX;
	}
	
	public int getPosZ()
	{
		return posZ;
	}
	
	public int getOffsetX()
	{
		return offsetX;
	}
	
	public int getOffsetZ()
	{
		return offsetZ;
	}
	
	public abstract void load(GL3 gl);
	
	public abstract void draw(GL3 gl);
}
