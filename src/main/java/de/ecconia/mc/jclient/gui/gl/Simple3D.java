package de.ecconia.mc.jclient.gui.gl;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.network.processor.WorldPacketProcessor;

@SuppressWarnings("serial")
public class Simple3D extends JPanel implements GLEventListener
{
	//????????????
	private GLU glu;
	private Random r = new Random();
	
	int[][][] blocks = new int[16][16][256];
	float[][] colors;
	
	float rotation = 30;
	float lifting = -25;
	
	public Simple3D(WorldPacketProcessor worldPacketProcessor, PrimitiveDataDude dataDude)
	{
		dataDude.setChunkPosHandler((x, z) -> {
			//TODO: Threadsafe!
			System.out.println("On thread: " + Thread.currentThread().getName());
			blocks = worldPacketProcessor.getProcessedChunk(x, z);
			
			List<Integer> blocktypes = new ArrayList<>();
			for(int iy = 0; iy < 256; iy++)
			{
				for(int ix = 0; ix < 16; ix++)
				{
					for(int iz = 0; iz < 16; iz++)
					{
						//TODO: Optimize access!
						int block = blocks[ix][iz][iy];
						if(block != 0)
						{
							blocktypes.add(block);
						}
					}
				}
			}
			
			final float[] c = {0f, 0.2f, 0.8f, 1f};
			
			colors = new float[blocktypes.size()][3];
			for(int i = 0; i < blocktypes.size(); i++)
			{
				int red = r.nextInt(4);
				int green = r.nextInt(4);
				int blue = r.nextInt(4);
				
				colors[i][0] = c[red];
				colors[i][1] = c[green];
				colors[i][2] = c[blue];
			}
		});
		
		//getting the capabilities object of GL2 profile        
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		
		// The canvas
		final GLCanvas glcanvas = new GLCanvas(capabilities);
		glcanvas.addGLEventListener(this);
		glcanvas.setSize(400, 400);
		setLayout(new BorderLayout());
		add(glcanvas);
		
		addMouseWheelListener(e -> {
			int wheelRotation = e.getWheelRotation();
			
			if(e.isShiftDown())
			{
				lifting += wheelRotation * 6;
			}
			else
			{
				rotation += wheelRotation;
			}
		});
		
		final FPSAnimator animator = new FPSAnimator(glcanvas, 30, true);
		animator.start();
	}
	
	@Override
	public void init(GLAutoDrawable drawable)
	{
		final GL2 gl = drawable.getGL().getGL2();
		
		glu = new GLU();
		
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.973f, 0.973f, 0.973f, 1.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable)
	{
	}
	
