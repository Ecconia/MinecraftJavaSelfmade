package de.ecconia.mc.jclient.gui.tabs;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ScrollBarUI;

import de.ecconia.mc.jclient.chat.parser.ChatSegment;
import de.ecconia.mc.jclient.gui.elements.ColorTypeArea;
import de.ecconia.mc.jclient.gui.elements.CustomScrollbarUI;

@SuppressWarnings("serial")
public class Statscreen extends JPanel
{
	private final List<Entry> entries = new ArrayList<>();
	private final Map<String, Entry> entryLookup = new HashMap<>();
	
	private final ColorTypeArea area = new ColorTypeArea();
	
	public Statscreen()
	{
		setLayout(new BorderLayout());
		
		JScrollPane scrollpane = new JScrollPane(area);
		scrollpane.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		ScrollBarUI scrollbarUI = new CustomScrollbarUI();
		scrollpane.getVerticalScrollBar().setUI(scrollbarUI);
		
		add(scrollpane, BorderLayout.CENTER);
	}
	
	private void update()
	{
		area.clear();
		for(Entry e : entries)
		{
			area.addContent(e.getText());
			add(e.getValue());
			area.addContent("\n");
		}
	}
	
	private void add(Object o)
	{
		if(o == null)
		{
			area.addContent("null");
		}
		else if(o instanceof ChatSegment)
		{
			area.addSegment((ChatSegment) o);
		}
		else
		{
			area.addContent(o.toString());
		}
	}
	
	//#########################################################################
	
	public void addKey(String key, String text, Object value)
	{
		Entry entry = new Entry(text, value);
		entries.add(entry);
		entryLookup.put(key, entry);
		
		update();
	}
	
	public void updateKey(String key, Object value)
	{
		entryLookup.get(key).setValue(value);
		
		update();
	}
	
	public void removeKey(String key)
	{
		Entry entry = entryLookup.remove(key);
		entries.remove(entry);
		
		update();
	}
	
	//#########################################################################
	
	public static class Entry
	{
		private final String text;
		private Object value;
		
		public Entry(String text, Object value)
		{
			this.text = text;
			this.value = value;
		}
		
		public String getText()
		{
			return text;
		}
		
		public Object getValue()
		{
			return value;
		}
		
		public void setValue(Object value)
		{
			this.value = value;
		}
	}
}
