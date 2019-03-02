package de.ecconia.mc.jclient.gui.gl;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.data.world.Chunk;
import de.ecconia.mc.jclient.data.world.WorldManager;
import de.ecconia.mc.jclient.gui.gl.PrimitiveMouseHandler.MouseAdapter;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.ChunkRenderer;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.FaceReducedRenderer;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.FaceReducedRenderer2;
import de.ecconia.mc.jclient.gui.gl.helper.Deleteable;
import de.ecconia.mc.jclient.gui.gl.helper.Matrix;
import de.ecconia.mc.jclient.gui.gl.helper.ShaderProgram;
import de.ecconia.mc.jclient.gui.gl.models.BlockLib;
import de.ecconia.mc.jclient.gui.input.KeyDebouncer;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.tools.concurrent.XYStorage;

@SuppressWarnings("serial")
public class Simple3D extends JPanel implements GLEventListener, MouseAdapter, WorldManager.World3DHandler
{
	private PrimitiveDataDude dataDude;
	//Turn to true on Windows...
	//TODO: Either move all debugging into a JOGL window, OR find a solution which works on all OS's (maybe GLCanvas)
	private final static boolean createWindow = false;
	
	//////////////////////////////////////
	//Camera position:
	private float rotation = 30;
	private float neck = 20f;
	
	private float posX = 0f;
	private float posY = 0f;
	private float posZ = 0f;
	
	//////////////////////////////////////
	//World data:
	
	private BlockLib blockModels = new BlockLib();
	
	private final Queue<ChunkRenderer> toBeLoadedChunks = new ConcurrentLinkedQueue<>();
	private final Queue<ChunkRenderer> toBeLoadedChunks2 = new ConcurrentLinkedQueue<>();
	private final XYStorage<ChunkRenderer> chunks = new XYStorage<>();
	private final XYStorage<ChunkRenderer> chunks2 = new XYStorage<>();
	
	//////////////////////////////////////
	//Mouse capture stuff:
	
	private PrimitiveMouseHandler mouseHandler;
	
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
	
	public Simple3D()
	{
		//Add the 3D panel to the debugging view.
		L.addCustomPanel("3D", this);
		//Create here, to prevent issues....
		L.writeLineOnChannel("3D-Text", "Creating...");
		
		//Setup this TAB:
		setLayout(new BorderLayout());
		setFocusable(true);
		setCursor(null);
		
		//Creating mouse handler.
		mouseHandler = new PrimitiveMouseHandler(this, this);
		
		//getting the capabilities object of GL3 profile
		final GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
		
		GLAutoDrawable canvas = null;
		
		if(createWindow)
		{
			capabilities.setBackgroundOpaque(true);
			final GLWindow glWindow = GLWindow.create(capabilities);
			glWindow.setSize(400, 400);
			glWindow.setVisible(true);
			
			canvas = glWindow;
		}
		else
		{
			capabilities.setBitmap(true);
			//TODO: Switch to GLCanvas somehow...
			final GLJPanel glJPanel = new GLJPanel(capabilities);
			glJPanel.setFocusable(false);
			add(glJPanel);
			revalidate();
			
			canvas = glJPanel;
		}
		
		canvas.addGLEventListener(this);
		
		//Wait for init():
		canvas.display();
		
		//TODO: Don't let it run amok here.
		final FPSAnimator animator = new FPSAnimator(canvas, 30, true);
		animator.start();
	}
	
