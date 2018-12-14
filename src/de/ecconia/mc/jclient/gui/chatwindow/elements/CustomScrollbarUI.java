package de.ecconia.mc.jclient.gui.chatwindow.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScrollbarUI extends BasicScrollBarUI
{
	public CustomScrollbarUI()
	{
		
	}
	
	//Background
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
	{
		g.setColor(new Color(30, 30, 30));
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
	}
	
	//Moving thing
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
	{
		if(thumbBounds.isEmpty() || !scrollbar.isEnabled())
		{
			return;
		}
		
		int x = thumbBounds.x;
		int y = thumbBounds.y;
		int w = thumbBounds.width;
		int h = thumbBounds.height;
		
		g.translate(x, y);
		
//		g.setColor(new Color(0, 0, 200));
//		g.fillRect(0, 0, w, h);
		
//		g.setColor(new Color(0, 200, 0));
		
//		g.setColor(new Color(255, 0, 0));
		g.setColor(new Color(60, 60, 60));
//		g.fillOval(0, 0, w, w-1);
//		g.fillOval(0, h-w, w, w-1);
		
		int halfWidth = w / 2;
		int left = 0;
		int right = w - 1;
		for(int i = 0; i < halfWidth; i++)
		{
			g.drawLine(left, halfWidth - i, right, halfWidth - i);
			g.drawLine(left, h - halfWidth + i, right, h - halfWidth + i);
			
			left++;
			right--;
		}
		
//		g.setColor(new Color(255, 0, 0));
		g.fillRect(0, halfWidth + 1, w, h - w);
		
//		g.setColor(new Color(30, 30, 30));
//		g.fillRect(0, h+1, w, 500);
		
		g.translate(-x, -y);
	}
	
	@Override
	protected JButton createIncreaseButton(int orientation)
	{
		JButton jbutton = new JButton();
		jbutton.setPreferredSize(new Dimension(0, 0));
		jbutton.setMinimumSize(new Dimension(0, 0));
		jbutton.setMaximumSize(new Dimension(0, 0));
		return jbutton;
	}
	
	@Override
	protected JButton createDecreaseButton(int orientation)
	{
		JButton jbutton = new JButton();
		jbutton.setPreferredSize(new Dimension(0, 0));
		jbutton.setMinimumSize(new Dimension(0, 0));
		jbutton.setMaximumSize(new Dimension(0, 0));
		return jbutton;
	}
}
