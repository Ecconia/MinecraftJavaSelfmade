package de.ecconia.mc.jclient.gui.gl;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JPanel;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

import de.ecconia.mc.jclient.data.player.MainPlayer;
import de.ecconia.mc.jclient.data.world.Chunk;
import de.ecconia.mc.jclient.data.world.WorldObserver;
import de.ecconia.mc.jclient.gui.gl.PrimitiveMouseHandler.MouseAdapter;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.ChunkRenderer;
import de.ecconia.mc.jclient.gui.gl.chunkrenderer.FaceReducedRenderer;
import de.ecconia.mc.jclient.gui.gl.helper.Deleteable;
import de.ecconia.mc.jclient.gui.gl.helper.Matrix;
import de.ecconia.mc.jclient.gui.gl.helper.ShaderProgram;
import de.ecconia.mc.jclient.gui.gl.models.BlockDataLib;
import de.ecconia.mc.jclient.gui.input.KeyDebouncer;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.main.Constants;
import de.ecconia.mc.jclient.main.Logger;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.tools.concurrent.XYStorage;

@SuppressWarnings("serial")
public class Simple3D extends JPanel implements GLEventListener, MouseAdapter, WorldObserver
{
	//TODO: Find better way to let the main-thread wait for the graphic to be done.
	private BlockingQueue<Object> initDone = new LinkedBlockingDeque<>(1);
	
//	//////////////////////////////////////
//	//Camera position:
//	private float rotation = 30;
//	private float neck = 20f;
//	
//	private float posX = 0f;
//	private float posY = 0f;
//	private float posZ = 0f;
	
	//////////////////////////////////////
	//World data:
	
	private BlockDataLib bdLib;
	
	private final Queue<ChunkRenderer> toBeLoadedChunksPriority = new ConcurrentLinkedQueue<>();
	
	private final Queue<ChunkRenderer> toBeLoadedChunks = new ConcurrentLinkedQueue<>();
	private final XYStorage<ChunkRenderer> chunks = new XYStorage<>();
	
	//////////////////////////////////////
	//Mouse capture stuff:
	
	private PrimitiveMouseHandler mouseHandler;
	
	//Neck
	public void updateY(int d)
	{
		float neck = player.getNeck() - (float) d / 10f;
		
		if(neck < -90)
		{
			neck = -90;
		}
		
		if(neck > 90)
		{
			neck = 90;
		}
		
		player.setNeck(neck);
	}
	
	//Rotation
	public void updateX(int d)
	{
		float rotation = player.getRotation() - (float) d / 10f;
		
		if(rotation > 180)
		{
			rotation -= 360;
		}
		
		if(rotation < -180)
		{
			rotation += 360;
		}
		
		player.setRotation(rotation);
	}
	
	//### ### ### ### ### ### ###
	
	private static int chunkProcessor = 1;
	private final FPSAnimator animator;
	private MainPlayer player;
	
