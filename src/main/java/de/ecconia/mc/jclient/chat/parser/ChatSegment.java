package de.ecconia.mc.jclient.chat.parser;

import java.util.ArrayList;
import java.util.List;

public class ChatSegment
{
	private String text;
	private ChatColor color;
	private List<ChatSegment> extra = new ArrayList<>();
	
	private boolean dirty;
	
	public ChatSegment(String text)
	{
		this(text, null, null);
	}
	
	public ChatSegment(String text, ChatColor color)
	{
		this(text, color, null);
	}
	
	public ChatSegment(String text, List<ChatSegment> extra)
	{
		this(text, null, extra);
	}
	
	public ChatSegment(List<ChatSegment> extra)
	{
		this.text = "";
		this.extra = extra == null ? new ArrayList<>() : extra;
	}
	
	public ChatSegment(String text, ChatColor color, List<ChatSegment> extra)
	{
		this.text = text;
		this.color = color;
		this.extra = extra == null ? new ArrayList<>() : extra;
		
		dirty = text.indexOf('§') != -1;
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
	
	public boolean isDirty()
	{
		return dirty;
	}
}
