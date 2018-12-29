package de.ecconia.mc.jclient.gui.gl;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import de.ecconia.mc.jclient.gui.input.KeyDebouncer;

@SuppressWarnings("serial")
public class YA3DTest extends JPanel implements GLEventListener
{
	private GLU glu;
	private Random r = new Random();
	
	//TODO: Optimize access!
	int[][][] blocks = new int[16][16][256];
	float[][] colors;
	
	float rotation = 0;
	float lifting = -25;
	
	int posX = -8;
	int posY = -22;
	int posZ = -8;
	
	float neck = 20f;
	
	boolean isCaptured = false;
	int mouseClickPosX = 0;
	int mouseClickPosY = 0;
	Robot robot;
	
	private void reset()
	{
		robot.mouseMove(mouseClickPosX, mouseClickPosY);
	}
	
	private void newAbsMousePos(int x, int y)
	{
		if(isCaptured)
		{
			int xMove = mouseClickPosX - x;
			int yMove = mouseClickPosY - y;
			
			if(xMove != 0 || yMove != 0)
			{
				reset();
				
				System.out.println("+ (" + xMove + ", " + yMove + ")");
				
				updateNeck(yMove);
				updateRotation(xMove);
			}
		}
	}
	
	public YA3DTest()
	{
		blocks = DecodeChunkTest.getProcessedChunk();
		generateColors();
		
		try
		{
			robot = new Robot();
		}
		catch(AWTException e1)
		{
			e1.printStackTrace();
		}
		
		//getting the capabilities object of GL2 profile        
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		
		// The canvas
		final GLJPanel glcanvas = new GLJPanel(capabilities);
		glcanvas.addGLEventListener(this);
		glcanvas.setFocusable(false);
		glcanvas.setSize(400, 400);
		
		addMouseWheelListener(e -> {
			int wheelRotation = e.getWheelRotation();
			
			if(e.isShiftDown())
			{
				lifting += wheelRotation * 6;
				System.out.println("L: " + lifting);
			}
			else
			{
				rotation += wheelRotation;
			}
		});
		
		addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				System.out.println("Clicked the window!");
				isCaptured = true;
				mouseClickPosX = e.getXOnScreen();
				mouseClickPosY = e.getYOnScreen();
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(isCaptured)
				{
					reset();
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
			}
		});
		
		addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				newAbsMousePos(e.getXOnScreen(), e.getYOnScreen());
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
			}
		});
		
		addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				if(isCaptured)
				{
					System.out.println("Focus lost, free mouse!");
					isCaptured = false;
				}
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
			}
		});
		
		//Keys:
		//	Control - 17
		
		//Modifiers:
		//	Shift - 16
		//	Alt - 18
		//	ESC - 27
		addKeyListener(new KeyDebouncer(new KeyDebouncer.KeyPress()
		{
			@Override
			public void released(int keyCode, char keyChar)
			{
				//TODO: Add mouse for head/camera rotation.
				if(keyCode == 18)
				{
					//Free mouse, if captured.
					System.out.println("Free Mouse, cause ALT.");
					isCaptured = false;
				}
			}
			
			@Override
			public void pressed(int keyCode, char keyChar)
			{
				//TODO: Fix directions, forward/backward/left/right
				if(keyChar == 'a')
				{
					//Plus 'x'
					posX++;
				}
				else if(keyChar == 'd')
				{
					//minus 'x'
					posX--;
				}
				else if(keyChar == 'w')
				{
					//Plus 'z'
					posZ++;
				}
				else if(keyChar == 's')
				{
					//Minus 'z'
					posZ--;
				}
				else if(keyChar == 'q')
				{
					//Up
					posY++;
				}
				else if(keyChar == 'e')
				{
					//Down
					posY--;
				}
			}
		}));
		
		setLayout(new BorderLayout());
		setFocusable(true);
		add(glcanvas);
		
		final FPSAnimator animator = new FPSAnimator(glcanvas, 30, true);
		animator.start();
	}
	
	public void updateNeck(int d)
	{
		neck -= (float) d / 10f;
		
		if(neck < -90)
		{
			neck = -90;
		}
		
		if(neck > 90)
		{
			neck = 90;
		}
		
		System.out.println("Neck: " + neck);
	}
	
	public void updateRotation(int d)
	{
		rotation -= (float) d / 10f;
		System.out.println("Rotation: " + rotation);
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
	public void display(GLAutoDrawable drawable)
	{
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glRotatef(neck, 1, 0, 0);
		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(posX, posY, posZ);
		for(int y = 0; y < 256; y++)
		{
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					int block = blocks[x][z][y];
					if(block != 0)
					{
						gl.glColor3f(colors[block][0], colors[block][1], colors[block][2]);
						drawBlock(gl, x, y, z);
					}
				}
			}
		}
		
		gl.glFlush();
	}
	
	private final static double blockRadius = 0.4999;
	private final static double blockOutterRadius = 0.45;
	private final static double blockInnerRadius = 0.5;
	
	private void drawBlock(GL2 gl, int x, int y, int z)
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
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		final GL2 gl = drawable.getGL().getGL2();
		
		if(height <= 0)
		{
			height = 1;
		}
		
		final float aspectRatio = (float) width / (float) height;
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, aspectRatio, 0.01, 100.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable)
	{
	}
	
	public void generateColors()
	{
		List<Integer> blocktypes = new ArrayList<>();
		for(int iy = 0; iy < 256; iy++)
		{
			for(int ix = 0; ix < 16; ix++)
			{
				for(int iz = 0; iz < 16; iz++)
				{
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
	}
}
