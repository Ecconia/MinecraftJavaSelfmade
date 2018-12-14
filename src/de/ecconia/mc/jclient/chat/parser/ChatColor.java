package de.ecconia.mc.jclient.chat.parser;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum ChatColor
{
	BLACK('0', new Color(0, 0, 0)),
	DARK_BLUE('1', new Color(0, 0, 170)),
	DARK_GREEN('2', new Color(0, 170, 0)),
	DARK_AQUA('3', new Color(0, 170, 170)),
	DARK_RED('4', new Color(170, 0, 0)),
	DARK_PURPLE('5', new Color(170, 0, 170)),
	GOLD('6', new Color(255, 170, 0)),
	GRAY('7', new Color(170, 170, 170)),
	DARK_GRAY('8', new Color(85, 85, 85)),
	BLUE('9', new Color(85, 85, 255)),
	GREEN('a', new Color(85, 255, 85)),
	AQUA('b', new Color(85, 255, 255)),
	RED('c', new Color(255, 85, 85)),
	LIGHT_PURPLE('d', new Color(255, 85, 255)),
	YELLOW('e', new Color(255, 255, 85)),
	WHITE('f', new Color(255, 255, 255)),
	;
	
	//###########################################
	
	private final char c;
	private final Color color;
	
	private ChatColor(char c, Color color)
	{
		this.c = c;
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	//###########################################
	
	private static Map<String, ChatColor> lookupByName;
	private static Map<Character, ChatColor> lookupByChar;
	
	static
	{
		lookupByName = new HashMap<>();
		lookupByChar = new HashMap<>();
		
		for(ChatColor color : values())
		{
			lookupByName.put(color.name().toLowerCase(), color);
			lookupByChar.put(color.c, color);
		}
	}
	
	public static ChatColor getColorByName(String name)
	{
		return lookupByName.get(name);
	}
	
	public static ChatColor getColorByChar(char c)
	{
		return lookupByChar.get(c);
	}
}
