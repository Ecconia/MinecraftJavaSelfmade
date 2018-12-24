package de.ecconia.mc.jclient.gui.elements;

import java.awt.Font;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.ecconia.mc.jclient.chat.parser.ChatColor;
import de.ecconia.mc.jclient.chat.parser.ChatParser;
import de.ecconia.mc.jclient.chat.parser.ChatSegment;

@SuppressWarnings("serial")
public class ColorTypeArea extends WrapTextPane
{
	public ColorTypeArea()
	{
		setEditable(false);
		//TODO: Add font loading, on runtime.
		setFont(new Font("Hack", Font.PLAIN, 18));
	}
	
	private void insert(MutableAttributeSet set, String text)
	{
		//TBI: Here, up there? Where, why?
		StyleConstants.setFontFamily(set, "Hack");
		
		try
		{
			getStyledDocument().insertString(getStyledDocument().getLength(), text, set);
		}
		catch(BadLocationException e)
		{
			//Should never happen, always appending to end.
		}
	}
	
	/**
	 * Method for setting the color in a more easy way.
	 */
	private void setColor(MutableAttributeSet set, ChatColor color)
	{
		StyleConstants.setForeground(set, color.getColor());
	}
	
	//#########################################################################
	
	public void clear()
	{
		setText("");
	}
	
	public void addContent(String text)
	{
		insert(new SimpleAttributeSet(), text);
	}
	
	//TODO: Ensure inheritance.
	public void addSegment(ChatSegment segment)
	{
		MutableAttributeSet set = new SimpleAttributeSet();
		
		if(!segment.getText().isEmpty())
		{
			if(segment.isDirty())
			{
				//TBI: Ignore color, when text with color?
				addSegment(ChatParser.parse(segment.getText()));
			}
			else
			{
				if(segment.getColor() != null)
				{
					setColor(set, segment.getColor());
				}
				
				insert(set, segment.getText());
			}
		}
		
		for(ChatSegment seg : segment.getExtra())
		{
			addSegment(seg);
		}
	}
	
	//#########################################################################
	
	//TODO: Support for chat formatting.
//	private void reset(MutableAttributeSet set)
//	{
//		set.removeAttribute(StyleConstants.Bold);
//		set.removeAttribute(StyleConstants.Italic);
//		set.removeAttribute(StyleConstants.Underline);
//		set.removeAttribute(StyleConstants.StrikeThrough);
//	}
}
