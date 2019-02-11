package de.ecconia.mc.jclient.gui.gl;

import java.awt.BorderLayout;
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
import de.ecconia.mc.jclient.gui.gl.PrimitiveMouseHandler.MouseAdapter;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.ChunkRenderer;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.FaceReducedChunkRenderer;
import de.ecconia.mc.jclient.gui.gl.models.BlockLib;
import de.ecconia.mc.jclient.gui.input.KeyDebouncer;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.tools.concurrent.XYStorage;

@SuppressWarnings("serial")
public class Advanced3D extends JPanel implements GLEventListener, MouseAdapter
{
	private final PrimitiveDataDude dataDude;
	
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
	
	private final PrimitiveMouseHandler mouseHandler;
	
	//Neck
	public void updateY(int d)
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
	
	//Rotation
	public void updateX(int d)
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
	
	public Advanced3D(PrimitiveDataDude dataDude)
	{
		this.dataDude = dataDude;
		
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
		
		//getting the capabilities object of GL2 profile        
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setBitmap(true);
		
		// The canvas
		final GLJPanel glcanvas = new GLJPanel(capabilities);
		glcanvas.addGLEventListener(this);
		glcanvas.setFocusable(false);
		glcanvas.setSize(400, 400);
		
		setCursor(null);
		mouseHandler = new PrimitiveMouseHandler(this, this);
		//TODO: Move registration into the handler. 
		//TODO: Fix this issue, the other one should capture the mouse.
		glcanvas.addMouseListener(mouseHandler);
		addMouseListener(mouseHandler);
		addFocusListener(mouseHandler);
		addKeyListener(new KeyDebouncer(new KeyDebouncer.KeyPress()
		{
			@Override
			public void released(int keyCode, char keyChar)
			{
				if(keyCode == 18)
				{
					//Free mouse, if captured.
					mouseHandler.leaveKey();
				}
			}
			
			@Override
			public void pressed(int keyCode, char keyChar)
			{
				if(keyChar == 'a')
				{
					walkIntoDirection(-90);
				}
				else if(keyChar == 'd')
				{
					walkIntoDirection(90);
				}
				else if(keyChar == 'w')
				{
					walkIntoDirection(0);
				}
				else if(keyChar == 's')
				{
					walkIntoDirection(180);
				}
				else if(keyChar == 'q')
				{
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY + 0.8f, posZ);
				}
				else if(keyChar == 'e')
				{
					dataDude.getCurrentServer().getMainPlayer().clientLocation(posX, posY - 0.8f, posZ);
				}
			}
		}));
		
		setLayout(new BorderLayout());
		setFocusable(true);
		add(glcanvas);
		
		final FPSAnimator animator = new FPSAnimator(glcanvas, 30, true);
		animator.start();
	}
	
	private final float conv = (float) (Math.PI / 180f);
	
	//TODO: Should use the Players rotation value, instead of the one from here. Accordingly move it away from this file.
	private void walkIntoDirection(float direction)
	{
		float dir = rotation + direction;
		float distance = 1;
		
		float offsetZ = (float) -(distance * Math.cos(conv * dir));
		float offsetX = (float) (distance * Math.sin(conv * dir));
		
		double x = dataDude.getCurrentServer().getMainPlayer().getLocationX();
		double y = dataDude.getCurrentServer().getMainPlayer().getLocationY();
		double z = dataDude.getCurrentServer().getMainPlayer().getLocationZ();
		
		dataDude.getCurrentServer().getMainPlayer().clientLocation(x + offsetX, y, z + offsetZ);
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
		mouseHandler.updatePos();
		
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
		mouseHandler.resize();
		
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