	public Simple3D()
	{
		//Dummy player...
		//TODO: Improve. (get rid of)
		player = new MainPlayer(null);
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
		
		if(Constants.external3DWindow)
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
		try
		{
			initDone.take();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		initDone = null;
		
		//TODO: Don't let it run amok here.
		animator = new FPSAnimator(canvas, 60, true);
	}
	
	public void attachServer(PrimitiveDataDude dataDude)
	{
		this.player = dataDude.getCurrentServer().getMainPlayer();
		
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
					walkIntoDirection(90);
				}
				else if(keyChar == 'd')
				{
					walkIntoDirection(-90);
				}
				else if(keyChar == 'w')
				{
					walkIntoDirection(180);
				}
				else if(keyChar == 's')
				{
					walkIntoDirection(0);
				}
				else if(keyChar == 'q')
				{
					player.clientLocation(
						player.getLocationX(),
						player.getLocationY() + 0.8f,
						player.getLocationZ());
				}
				else if(keyChar == 'e')
				{
					player.clientLocation(
						player.getLocationX(),
						player.getLocationY() - 0.8f,
						player.getLocationZ());
				}
			}
		}));
		
		dataDude.getCurrentServer().getWorldManager().observe(this);
		
		animator.start();
	}
	
	private static final float grad2rad = (float) (Math.PI / 180D);
	
	private void walkIntoDirection(float direction)
	{
		float dir = player.getRotation() + direction;
		float distance = 1;
		
		float offsetZ = (float) -(distance * Math.cos(grad2rad * dir));
		float offsetX = (float) (distance * Math.sin(grad2rad * dir));
		
		double x = player.getLocationX();
		double y = player.getLocationY();
		double z = player.getLocationZ();
		
		player.clientLocation(x + offsetX, y, z + offsetZ);
	}
	
	//### 3D Stuff:
	
	private ShaderProgram textureShader;
	
	private final Matrix projection = new Matrix();
	private final Matrix view = new Matrix();
	private final Matrix model = new Matrix();
	
	private final List<Deleteable> stuffToDeleteSoon = new ArrayList<>();
	
	@Override
	public void init(GLAutoDrawable drawable)
	{
		final GL3 gl = drawable.getGL().getGL3();
		gl.glEnable(GL3.GL_DEPTH_TEST);
		gl.glClearColor(0.973f, 0.973f, 0.973f, 1.0f);
		
		textureShader = new ShaderProgram(gl, Constants.shaderPath + "textureFaceSimple");
		bdLib = new BlockDataLib(gl);
		
		try
		{
			initDone.put(new Object());
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		final GL3 gl = drawable.getGL().getGL3();
		
		deleteLoadedChunks();
		for(Deleteable del : stuffToDeleteSoon)
		{
			del.delete(gl);
		}
	}
	
	private void deleteLoadedChunks()
	{
		Iterator<ChunkRenderer> it = chunks.iterator();
		while(it.hasNext())
		{
			stuffToDeleteSoon.add(it.next());
		}
		chunks.clear();
	}
	
	@Override
	public void display(GLAutoDrawable drawable)
	{
		final GL3 gl = drawable.getGL().getGL3();
		
		//Delete e.g. overwritten chunks:
		for(Deleteable del : stuffToDeleteSoon)
		{
			del.delete(gl);
		}
		
		//Load priority chunks:
		{
			for(int i = 0; i < 5; i++)
			{
				ChunkRenderer chunk = toBeLoadedChunksPriority.poll();
				if(chunk != null)
				{
					chunk.load(gl);
					ChunkRenderer oldone = chunks.put(chunk.getPosX(), chunk.getPosZ(), chunk);
					if(oldone != null)
					{
						oldone.delete(gl);
					}
				}
				else
				{
					//Stop attempting to load nothing.
					break;
				}
			}
		}
		
		//Load new chunks:
		{
			ChunkRenderer chunk = toBeLoadedChunks.poll();
			if(chunk != null)
			{
				chunk.load(gl);
				ChunkRenderer oldone = chunks.put(chunk.getPosX(), chunk.getPosZ(), chunk);
				if(oldone != null)
				{
					oldone.delete(gl);
				}
			}
		}
		
		//Process mouse input:
		mouseHandler.updatePos();
		
		//Setup GL stuff for this frame:
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		
		//Camera:
		view.identity();
		view.rotate(player.getNeck(), 1, 0, 0);
		view.rotate(player.getRotation() - 180, 0, 1, 0);
		view.translate(
			-player.getLocationX(),
			-player.getLocationY(),
			-player.getLocationZ());
		view.translate(0.5f, -1.04f, 0.5f);
		
		//Use the big texture map:
		gl.glBindTexture(GL3.GL_TEXTURE_2D, bdLib.getID());
		
		//Set shader and the variables:
		textureShader.use(gl);
		textureShader.setUniform(gl, 2, projection.getMat());
		textureShader.setUniform(gl, 1, view.getMat());
		
		//Print chunks:
		Iterator<ChunkRenderer> it = chunks.iterator();
		while(it.hasNext())
		{
			ChunkRenderer chunk = it.next();
			
			//Set the model matrix for this chunk:
			model.identity();
			model.translate(chunk.getOffsetX(), 0, chunk.getOffsetZ());
			textureShader.setUniform(gl, 0, model.getMat());
			
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
	public void loadChunk(Chunk chunk)
	{
		new Thread(() -> {
			int x = chunk.getX();
			int z = chunk.getZ();
			
			int[][][] blocks = chunk.toBlockArray();
			toBeLoadedChunks.add(new FaceReducedRenderer(x, z, blocks, bdLib));
		}, "Chunk processor # " + chunkProcessor++).start();
	}
	
	@Override
	public void unloadChunk(int x, int z)
	{
		//Nah, lets not do this!
	}
	
	@Override
	public void dirtyChunk(Chunk chunk)
	{
		//TODO: Put on some update list, with higher priority.
		
		new Thread(() -> {
			int x = chunk.getX();
			int z = chunk.getZ();
			
			int[][][] blocks = chunk.toBlockArray();
			//TODO: Don't just add them, replace the old chunks, else all will have to be parsed over multiple frames.
			toBeLoadedChunksPriority.add(new FaceReducedRenderer(x, z, blocks, bdLib));
		}, "Chunk processor # " + chunkProcessor++).start();
	}
	
	@Override
	public void switchWorld()
	{
		//TODO: different thread
		toBeLoadedChunks.clear();
		deleteLoadedChunks();
	}
}
