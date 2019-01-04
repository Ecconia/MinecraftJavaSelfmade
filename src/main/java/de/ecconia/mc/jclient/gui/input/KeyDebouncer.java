package de.ecconia.mc.jclient.gui.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyDebouncer implements KeyListener
{
	private final static int midDelay = 10;
	
	private final KeyPress handler;
	private final Map<Integer, Long> pressed = new HashMap<>();
	
	public KeyDebouncer(KeyPress handler)
	{
		this.handler = handler;
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		//Ignore, seems to be identical to keyPressed :/
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		Long lastPress = pressed.get(e.getKeyCode());
		
		if(lastPress != null)
		{
			if((System.currentTimeMillis() - lastPress) < midDelay)
			{
				return;
			}
			
			lastPress = null;
		}
		
		if(lastPress == null)
		{
			pressed.put(e.getKeyCode(), System.currentTimeMillis());
			handler.pressed(e.getKeyCode(), e.getKeyChar());
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		pressed.remove(e.getKeyCode());
		handler.released(e.getKeyCode(), e.getKeyChar());
	}
	
	public static interface KeyPress
	{
		public void pressed(int keyCode, char keyChar);
		
		public void released(int keyCode, char keyChar);
	}
}
