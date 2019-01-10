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
import java.util.Iterator;

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
import de.ecconia.mc.jclient.data.world.Chunk;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.ChunkRenderer;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.FaceReducedChunkRenderer;
import de.ecconia.mc.jclient.gui.gl.models.BlockLib;
import de.ecconia.mc.jclient.gui.input.KeyDebouncer;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.tools.concurrent.XYStorage;

@SuppressWarnings("serial")
public class Simple3D extends JPanel implements GLEventListener
{
	//????????????
	private GLU glu;
	
	//////////////////////////////////////
	//Camera position:
	private float rotation = 30;
	private float neck = 20f;
	
	private double posX = 0D;
	private double posY = 0D;
	private double posZ = 0D;
	
	//////////////////////////////////////
	//World data:
	
	private BlockLib blockModels = new BlockLib();
	private final XYStorage<ChunkRenderer> chunks = new XYStorage<>();
	
	//////////////////////////////////////
	//Mouse capture stuff:
	
	private boolean isCaptured = false;
	private int mouseClickPosX = 0;
	private int mouseClickPosY = 0;
	private int lastAbsMousePosX;
	private int lastAbsMousePosY;
	
	private Robot robot;
	
	//////////////////////////////////////
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
	
	public Simple3D(PrimitiveDataDude dataDude)
	{
		dataDude.getCurrentServer().getMainPlayer().setChunkPosHandler((x, z) -> {
			//TODO: Threadsafe!
			new Thread(() -> {
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e1)
				{
					e1.printStackTrace();
				}
				
				Chunk chunk = dataDude.getCurrentServer().getWorldManager().getChunk(x, z);
				if(chunk != null)
				{
					L.writeLineOnChannel("3D-Text", "Chunk (" + x + ", " + z + ") will now be processed to display it.");
					int[][][] blocks = chunk.toBlockArray();
					//TBI: Maybe only put, if not there?
					chunks.put(x, z, new FaceReducedChunkRenderer(x, z, blocks, blockModels));
				}
				else
				{
					L.writeLineOnChannel("3D-Text", "Chunk (" + x + ", " + z + ") is not loaded yet. Can not display it.");
					System.out.println("Chunk (" + x + ", " + z + ") is not loaded yet. Can not display it.");
				}
			}, "Chunk processor # " + chunkProcessor++).start();
		});
		
		dataDude.getCurrentServer().getMainPlayer().setPlayerPositionHandler((x, y, z) -> {
			posX = x;
			posY = y;
			posZ = z;
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
				L.writeLineOnChannel("3D-Text", "Pressed canvas.");
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
				L.writeLineOnChannel("3D-Text", "Pressed panel.");
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
					L.writeLineOnChannel("3D-Text", "Focus lost -> free mouse.");
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
					L.writeLineOnChannel("3D-Text", "Alt pressed -> free mouse.");
					freeMouse();
				}
			}
			
			@Override
			public void pressed(int keyCode, char keyChar)
			{
				//TODO: Fix directions, forward/backward/left/right
				if(keyChar == 'a')
				{
					posX += 0.8f;
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY, posZ);
				}
				else if(keyChar == 'd')
				{
					posX -= 0.8f;
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY, posZ);
				}
				else if(keyChar == 'w')
				{
					posZ += 0.8f;
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY, posZ);
				}
				else if(keyChar == 's')
				{
					posZ -= 0.8f;
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY, posZ);
				}
				else if(keyChar == 'q')
				{
					posY += 0.8f;
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY, posZ);
				}
				else if(keyChar == 'e')
				{
					posY -= 0.8f;
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY, posZ);
				}
			}
		}));
		
		setLayout(new BorderLayout());
		setFocusable(true);
		add(glcanvas);
		
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
		//Process mouse input:
		if(isCaptured)
		{
			checkForMouseChanges();
		}
		
		//Setup GL stuff for this frame:
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		//Set camera position and direction:
		gl.glRotatef(neck, 1, 0, 0);
		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		//Actual player position:
		gl.glTranslated(-posX, -posY, -posZ);
		//World offset to align to player position:
		gl.glTranslatef(0.5f, -1.04f, 0.5f);
		
		//Print chunks:
		Iterator<ChunkRenderer> it = chunks.iterator();
		while(it.hasNext())
		{
			gl.glPushMatrix();
			it.next().render(gl);
			gl.glPopMatrix();
		}
		
		//Flush!?
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
		glu.gluPerspective(45.0f, aspectRatio, 0.1, 5000.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}
