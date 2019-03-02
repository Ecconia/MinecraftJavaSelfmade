
package de.ecconia.mc.jclient.resourcegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GenBlockDataTextures
{
	private static final int AMOUNT = 8599;
	private static final int PADDING = (int) (Math.log10(AMOUNT) + 1);
	
	public static void main(String[] args)
	{
		File folder = new File("textures/blockdata/");
		if(!folder.exists() && !folder.mkdirs())
		{
			System.out.println("Could not create folder(s).");
			System.exit(1);
		}
		
		try
		{
			for(int i = 0; i < AMOUNT; i++)
			{
				int width = 64;
				int height = 64;
				
				// Constructs a BufferedImage of one of the predefined image types.
				BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				// Create a graphics which can be used to draw into the buffered image
				Graphics2D g2d = bufferedImage.createGraphics();
				
				//Background:
				g2d.setColor(Color.white);
				g2d.fillRect(0, 0, width, height);
				
				//Border, 3px:
				g2d.setColor(Color.black);
				g2d.drawRect(0, 0, width, height);
				g2d.drawRect(1, 1, width - 1, height - 1);
				g2d.drawRect(2, 2, width - 2, height - 2);
				
				// create a string with yellow
				g2d.setColor(Color.black);
				g2d.drawString(String.valueOf(f(i)), 4, height / 2);
				
				// Disposes of this graphics context and releases any system resources that it is using. 
				g2d.dispose();
				
				// Save as PNG
				File file = new File(folder, "bd-" + f(i) + ".png");
				ImageIO.write(bufferedImage, "png", file);
			}
		}
		catch(IOException e)
		{
			System.out.println("Abort.");
			e.printStackTrace();
		}
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
}
