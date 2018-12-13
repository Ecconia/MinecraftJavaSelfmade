package de.ecconia.mc.jclient.chat.parser;

import java.util.ArrayList;
import java.util.List;

public class ChatSegment
{
	private String text;
	private ChatColor color;
	private List<ChatSegment> extra = new ArrayList<>();
	
	public ChatSegment(String text)
	{
		this.text = text;
	}
	
	public ChatSegment(String text, ChatColor color)
	{
		this.text = text;
		this.color = color;
	}
	
	public ChatSegment(String text, List<ChatSegment> extra)
	{
		this.text = text;
		this.extra = extra;
	}
	
	public ChatSegment(String text, ChatColor color, List<ChatSegment> extra)
	{
		this.text = text;
		this.color = color;
		this.extra = extra;
	}
	
	public ChatColor getColor()
	{
		return color;
	}
	
	public List<ChatSegment> getExtra()
	{
		return extra;
	}
	
	public String getText()
	{
		return text;
	}
}