	@Override
	public void display(GLAutoDrawable drawable)
	{
//		L.writeLineOnChannel("spam!", "############################################################");
//		System.out.println("Render...");
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(-8, lifting, -50);
		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		
		for(int y = 0; y < 256; y++)
		{
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					//TODO: Optimize access!
					int block = blocks[x][z][y];
					if(block != 0)
					{
//						setRandomColor(gl);
						gl.glColor3f(colors[block][0], colors[block][1], colors[block][2]);
						drawBlock(gl, x, y, z - 8);
					}
//					L.writeLineOnChannel("spam!", "XYZ: " + x + ", " + y + ", " + z + " -> " + block);
				}
			}
		}
		
		gl.glFlush();
	}
	
	private void drawBlock(GL2 gl, int x, int y, int z)
	{
		gl.glPushMatrix();
		
		gl.glTranslatef(x, y, z);
		gl.glBegin(GL2.GL_QUADS);
		
		double r = 0.4999;
		
		//Top:
		gl.glVertex3d(r, r, r);
		gl.glVertex3d(r, r, -r);
		gl.glVertex3d(-r, r, -r);
		gl.glVertex3d(-r, r, r);
		
		//Bottom:
		gl.glVertex3d(r, -r, r);
		gl.glVertex3d(r, -r, -r);
		gl.glVertex3d(-r, -r, -r);
		gl.glVertex3d(-r, -r, r);
		
		//Left:
		gl.glVertex3d(-r, -r, r);
		gl.glVertex3d(-r, -r, -r);
		gl.glVertex3d(-r, r, -r);
		gl.glVertex3d(-r, r, r);
		
		//Right:
		gl.glVertex3d(r, -r, r);
		gl.glVertex3d(r, -r, -r);
		gl.glVertex3d(r, r, -r);
		gl.glVertex3d(r, r, r);
		
		//Back:
		gl.glVertex3d(-r, -r, -r);
		gl.glVertex3d(r, -r, -r);
		gl.glVertex3d(r, r, -r);
		gl.glVertex3d(-r, r, -r);
		
		//Front:
		gl.glVertex3d(-r, -r, r);
		gl.glVertex3d(r, -r, r);
		gl.glVertex3d(r, r, r);
		gl.glVertex3d(-r, r, r);
		
		gl.glEnd();
		
		double i = 0.45;
		r = 0.5;
		
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glBegin(GL2.GL_QUADS);
		
		//TOP:
		gl.glVertex3d(r, r, r);
		gl.glVertex3d(r, r, -r);
		gl.glVertex3d(i, r, -r);
		gl.glVertex3d(i, r, r);
		
		gl.glVertex3d(-r, r, r);
		gl.glVertex3d(-r, r, -r);
		gl.glVertex3d(-i, r, -r);
		gl.glVertex3d(-i, r, r);
		
		gl.glVertex3d(-i, r, r);
		gl.glVertex3d(i, r, r);
		gl.glVertex3d(i, r, i);
		gl.glVertex3d(-i, r, i);
		
		gl.glVertex3d(-i, r, -r);
		gl.glVertex3d(i, r, -r);
		gl.glVertex3d(i, r, -i);
		gl.glVertex3d(-i, r, -i);
		
		//Bottom:
		gl.glVertex3d(r, -r, r);
		gl.glVertex3d(r, -r, -r);
		gl.glVertex3d(i, -r, -r);
		gl.glVertex3d(i, -r, r);
		
		gl.glVertex3d(-r, -r, r);
		gl.glVertex3d(-r, -r, -r);
		gl.glVertex3d(-i, -r, -r);
		gl.glVertex3d(-i, -r, r);
		
		gl.glVertex3d(-i, -r, r);
		gl.glVertex3d(i, -r, r);
		gl.glVertex3d(i, -r, i);
		gl.glVertex3d(-i, -r, i);
		
		gl.glVertex3d(-i, -r, -r);
		gl.glVertex3d(i, -r, -r);
		gl.glVertex3d(i, -r, -i);
		gl.glVertex3d(-i, -r, -i);
		
		//Right:
		gl.glVertex3d(r, r, r);
		gl.glVertex3d(r, -r, r);
		gl.glVertex3d(r, -r, i);
		gl.glVertex3d(r, r, i);
		
		gl.glVertex3d(r, r, -r);
		gl.glVertex3d(r, -r, -r);
		gl.glVertex3d(r, -r, -i);
		gl.glVertex3d(r, r, -i);
		
		gl.glVertex3d(r, r, -i);
		gl.glVertex3d(r, r, i);
		gl.glVertex3d(r, i, i);
		gl.glVertex3d(r, i, -i);
		
		gl.glVertex3d(r, -r, -i);
		gl.glVertex3d(r, -r, i);
		gl.glVertex3d(r, -i, i);
		gl.glVertex3d(r, -i, -i);
		
		//Left:
		gl.glVertex3d(-r, r, r);
		gl.glVertex3d(-r, -r, r);
		gl.glVertex3d(-r, -r, i);
		gl.glVertex3d(-r, r, i);
		
		gl.glVertex3d(-r, r, -r);
		gl.glVertex3d(-r, -r, -r);
		gl.glVertex3d(-r, -r, -i);
		gl.glVertex3d(-r, r, -i);
		
		gl.glVertex3d(-r, r, -i);
		gl.glVertex3d(-r, r, i);
		gl.glVertex3d(-r, i, i);
		gl.glVertex3d(-r, i, -i);
		
		gl.glVertex3d(-r, -r, -i);
		gl.glVertex3d(-r, -r, i);
		gl.glVertex3d(-r, -i, i);
		gl.glVertex3d(-r, -i, -i);
		
		//Front:
		gl.glVertex3d(r, r, r);
		gl.glVertex3d(-r, r, r);
		gl.glVertex3d(-r, i, r);
		gl.glVertex3d(r, i, r);
		
		gl.glVertex3d(r, -r, r);
		gl.glVertex3d(-r, -r, r);
		gl.glVertex3d(-r, -i, r);
		gl.glVertex3d(r, -i, r);
		
		gl.glVertex3d(r, -i, r);
		gl.glVertex3d(r, i, r);
		gl.glVertex3d(i, i, r);
		gl.glVertex3d(i, -i, r);
		
		gl.glVertex3d(-r, -i, r);
		gl.glVertex3d(-r, i, r);
		gl.glVertex3d(-i, i, r);
		gl.glVertex3d(-i, -i, r);
		
		//Back:
		gl.glVertex3d(r, r, -r);
		gl.glVertex3d(-r, r, -r);
		gl.glVertex3d(-r, i, -r);
		gl.glVertex3d(r, i, -r);
		
		gl.glVertex3d(r, -r, -r);
		gl.glVertex3d(-r, -r, -r);
		gl.glVertex3d(-r, -i, -r);
		gl.glVertex3d(r, -i, -r);
		
		gl.glVertex3d(r, -i, -r);
		gl.glVertex3d(r, i, -r);
		gl.glVertex3d(i, i, -r);
		gl.glVertex3d(i, -i, -r);
		
		gl.glVertex3d(-r, -i, -r);
		gl.glVertex3d(-r, i, -r);
		gl.glVertex3d(-i, i, -r);
		gl.glVertex3d(-i, -i, -r);
		
		gl.glEnd();
		
		gl.glPopMatrix();
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
//		System.out.println("Resize!!!");
		final GL2 gl = drawable.getGL().getGL2();
		
		if(height <= 0)
		{
			height = 1;
		}
		
		final float aspectRatio = (float) width / (float) height;
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
//		System.out.println("Aspect: " + aspectRatio);
		glu.gluPerspective(45.0f, aspectRatio, 1.0, 100.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
//	private void setRandomColor(GL2 gl)
//	{
//		final float[] c = {0f, 0.2f, 0.8f, 1f};
//		
//		int red = r.nextInt(4);
//		int green = r.nextInt(4);
//		int blue = r.nextInt(4);
//		
//		gl.glColor3f(c[red], c[green], c[blue]);
//	}
}
