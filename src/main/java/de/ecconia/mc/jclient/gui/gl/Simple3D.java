package de.ecconia.mc.jclient.gui.gl;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
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

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.input.KeyDebouncer;
import de.ecconia.mc.jclient.network.processor.WorldPacketProcessor;

@SuppressWarnings("serial")
public class Simple3D extends JPanel implements GLEventListener
{
	//????????????
	private GLU glu;
	private Random r = new Random();
	
	//TODO: Optimize access!
	private int[][][] blocks = new int[16][16][256];
	private float[][] colors;
	
	private float rotation = 30;
	private float neck = 20f;
	
	private int posX = -8;
	private int posY = -22;
	private int posZ = -8;
	
	private boolean isCaptured = false;
	private int mouseClickPosX = 0;
	private int mouseClickPosY = 0;
	private int lastAbsMousePosX;
	private int lastAbsMousePosY;
	
	private Robot robot;
	
	//### Mouse ### ### ### ### ### ###
	
	private void freeMouse()
	{
		isCaptured = false;
		
		setCursor(Cursor.getDefaultCursor());
	}
	
	private void captureMouse(int x, int y)
	{
		isCaptured = true;
		mouseClickPosX = x;
		mouseClickPosY = y;
		
		lastAbsMousePosX = x;
		lastAbsMousePosY = y;
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		setCursor(blankCursor);
	}
	
	public void checkForMouseChanges()
	{
		Point currentMousePos = MouseInfo.getPointerInfo().getLocation();
		int currentX = currentMousePos.x;
		int currentY = currentMousePos.y;
		
		int diffX = lastAbsMousePosX - currentX;
		int diffY = lastAbsMousePosY - currentY;
		
		if(diffX != 0 || diffY != 0)
		{
			robot.mouseMove(mouseClickPosX, mouseClickPosY);
			lastAbsMousePosX = mouseClickPosX;
			lastAbsMousePosY = mouseClickPosY;
			
			updateNeck(diffY);
			updateRotation(diffX);
		}
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
	}
	
	public void updateRotation(int d)
	{
		rotation -= (float) d / 10f;
		
		if(rotation > 180)
		{
			rotation -= 360;
		}
		
		if(rotation < -180)
		{
			rotation += 360;
		}
	}
	
	//### ### ### ### ### ### ###
	
	private static int chunkProcessor = 1;
	
	public Simple3D(WorldPacketProcessor worldPacketProcessor, PrimitiveDataDude dataDude)
	{
		dataDude.setChunkPosHandler((x, z) -> {
			//TODO: Threadsafe!
			new Thread(() -> {
				System.out.println("On thread: " + Thread.currentThread().getName());
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e1)
				{
					e1.printStackTrace();
				}
				blocks = worldPacketProcessor.getProcessedChunk(x, z);
				
				generateColors();
			}, "Chunk processor # " + chunkProcessor++).start();
		});
		
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
		
		glcanvas.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				//TODO: Fix this issue, the other one should capture the mouse.
				System.out.println("Pressed canvas");
				captureMouse(e.getXOnScreen(), e.getYOnScreen());
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
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
				captureMouse(e.getXOnScreen(), e.getYOnScreen());
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
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
		
		addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				if(isCaptured)
				{
					System.out.println("Focus lost, free mouse!");
					freeMouse();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
			}
		});
		
		addKeyListener(new KeyDebouncer(new KeyDebouncer.KeyPress()
		{
			@Override
			public void released(int keyCode, char keyChar)
			{
				if(keyCode == 18)
				{
					//Free mouse, if captured.
					System.out.println("Free Mouse, cause ALT.");
					freeMouse();
				}
			}
			
			@Override
			public void pressed(int keyCode, char keyChar)
			{
				//TODO: Fix directions, forward/backward/left/right
				if(keyChar == 'a')
				{
					posX++;
				}
				else if(keyChar == 'd')
				{
					posX--;
				}
				else if(keyChar == 'w')
				{
					posZ++;
				}
				else if(keyChar == 's')
				{
					posZ--;
				}
				else if(keyChar == 'q')
				{
					posY++;
				}
				else if(keyChar == 'e')
				{
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
	
	private void generateColors()
	{
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
		if(isCaptured)
		{
			checkForMouseChanges();
		}
		
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
						Helper3D.drawBlock(gl, x, y, z - 8);
					}
				}
			}
		}
		
		gl.glFlush();
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		if(isCaptured)
		{
			System.out.println("Resized, free mouse.");
			freeMouse();
		}
		
		final GL2 gl = drawable.getGL().getGL2();
		
		if(height <= 0)
		{
			height = 1;
		}
		
		final float aspectRatio = (float) width / (float) height;
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, aspectRatio, 1.0, 100.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}
