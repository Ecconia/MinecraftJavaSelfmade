package de.ecconia.mc.jclient.gui.tabs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class ChunkMap extends JPanel
{
	private int xMin = Integer.MAX_VALUE;
	private int xMax = Integer.MIN_VALUE;
	
	private int yMin = Integer.MAX_VALUE;
	private int yMax = Integer.MIN_VALUE;
	
	private final List<Point> chunks = new ArrayList<>();
	
	public ChunkMap()
	{
	}
	
	public void load(int x, int y)
	{
		chunks.add(new Point(x, y));
		
		if(xMin > x)
		{
			xMin = x;
		}
		
		if(xMax < x)
		{
			xMax = x;
		}
		
		if(yMin > y)
		{
			yMin = y;
		}
		
		if(yMax < y)
		{
			yMax = y;
		}
		
		SwingUtilities.invokeLater(() -> {
			repaint();
		});
	}
	
	public void unload(int x, int y)
	{
		throw new RuntimeException("Impl missing.");
	}
	
	@Override
	public void paint(Graphics g)
	{
		int h = getHeight() - 1;
		int w = getWidth() - 1;
		
		int xAmount = xMax - xMin + 1;
		int yAmount = yMax - yMin + 1;
		
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
		for(Point p : chunks)
		{
			g.fillRect((p.x - xMin) * a + 1, (p.y - yMin) * a + 1, a - 1, a - 1);
		}
	}
}
