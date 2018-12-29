package de.ecconia.mc.jclient.gui.gl;

import com.jogamp.opengl.GL2;

public class Helper3D
{
	private final static double blockRadius = 0.4999;
	private final static double blockOutterRadius = 0.45;
	private final static double blockInnerRadius = 0.5;
	
	public static void drawBlock(GL2 gl, int x, int y, int z)
	{
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		
		gl.glBegin(GL2.GL_QUADS);
		
		//Top:
		gl.glVertex3d(blockRadius, blockRadius, blockRadius);
		gl.glVertex3d(blockRadius, blockRadius, -blockRadius);
		gl.glVertex3d(-blockRadius, blockRadius, -blockRadius);
		gl.glVertex3d(-blockRadius, blockRadius, blockRadius);
		
		//Bottom:
		gl.glVertex3d(blockRadius, -blockRadius, blockRadius);
		gl.glVertex3d(blockRadius, -blockRadius, -blockRadius);
		gl.glVertex3d(-blockRadius, -blockRadius, -blockRadius);
		gl.glVertex3d(-blockRadius, -blockRadius, blockRadius);
		
		//Left:
		gl.glVertex3d(-blockRadius, -blockRadius, blockRadius);
		gl.glVertex3d(-blockRadius, -blockRadius, -blockRadius);
		gl.glVertex3d(-blockRadius, blockRadius, -blockRadius);
		gl.glVertex3d(-blockRadius, blockRadius, blockRadius);
		
		//Right:
		gl.glVertex3d(blockRadius, -blockRadius, blockRadius);
		gl.glVertex3d(blockRadius, -blockRadius, -blockRadius);
		gl.glVertex3d(blockRadius, blockRadius, -blockRadius);
		gl.glVertex3d(blockRadius, blockRadius, blockRadius);
		
		//Back:
		gl.glVertex3d(-blockRadius, -blockRadius, -blockRadius);
		gl.glVertex3d(blockRadius, -blockRadius, -blockRadius);
		gl.glVertex3d(blockRadius, blockRadius, -blockRadius);
		gl.glVertex3d(-blockRadius, blockRadius, -blockRadius);
		
		//Front:
		gl.glVertex3d(-blockRadius, -blockRadius, blockRadius);
		gl.glVertex3d(blockRadius, -blockRadius, blockRadius);
		gl.glVertex3d(blockRadius, blockRadius, blockRadius);
		gl.glVertex3d(-blockRadius, blockRadius, blockRadius);
		
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		
		//TOP:
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockInnerRadius, blockInnerRadius);
		
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, blockInnerRadius, blockInnerRadius);
		
		gl.glVertex3d(-blockOutterRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(-blockOutterRadius, blockInnerRadius, blockOutterRadius);
		
		gl.glVertex3d(-blockOutterRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(-blockOutterRadius, blockInnerRadius, -blockOutterRadius);
		
		//Bottom:
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockInnerRadius, blockInnerRadius);
		
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, -blockInnerRadius, blockInnerRadius);
		
		gl.glVertex3d(-blockOutterRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(-blockOutterRadius, -blockInnerRadius, blockOutterRadius);
		
		gl.glVertex3d(-blockOutterRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(-blockOutterRadius, -blockInnerRadius, -blockOutterRadius);
		
		//Right:
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, blockOutterRadius);
		
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, -blockOutterRadius);
		
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, blockOutterRadius, blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, blockOutterRadius, -blockOutterRadius);
		
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, -blockOutterRadius, blockOutterRadius);
		gl.glVertex3d(blockInnerRadius, -blockOutterRadius, -blockOutterRadius);
		
		//Left:
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, blockOutterRadius);
		
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, -blockOutterRadius);
		
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, blockOutterRadius, blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, blockOutterRadius, -blockOutterRadius);
		
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, -blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, -blockOutterRadius, blockOutterRadius);
		gl.glVertex3d(-blockInnerRadius, -blockOutterRadius, -blockOutterRadius);
		
		//Front:
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, blockOutterRadius, blockInnerRadius);
		
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, -blockOutterRadius, blockInnerRadius);
		
		gl.glVertex3d(blockInnerRadius, -blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockOutterRadius, blockInnerRadius);
		
		gl.glVertex3d(-blockInnerRadius, -blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, blockOutterRadius, blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, -blockOutterRadius, blockInnerRadius);
		
		//Back:
		gl.glVertex3d(blockInnerRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, blockOutterRadius, -blockInnerRadius);
		
		gl.glVertex3d(blockInnerRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockInnerRadius, -blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, -blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, -blockOutterRadius, -blockInnerRadius);
		
		gl.glVertex3d(blockInnerRadius, -blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(blockInnerRadius, blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(blockOutterRadius, -blockOutterRadius, -blockInnerRadius);
		
		gl.glVertex3d(-blockInnerRadius, -blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(-blockInnerRadius, blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, blockOutterRadius, -blockInnerRadius);
		gl.glVertex3d(-blockOutterRadius, -blockOutterRadius, -blockInnerRadius);
		
		gl.glEnd();
		
		gl.glPopMatrix();
	}
}
