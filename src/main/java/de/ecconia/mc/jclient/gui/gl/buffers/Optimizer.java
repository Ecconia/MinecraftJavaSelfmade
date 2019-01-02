package de.ecconia.mc.jclient.gui.gl.buffers;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import de.ecconia.mc.jclient.gui.gl.models.BlockLib;

public class Optimizer
{
	public List<Face> xFaces = new ArrayList<>();
	public List<Face> yFaces = new ArrayList<>();
	public List<Face> zFaces = new ArrayList<>();
	
	public Optimizer(int[][][] blocks)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				int block = blocks[x][z][0];
				
				if(block != 0)
				{
					yFaces.add(new Face(block, x, 0, z));
				}
			}
		}
		
		for(int y = 0; y < 256; y++)
		{
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					int block = blocks[x][z][y];
					
					if(block == 0)
					{
						if(y != 0)
						{
							int block2 = blocks[x][z][y - 1];
							if(block2 != 0)
							{
								yFaces.add(new Face(block2, x, y, z));
							}
						}
						
						if(y != 255)
						{
							int block2 = blocks[x][z][y + 1];
							if(block2 != 0)
							{
								yFaces.add(new Face(block2, x, y + 1, z));
							}
						}
						
						if(z != 15)
						{
							int block2 = blocks[x][z + 1][y];
							if(block2 != 0)
							{
								zFaces.add(new Face(block2, x, y, z + 1));
							}
						}
						
						if(z != 0)
						{
							int block2 = blocks[x][z - 1][y];
							if(block2 != 0)
							{
								zFaces.add(new Face(block2, x, y, z));
							}
						}
						
						if(x != 15)
						{
							int block2 = blocks[x + 1][z][y];
							if(block2 != 0)
							{
								xFaces.add(new Face(block2, x + 1, y, z));
							}
						}
						
						if(x != 0)
						{
							int block2 = blocks[x - 1][z][y];
							if(block2 != 0)
							{
								xFaces.add(new Face(block2, x, y, z));
							}
						}
					}
				}
			}
		}
	}
	
	public void draw(GL2 gl, BlockLib lib)
	{
		for(Face f : yFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x, f.y - 0.5f, f.z);
			
			gl.glBegin(GL2.GL_QUADS);
			drawFaceY(gl);
			gl.glEnd();
			
			gl.glPopMatrix();
		}
		
		for(Face f : xFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x - 0.5f, f.y, f.z);
			
			gl.glBegin(GL2.GL_QUADS);
			drawFaceX(gl);
			gl.glEnd();
			
			gl.glPopMatrix();
		}
		
		for(Face f : zFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x, f.y, f.z - 0.5f);
			
			gl.glBegin(GL2.GL_QUADS);
			drawFaceZ(gl);
			gl.glEnd();
			
			gl.glPopMatrix();
		}
	}
	
	private final static double blockRadius = 0.5;
	
	private void drawFaceY(GL2 gl)
	{
		gl.glVertex3d(blockRadius, 0, blockRadius);
		gl.glVertex3d(blockRadius, 0, -blockRadius);
		gl.glVertex3d(-blockRadius, 0, -blockRadius);
		gl.glVertex3d(-blockRadius, 0, blockRadius);
	}
	
	private void drawFaceX(GL2 gl)
	{
		gl.glVertex3d(0, blockRadius, blockRadius);
		gl.glVertex3d(0, blockRadius, -blockRadius);
		gl.glVertex3d(0, -blockRadius, -blockRadius);
		gl.glVertex3d(0, -blockRadius, blockRadius);
	}
	
	private void drawFaceZ(GL2 gl)
	{
		gl.glVertex3d(blockRadius, blockRadius, 0);
		gl.glVertex3d(blockRadius, -blockRadius, 0);
		gl.glVertex3d(-blockRadius, -blockRadius, 0);
		gl.glVertex3d(-blockRadius, blockRadius, 0);
	}
	
	private static class Face
	{
		public final int x;
		public final int y;
		public final int z;
		public final int type;
		
		public Face(int type, int x, int y, int z)
		{
			this.type = type;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
