package de.ecconia.mc.jclient.gui.tabs;

import java.awt.Color;
import java.awt.Graphics;
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
	
	private final XYStorage<ChunkEntry> chunks = new XYStorage<>();
	
	public ChunkMap()
	{
	}
	
	public void load(int x, int z)
	{
		chunks.put(x, z, new ChunkEntry(x, z));
		
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
		ChunkEntry chunk = chunks.get(x, z);
		if(chunk != null)
		{
			chunk.loaded = false;
			repaint();
		}
	}
	
	public void update(int x, int z)
	{
		//TODO: Update timestamp.
	}
	
	private static class ChunkEntry
	{
		//TODO: Timestamp, to highlight, when the last update did occur.
		public boolean loaded = true;
		public final int x;
		public final int z;
		
		public ChunkEntry(int x, int z)
		{
			this.x = x;
			this.z = z;
		}
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
		
		g.clearRect(0, 0, w, h);
		
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
		
		Iterator<ChunkEntry> it = chunks.iterator();
		while(it.hasNext())
		{
			ChunkEntry p = it.next();
			g.setColor(p.loaded ? Color.green : Color.red);
			g.fillRect((p.x - xMin) * a + 1, (p.z - zMin) * a + 1, a - 1, a - 1);
		}
	}
}