	public void attachServer(PrimitiveDataDude dataDude)
	{
		this.dataDude = dataDude;
		//TODO: Move registration into the handler. 
		//TODO: Fix this issue, the other one should capture the mouse.
//		glcanvas.addMouseListener(mouseHandler);
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
		
//		dataDude.getCurrentServer().getMainPlayer().setChunkPosHandler((x, z) -> {
//			//TODO: Threadsafe!
//			new Thread(() -> {
//				try
//				{
//					//TODO: NOOOOOB Code, no delay! Make deterministic
//					Thread.sleep(500);
//				}
//				catch(InterruptedException e1)
//				{
//					e1.printStackTrace();
//				}
//				
//				Chunk chunk = dataDude.getCurrentServer().getWorldManager().getChunk(x, z);
//				if(chunk != null)
//				{
//					L.writeLineOnChannel("3D-Text", "Chunk (" + x + ", " + z + ") will now be processed to display it.");
//					int[][][] blocks = chunk.toBlockArray();
//					//TBI: Maybe only put, if not there?
//					toBeLoadedChunks.add(new FaceReducedAdvancedRenderer(x, z, blocks, blockModels));
//				}
//				else
//				{
//					L.writeLineOnChannel("3D-Text", "Chunk (" + x + ", " + z + ") is not loaded yet. Can not display it.");
//					System.out.println("Chunk (" + x + ", " + z + ") is not loaded yet. Can not display it.");
//				}
//			}, "Chunk processor # " + chunkProcessor++).start();
//		});
		
		dataDude.getCurrentServer().getMainPlayer().setPlayerPositionHandler((x, y, z) -> {
			posX = (float) x;
			posY = (float) y;
			posZ = (float) z;
		});
		
		dataDude.getCurrentServer().getWorldManager().addNew3DHandler(this);
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
	
	//### 3D Stuff:
	
//	private ShaderProgram faceRenderer;
	private ShaderProgram faceNewRenderer;
	private ShaderProgram faceNewRenderer2;
	
	private final Matrix projection = new Matrix();
	private final Matrix view = new Matrix();
	private final Matrix model = new Matrix();
	
	private final List<Deleteable> stuffToDelete = new ArrayList<>();
	
	@Override
	public void init(GLAutoDrawable drawable)
	{
		final GL3 gl = drawable.getGL().getGL3();
		gl.glEnable(GL3.GL_DEPTH_TEST);
		gl.glClearColor(0.973f, 0.973f, 0.973f, 1.0f);
		
//		faceRenderer = new ShaderProgram(gl, "shaders/face");
		faceNewRenderer = new ShaderProgram(gl, "shaders/faceNew");
		faceNewRenderer2 = new ShaderProgram(gl, "shaders/faceNew2");
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		final GL3 gl = drawable.getGL().getGL3();
		
		for(Deleteable del : stuffToDelete)
		{
			del.delete(gl);
		}
	}
	
	@Override
	public void display(GLAutoDrawable drawable)
	{
		final GL3 gl = drawable.getGL().getGL3();
		
		{
			ChunkRenderer chunk = toBeLoadedChunks.poll();
			if(chunk != null)
			{
				chunk.load(gl);
//				System.out.println("Loaded chunk: " + chunk.getPosX() + "|" + chunk.getPosZ());
				chunks.put(chunk.getPosX(), chunk.getPosZ(), chunk);
				stuffToDelete.add(chunk);
			}
		}
		
		{
			ChunkRenderer chunk = toBeLoadedChunks2.poll();
			if(chunk != null)
			{
				chunk.load(gl);
//				System.out.println("Loaded chunk: " + chunk.getPosX() + "|" + chunk.getPosZ());
				chunks2.put(chunk.getPosX(), chunk.getPosZ(), chunk);
				stuffToDelete.add(chunk);
			}
		}
		
		//Process mouse input:
		mouseHandler.updatePos();
		
		//Setup GL stuff for this frame:
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		
		//Camera:
		view.identity();
		view.rotate(neck, 1, 0, 0);
		view.rotate(rotation, 0, 1, 0);
		view.translate(-posX, -posY, -posZ);
		view.translate(0.5f, -1.04f, 0.5f);
		
		Iterator<ChunkRenderer> it;
		
//		faceRenderer.use(gl);
//		faceRenderer.setUniform(gl, 2, projection.getMat());
//		faceRenderer.setUniform(gl, 1, view.getMat());
//		
//		//Print chunks:
//		it = chunks.iterator();
//		while(it.hasNext())
//		{
//			ChunkRenderer chunk = it.next();
//			
//			model.identity();
//			model.translate(chunk.getOffsetX(), 0, chunk.getOffsetZ());
//			faceRenderer.setUniform(gl, 0, model.getMat());
//			
//			chunk.draw(gl);
//		}
		
		faceNewRenderer.use(gl);
		faceNewRenderer.setUniform(gl, 2, projection.getMat());
		faceNewRenderer.setUniform(gl, 1, view.getMat());
		
		//Print chunks:
		it = chunks2.iterator();
		while(it.hasNext())
		{
			ChunkRenderer chunk = it.next();
			
			model.identity();
			model.translate(chunk.getOffsetX(), 0, chunk.getOffsetZ());
			faceNewRenderer.setUniform(gl, 0, model.getMat());
			
			chunk.draw(gl);
		}
		
		faceNewRenderer2.use(gl);
		faceNewRenderer2.setUniform(gl, 2, projection.getMat());
		faceNewRenderer2.setUniform(gl, 1, view.getMat());
		
		//Print chunks:
		it = chunks2.iterator();
		while(it.hasNext())
		{
			ChunkRenderer chunk = it.next();
			
			model.identity();
			model.translate(chunk.getOffsetX(), 0, chunk.getOffsetZ());
			faceNewRenderer2.setUniform(gl, 0, model.getMat());
			
			chunk.draw(gl);
		}
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		mouseHandler.resize();
		
		final GL3 gl = drawable.getGL().getGL3();
		
		if(height <= 0)
		{
			height = 1;
		}
		
		gl.glViewport(0, 0, width, height);
		projection.perspective(45f, (float) width / (float) height, 0.1f, 100000f);
	}

	@Override
	public void reset()
	{
		//TODO: different thread
		toBeLoadedChunks.clear();
		chunks.clear();
	}

	@Override
	public void loadChunk(Chunk chunk)
	{
		new Thread(() -> {
			int x = chunk.getX();
			int z = chunk.getZ();
			
//			try
//			{
//				//TODO: NOOOOOB Code, no delay! Make deterministic
//				Thread.sleep(500);
//			}
//			catch(InterruptedException e1)
//			{
//				e1.printStackTrace();
//			}
			
//			if(chunk != null)
//			{
			L.writeLineOnChannel("3D-Text", "Chunk (" + x + ", " + z + ") will now be processed to display it.");
			int[][][] blocks = chunk.toBlockArray();
			//TBI: Maybe only put, if not there?
			toBeLoadedChunks.add(new FaceReducedRenderer(x, z, blocks, blockModels));
			toBeLoadedChunks2.add(new FaceReducedRenderer2(x, z, blocks, blockModels));
//			}
//			else
//			{
//				L.writeLineOnChannel("3D-Text", "Chunk (" + x + ", " + z + ") is not loaded yet. Can not display it.");
//				System.out.println("Chunk (" + x + ", " + z + ") is not loaded yet. Can not display it.");
//			}
		}, "Chunk processor # " + chunkProcessor++).start();
	}
}
