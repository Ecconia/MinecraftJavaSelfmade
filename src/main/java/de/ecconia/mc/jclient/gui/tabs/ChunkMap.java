package de.ecconia.mc.jclient.gui.tabs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.ecconia.mc.jclient.tools.concurrent.XYStorage;

@SuppressWarnings("serial")
public class ChunkMap extends JPanel
{
	private int xMin = Integer.MAX_VALUE;
	private int xMax = Integer.MIN_VALUE;
	
	private int zMin = Integer.MAX_VALUE;
	private int zMax = Integer.MIN_VALUE;
	
	private final XYStorage<Point> chunks = new XYStorage<>();
	
	public ChunkMap()
	{
	}
	
	public void load(int x, int z)
	{
		chunks.put(x, z, new Point(x, z));
		
		if(xMin > x)
		{
			xMin = x;
		}
		
		if(xMax < x)
		{
			xMax = x;
		}
		
		if(zMin > z)
		{
			zMin = z;
		}
		
		if(zMax < z)
		{
			zMax = z;
		}
		
		SwingUtilities.invokeLater(() -> {
			repaint();
		});
	}
	
	public void unload(int x, int z)
	{
		throw new RuntimeException("Impl missing.");
	}
	
	@Override
	public void paint(Graphics g)
	{
		int h = getHeight() - 1;
		int w = getWidth() - 1;
		
		int xAmount = xMax - xMin + 1;
		int yAmount = zMax - zMin + 1;
		
		int a;
		{
			int ax = w / xAmount;
			int ay = h / yAmount;
			a = ax < ay ? ax : ay;
		}
		
		int aw = xAmount * a;
		int ah = yAmount * a;
		
		g.setColor(Color.blue);
		
		int y = 0;
		for(int yi = 0; yi <= yAmount; yi++)
		{
			g.drawLine(0, y, aw, y);
			y += a;
		}
		
		int x = 0;
		for(int xi = 0; xi <= xAmount; xi++)
		{
			g.drawLine(x, 0, x, ah);
			x += a;
		}
		
		g.setColor(Color.red);
		Iterator<Point> it = chunks.iterator();
		while(it.hasNext())
		{
			Point p = it.next();
			g.fillRect((p.x - xMin) * a + 1, (p.y - zMin) * a + 1, a - 1, a - 1);
		}
	}
}
