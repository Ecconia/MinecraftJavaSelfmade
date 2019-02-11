package de.ecconia.mc.jclient.gui.gl;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import de.ecconia.mc.jclient.gui.monitor.L;

//TODO: Get rid of debug messages:
//TODO: Replace with native mouse hook
public class PrimitiveMouseHandler implements MouseListener, FocusListener
{
	private final Component component;
	private final MouseAdapter adapter;
	
	private Robot robot;
	
	private boolean isCaptured = false;
	private int mouseClickPosX = 0;
	private int mouseClickPosY = 0;
	private int lastAbsMousePosX;
	private int lastAbsMousePosY;
	
	public PrimitiveMouseHandler(Component component, MouseAdapter adapter)
	{
		this.component = component;
		this.adapter = adapter;
		
		try
		{
			robot = new Robot();
		}
		catch(AWTException e1)
		{
			e1.printStackTrace();
		}
	}
	
	//### Public calls:
	public void resize()
	{
		if(isCaptured)
		{
			System.out.println("Resized, free mouse.");
			freeMouse();
		}
	}
	
	public void leaveKey()
	{
		if(isCaptured)
		{
			L.writeLineOnChannel("3D-Text", "Alt pressed -> free mouse.");
			freeMouse();
		}
	}
	
	public void updatePos()
	{
		if(isCaptured)
		{
			checkForMouseChanges();
		}
	}
	
	//### Internal:
	
	public void checkForMouseChanges()
	{
		Point currentMousePos = MouseInfo.getPointerInfo().getLocation();
		int currentX = currentMousePos.x;
		int currentY = currentMousePos.y;
		
		int diffX = lastAbsMousePosX - currentX;
		int diffY = lastAbsMousePosY - currentY;
		
		if(diffX != 0 || diffY != 0)
		{
			robot.mouseMove(mouseClickPosX, mouseClickPosY);
			lastAbsMousePosX = mouseClickPosX;
			lastAbsMousePosY = mouseClickPosY;
			
			adapter.updateX(diffX);
			adapter.updateY(diffY);
		}
	}
	
	private void freeMouse()
	{
		isCaptured = false;
		
		component.setCursor(Cursor.getDefaultCursor());
	}
	
	private void captureMouse(int x, int y)
	{
		isCaptured = true;
		mouseClickPosX = x;
		mouseClickPosY = y;
		
		lastAbsMousePosX = x;
		lastAbsMousePosY = y;
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		component.setCursor(blankCursor);
	}
	
	//### Mouse Listener:
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		L.writeLineOnChannel("3D-Text", "Pressed either.");
//		L.writeLineOnChannel("3D-Text", "Pressed canvas.");
//		L.writeLineOnChannel("3D-Text", "Pressed panel.");
		captureMouse(e.getXOnScreen(), e.getYOnScreen());
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
	}
	
	//### Focus Listener:
	
	@Override
	public void focusGained(FocusEvent e)
	{
	}
	
	@Override
	public void focusLost(FocusEvent e)
	{
		if(isCaptured)
		{
			L.writeLineOnChannel("3D-Text", "Focus lost -> free mouse.");
			freeMouse();
		}
	}
	
	//### Adapter:
	public static interface MouseAdapter
	{
		void updateX(int diff);
		
		void updateY(int diff);
	}
}
