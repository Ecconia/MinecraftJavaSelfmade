package de.ecconia.mc.jclient.gui.gl.models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import de.ecconia.mc.jclient.main.Constants;
import de.ecconia.mc.jclient.main.FatalException;

public class BlockDataLib
{
	//TODO: Auto update.
	private static final int PADDING = (int) (Math.log10(Constants.amountBlockstates) + 1);
	private static final int SIDE = (int) Math.ceil(Math.sqrt(Constants.amountBlockstates));
	
	public static final float LENGTH = 1f / (float) SIDE;
	
	private float[] textureOffsetX = new float[Constants.amountBlockstates];
	private float[] textureOffsetY = new float[Constants.amountBlockstates];
	
	private int bigTexture;
	
	public BlockDataLib(GL3 gl)
	{
		File folder = new File(Constants.textureFolder);
		if(!folder.exists())
		{
			throw new FatalException("TextureFolder does not exist.");
		}
		
		IntBuffer ids = IntBuffer.allocate(1);
		gl.glGenTextures(1, ids);
		bigTexture = ids.get(0);
		ids = null;
		
		gl.glEnable(GL3.GL_TEXTURE_2D);
		
		System.out.println("Started loading textures.");
		try
		{
			gl.glBindTexture(GL3.GL_TEXTURE_2D, bigTexture);
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
			
			File imageFile = new File(folder, Constants.textureFile);
			
			//TODO: get rid of this weird class.
			TextureData td = TextureIO.newTextureData(gl.getGLProfile(), imageFile, false, "png");
			BufferedImage image = ImageIO.read(new FileInputStream(imageFile));
			gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGB, image.getWidth(), image.getHeight(), 0, GL3.GL_RGB, GL3.GL_UNSIGNED_BYTE, td.getBuffer());
			gl.glGenerateMipmap(GL3.GL_TEXTURE_2D);
			
			System.out.println("Finished loading textures.");
		}
		catch(Exception e)
		{
			//TODO: Fix way of stopping the client!
			e.printStackTrace();
			System.exit(1);
			throw new FatalException("IOException while loading a texture.");
		}
		
		int i = 0;
		loop: for(int y = 0; y < SIDE; y++)
		{
			float vx = 0;
			float vy = y * LENGTH;
			
			for(int x = 0; x < SIDE; x++)
			{
				textureOffsetX[i] = vx;
				textureOffsetY[i] = vy;
				
				vx += LENGTH;
				
				if(++i >= Constants.amountBlockstates)
				{
					break loop;
				}
			}
		}
	}
	
	public float getOffsetX(int blockdata)
	{
		return textureOffsetX[blockdata];
	}
	
	public float getOffsetY(int blockdata)
	{
		return textureOffsetY[blockdata];
	}
	
	public static String f(int i)
	{
		String r = String.valueOf(i);
		while(r.length() < PADDING)
		{
			r = '0' + r;
		}
		return r;
	}
	
	public int getID()
	{
		return bigTexture;
	}
}
