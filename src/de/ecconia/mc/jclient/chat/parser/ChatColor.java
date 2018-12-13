package de.ecconia.mc.jclient.chat.parser;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum ChatColor
{
	BLACK(new Color(0, 0, 0)),
	DARK_BLUE(new Color(0, 0, 170)),
	DARK_GREEN(new Color(0, 170, 0)),
	DARK_AQUA(new Color(0, 170, 170)),
	DARK_RED(new Color(170, 0, 0)),
	DARK_PURPLE(new Color(170, 0, 170)),
	GOLD(new Color(255, 170, 0)),
	GRAY(new Color(170, 170, 170)),
	DARK_GRAY(new Color(85, 85, 85)),
	BLUE(new Color(85, 85, 255)),
	GREEN(new Color(85, 255, 85)),
	AQUA(new Color(85, 255, 255)),
	RED(new Color(255, 85, 85)),
	LIGHT_PURPLE(new Color(255, 85, 255)),
	YELLOW(new Color(255, 255, 85)),
	WHITE(new Color(255, 255, 255)),
	;
	
	//###########################################
	
	private final Color color;
	
	private ChatColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	//###########################################
	
	private static Map<String, ChatColor> lookupByName;
	
	static
	{
		lookupByName = new HashMap<>();
		
		for(ChatColor color : values())
		{
			lookupByName.put(color.name().toLowerCase(), color);
		}
	}
	
	public static ChatColor getColorByName(String name)
	{
		return lookupByName.get(name);
	}
}
