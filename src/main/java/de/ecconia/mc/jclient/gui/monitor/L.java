package de.ecconia.mc.jclient.gui.monitor;

import javax.swing.JComponent;

public class L
{
	private static TabLogger logger = new TabLogger();
	
	public static void writeLineOnChannel(String channel, String line)
	{
		logger.writeLineOnChannel(channel, line);
	}
	
	public static void addCustomPanel(String name, JComponent panel)
	{
		logger.addCustomPanel(name, panel);
	}
	
	public static void init()
	{
	}
}
