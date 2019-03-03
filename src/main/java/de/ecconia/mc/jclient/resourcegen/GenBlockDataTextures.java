
package de.ecconia.mc.jclient.resourcegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public class GenBlockDataTextures
{
	private static final int AMOUNT = 8599;
	private static final int PADDING = (int) (Math.log10(AMOUNT) + 1);
	private static final int SIDE = (int) Math.ceil(Math.sqrt(AMOUNT));
	private static final int TileSide = 64;
	private static final int RootSide = TileSide * SIDE;
	
	public static void main(String[] args)
	{
		File folder = new File("textures/blockdata/");
		if(!folder.exists() && !folder.mkdirs())
		{
			System.out.println("Could not create folder(s).");
			System.exit(1);
		}
		
		Set<String> knownTextures = new HashSet<>();
		knownTextures.addAll(Arrays.asList(folder.list()));
		
		try
		{
			BufferedImage rootPane = new BufferedImage(RootSide, RootSide, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = rootPane.createGraphics();
			
			int i = 0;
			loop: for(int y = SIDE - 1; y >= 0; y--)
			{
				for(int x = 0; x < SIDE; x++)
				{
					BufferedImage bufferedImage;
					String name = String.valueOf(i) + ".png";
					if(knownTextures.contains(name))
					{
						System.out.println("Found: " + name);
						bufferedImage = ImageIO.read(new File(folder, name));
					}
					else
					{
						// Constructs a BufferedImage of one of the predefined image types.
						bufferedImage = new BufferedImage(TileSide, TileSide, BufferedImage.TYPE_3BYTE_BGR);
						// Create a graphics which can be used to draw into the buffered image
						Graphics2D g2d = bufferedImage.createGraphics();
						
						//Background:
						float[] rca = randomColor();
						Color randomBackground = new Color(rca[0], rca[1], rca[2]);
						g2d.setColor(randomBackground);
						g2d.fillRect(0, 0, TileSide, TileSide);
						
						//Border, 3px:
						g2d.setColor(Color.black);
						g2d.drawRect(0, 0, TileSide - 1, TileSide - 1);
						g2d.drawRect(1, 1, TileSide - 2, TileSide - 2);
						g2d.drawRect(2, 2, TileSide - 3, TileSide - 3);
						
						// create a string with yellow
						g2d.setColor(Color.black);
						g2d.drawString(String.valueOf(f(i)), 4, TileSide / 2);
						
						// Disposes of this graphics context and releases any system resources that it is using. 
						g2d.dispose();
					}
					
					g.drawImage(bufferedImage, TileSide * x, TileSide * y, TileSide, TileSide, null);
					
					if(++i > AMOUNT)
					{
						break loop;
					}
				}
			}
			
			g.dispose();
			
			// Save as PNG
			File file = new File(folder, "blockdata.png");
			ImageIO.write(rootPane, "png", file);
		}
		catch(IOException e)
		{
			System.out.println("Abort.");
			e.printStackTrace();
		}
		
		System.out.println("Done.");
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
	
	private static final Random r = new Random();
	private static final float[] c = {0f, 0.2f, 0.8f, 1f};
	
	public static float[] randomColor()
	{
		float[] color = new float[3];
		
		color[0] = c[r.nextInt(4)];
		color[1] = c[r.nextInt(4)];
		color[2] = c[r.nextInt(4)];
		
		return color;
	}
}
