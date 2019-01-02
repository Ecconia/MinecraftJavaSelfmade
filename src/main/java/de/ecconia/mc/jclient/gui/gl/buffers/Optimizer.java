package de.ecconia.mc.jclient.gui.gl.buffers;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import de.ecconia.mc.jclient.gui.gl.models.BlockLib;

public class Optimizer
{
	public List<Face> xpFaces = new ArrayList<>();
	public List<Face> ypFaces = new ArrayList<>();
	public List<Face> zpFaces = new ArrayList<>();
	
	public List<Face> xmFaces = new ArrayList<>();
	public List<Face> ymFaces = new ArrayList<>();
	public List<Face> zmFaces = new ArrayList<>();
	
	public Optimizer(int[][][] blocks)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				int block = blocks[x][z][0];
				
				if(block != 0)
				{
					ymFaces.add(new Face(block, x, 0, z));
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
								ypFaces.add(new Face(block2, x, y, z));
							}
						}
						
						if(y != 255)
						{
							int block2 = blocks[x][z][y + 1];
							if(block2 != 0)
							{
								ymFaces.add(new Face(block2, x, y + 1, z));
							}
						}
						
						if(z != 15)
						{
							int block2 = blocks[x][z + 1][y];
							if(block2 != 0)
							{
								zmFaces.add(new Face(block2, x, y, z + 1));
							}
						}
						
						if(z != 0)
						{
							int block2 = blocks[x][z - 1][y];
							if(block2 != 0)
							{
								zpFaces.add(new Face(block2, x, y, z));
							}
						}
						
						if(x != 15)
						{
							int block2 = blocks[x + 1][z][y];
							if(block2 != 0)
							{
								xmFaces.add(new Face(block2, x + 1, y, z));
							}
						}
						
						if(x != 0)
						{
							int block2 = blocks[x - 1][z][y];
							if(block2 != 0)
							{
								xpFaces.add(new Face(block2, x, y, z));
							}
						}
					}
				}
			}
		}
	}
	
	public void draw(GL2 gl, BlockLib lib)
	{
		for(Face f : ymFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x, f.y - 0.5f, f.z);
			
			drawFaceYM(gl);
			
			gl.glPopMatrix();
		}
		
		for(Face f : xmFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x - 0.5f, f.y, f.z);
			
			drawFaceXM(gl);
			
			gl.glPopMatrix();
		}
		
		for(Face f : zmFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x, f.y, f.z - 0.5f);
			
			drawFaceZM(gl);
			
			gl.glPopMatrix();
		}
		
		for(Face f : ypFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x, f.y - 0.5f, f.z);
			
			drawFaceYP(gl);
			
			gl.glPopMatrix();
		}
		
		for(Face f : xpFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x - 0.5f, f.y, f.z);
			
			drawFaceXP(gl);
			
			gl.glPopMatrix();
		}
		
		for(Face f : zpFaces)
		{
			gl.glPushMatrix();
			
			lib.get(f.type).draw(gl, 0, 0, 0);
			gl.glTranslated(f.x, f.y, f.z - 0.5f);
			
			drawFaceZP(gl);
			
			gl.glPopMatrix();
		}
	}
	
	private final static double blockRadius = 0.5;
	private final static double gridOffset = 0.01;
	
	private void drawFaceYM(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(blockRadius, 0, blockRadius);
		gl.glVertex3d(blockRadius, 0, -blockRadius);
		gl.glVertex3d(-blockRadius, 0, -blockRadius);
		gl.glVertex3d(-blockRadius, 0, blockRadius);
		gl.glEnd();
		
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(blockRadius, -gridOffset, blockRadius);
		gl.glVertex3d(-blockRadius, -gridOffset, -blockRadius);
		gl.glVertex3d(-blockRadius, -gridOffset, blockRadius);
		gl.glVertex3d(blockRadius, -gridOffset, -blockRadius);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(blockRadius, -gridOffset, blockRadius);
		gl.glVertex3d(blockRadius, -gridOffset, -blockRadius);
		gl.glVertex3d(-blockRadius, -gridOffset, -blockRadius);
		gl.glVertex3d(-blockRadius, -gridOffset, blockRadius);
		gl.glEnd();
	}
	
	private void drawFaceXM(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(0, blockRadius, blockRadius);
		gl.glVertex3d(0, blockRadius, -blockRadius);
		gl.glVertex3d(0, -blockRadius, -blockRadius);
		gl.glVertex3d(0, -blockRadius, blockRadius);
		gl.glEnd();
		
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(-gridOffset, blockRadius, blockRadius);
		gl.glVertex3d(-gridOffset, -blockRadius, -blockRadius);
		gl.glVertex3d(-gridOffset, -blockRadius, blockRadius);
		gl.glVertex3d(-gridOffset, blockRadius, -blockRadius);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(-gridOffset, blockRadius, blockRadius);
		gl.glVertex3d(-gridOffset, blockRadius, -blockRadius);
		gl.glVertex3d(-gridOffset, -blockRadius, -blockRadius);
		gl.glVertex3d(-gridOffset, -blockRadius, blockRadius);
		gl.glEnd();
	}
	
	private void drawFaceZM(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(blockRadius, blockRadius, 0);
		gl.glVertex3d(blockRadius, -blockRadius, 0);
		gl.glVertex3d(-blockRadius, -blockRadius, 0);
		gl.glVertex3d(-blockRadius, blockRadius, 0);
		gl.glEnd();
		
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(blockRadius, blockRadius, -gridOffset);
		gl.glVertex3d(-blockRadius, -blockRadius, -gridOffset);
		gl.glVertex3d(-blockRadius, blockRadius, -gridOffset);
		gl.glVertex3d(blockRadius, -blockRadius, -gridOffset);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(blockRadius, blockRadius, -gridOffset);
		gl.glVertex3d(blockRadius, -blockRadius, -gridOffset);
		gl.glVertex3d(-blockRadius, -blockRadius, -gridOffset);
		gl.glVertex3d(-blockRadius, blockRadius, -gridOffset);
		gl.glEnd();
	}
	
	private void drawFaceYP(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(blockRadius, 0, blockRadius);
		gl.glVertex3d(blockRadius, 0, -blockRadius);
		gl.glVertex3d(-blockRadius, 0, -blockRadius);
		gl.glVertex3d(-blockRadius, 0, blockRadius);
		gl.glEnd();
		
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(blockRadius, gridOffset, blockRadius);
		gl.glVertex3d(-blockRadius, gridOffset, -blockRadius);
		gl.glVertex3d(-blockRadius, gridOffset, blockRadius);
		gl.glVertex3d(blockRadius, gridOffset, -blockRadius);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(blockRadius, gridOffset, blockRadius);
		gl.glVertex3d(blockRadius, gridOffset, -blockRadius);
		gl.glVertex3d(-blockRadius, gridOffset, -blockRadius);
		gl.glVertex3d(-blockRadius, gridOffset, blockRadius);
		gl.glEnd();
	}
	
	private void drawFaceXP(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(0, blockRadius, blockRadius);
		gl.glVertex3d(0, blockRadius, -blockRadius);
		gl.glVertex3d(0, -blockRadius, -blockRadius);
		gl.glVertex3d(0, -blockRadius, blockRadius);
		gl.glEnd();
		
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(gridOffset, blockRadius, blockRadius);
		gl.glVertex3d(gridOffset, -blockRadius, -blockRadius);
		gl.glVertex3d(gridOffset, -blockRadius, blockRadius);
		gl.glVertex3d(gridOffset, blockRadius, -blockRadius);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(gridOffset, blockRadius, blockRadius);
		gl.glVertex3d(gridOffset, blockRadius, -blockRadius);
		gl.glVertex3d(gridOffset, -blockRadius, -blockRadius);
		gl.glVertex3d(gridOffset, -blockRadius, blockRadius);
		gl.glEnd();
	}
	
	private void drawFaceZP(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(blockRadius, blockRadius, 0);
		gl.glVertex3d(blockRadius, -blockRadius, 0);
		gl.glVertex3d(-blockRadius, -blockRadius, 0);
		gl.glVertex3d(-blockRadius, blockRadius, 0);
		gl.glEnd();
		
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(blockRadius, blockRadius, gridOffset);
		gl.glVertex3d(-blockRadius, -blockRadius, gridOffset);
		gl.glVertex3d(-blockRadius, blockRadius, gridOffset);
		gl.glVertex3d(blockRadius, -blockRadius, gridOffset);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(blockRadius, blockRadius, gridOffset);
		gl.glVertex3d(blockRadius, -blockRadius, gridOffset);
		gl.glVertex3d(-blockRadius, -blockRadius, gridOffset);
		gl.glVertex3d(-blockRadius, blockRadius, gridOffset);
		gl.glEnd();
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
