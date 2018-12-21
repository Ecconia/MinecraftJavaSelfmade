package de.ecconia.mc.jclient.gui.chatwindow.elements;

import java.awt.Font;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.ecconia.mc.jclient.chat.ChatFormatException;
import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.chat.parser.ChatColor;
import de.ecconia.mc.jclient.chat.parser.ChatParser;
import de.ecconia.mc.jclient.chat.parser.ChatSegment;
import de.ecconia.mc.jclient.tools.json.JSONException;

@SuppressWarnings("serial")
public class ColoredTextPane extends WrapTextPane
{
	public ColoredTextPane()
	{
		setEditable(false);
		setFont(new Font("Hack", Font.PLAIN, 18));
	}
	
	private void insertWithAttSet(AttributeSet set, String text)
	{
		try
		{
			getStyledDocument().insertString(getStyledDocument().getLength(), text, set);
		}
		catch(BadLocationException e)
		{
		}
	}
	
	public void addChatLine(ParsedMessageContainer messageContainer)
	{
		MutableAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setFontFamily(set, "Hack");
		
		try
		{
			ChatSegment segment = messageContainer.getChatSegment();
			writeChatSegment(segment);
			insertWithAttSet(set, "\n");
		}
		catch(JSONException | ChatFormatException e)
		{
			//TODO: Ehm console won't print. Lets hack it in. (Clean up this...)
			//TBI: Whats that TODO about? :P Probably cause thats no good place for error reporting.
			addSystemMessage("Error parsing chat message, see console!");
			System.out.println("Original JSON: " + messageContainer.getRawJson());
			e.printStackTrace(System.out);
		}
	}
	
	//TODO: Ensure inheritance.
	
	private void writeChatSegment(ChatSegment segment)
	{
		MutableAttributeSet set = new SimpleAttributeSet();
		
		if(!segment.getText().isEmpty())
		{
			if(segment.isDirty())
			{
				//TBI: Ignore color, when text with color?
				writeChatSegment(ChatParser.parse(segment.getText()));
			}
			else
			{
				if(segment.getColor() != null)
				{
					StyleConstants.setForeground(set, segment.getColor().getColor());
				}
				
				insertWithAttSet(set, segment.getText());
			}
		}
		
		for(ChatSegment seg : segment.getExtra())
		{
			writeChatSegment(seg);
		}
	}
	
	public void addSystemMessage(String message)
	{
		MutableAttributeSet set = new SimpleAttributeSet();
		setColor(set, ChatColor.WHITE);
		insertWithAttSet(set, "<[");
		setColor(set, ChatColor.YELLOW);
		insertWithAttSet(set, "System");
		setColor(set, ChatColor.WHITE);
		insertWithAttSet(set, "]> ");
		setColor(set, ChatColor.GRAY);
		insertWithAttSet(set, message + "\n");
	}
	
	/**
	 * Method for setting the color in a more easy way.
	 */
	private void setColor(MutableAttributeSet set, ChatColor color)
	{
		StyleConstants.setForeground(set, color.getColor());
	}
	
	//TODO: Support for chat formatting.
//	private void reset(MutableAttributeSet set)
//	{
//		set.removeAttribute(StyleConstants.Bold);
//		set.removeAttribute(StyleConstants.Italic);
//		set.removeAttribute(StyleConstants.Underline);
//		set.removeAttribute(StyleConstants.StrikeThrough);
//	}
}
