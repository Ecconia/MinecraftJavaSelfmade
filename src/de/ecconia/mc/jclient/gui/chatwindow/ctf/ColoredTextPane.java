package de.ecconia.mc.jclient.gui.chatwindow.ctf;

import java.awt.Font;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.chat.parser.ChatColor;
import de.ecconia.mc.jclient.chat.parser.ChatSegment;

@SuppressWarnings("serial")
public class ColoredTextPane extends WarpTextPane
{
	public ColoredTextPane()
	{
		setEditable(false);
		setFont(new Font("Hack", Font.PLAIN, 18));
	}
	
	private void insertWithAttSet(String text, AttributeSet set)
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
		
//		String plainMessage;
//		try
//		{
//			plainMessage = messageContainer.getPlainMessage();
//		}
//		catch(Exception e)
//		{
//			//TODO: Ehm console won't print. Lets hack it in. (Clean up this...)
//			insertWithAttSet("Error parsing, see console!\n", set);
//			e.printStackTrace(System.out);
//			return;
//		}
		
//		if(plainMessage.indexOf('\n') != -1)
//		{
//			String[] parts = plainMessage.split("\n");
//			insertWithAttSet("P:\n", set);
//			for(String s : parts)
//			{
//				insertWithAttSet(" : " + s + "\n", set);
//			}
//		}
//		else
//		{
//			insertWithAttSet("P: " + plainMessage + "\n", set);
//		}
		
		ChatSegment segment = messageContainer.getChatSegment();
		writeChatSegment(segment);
		insertWithAttSet("\n", set);
	}
	
	//TODO: Ensure inheritance.
	
	private void writeChatSegment(ChatSegment segment)
	{
		MutableAttributeSet set = new SimpleAttributeSet();
		
		if(segment.getColor() != null)
		{
			StyleConstants.setForeground(set, segment.getColor().getColor());
		}
		
		insertWithAttSet(segment.getText(), set);
		
		for(ChatSegment seg : segment.getExtra())
		{
			writeChatSegment(seg);
		}
	}

	public void addSystemMessage(String message)
	{
		MutableAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, ChatColor.WHITE.getColor());
		insertWithAttSet("<[", set);
		StyleConstants.setForeground(set, ChatColor.YELLOW.getColor());
		insertWithAttSet("System", set);
		StyleConstants.setForeground(set, ChatColor.WHITE.getColor());
		insertWithAttSet("]> ", set);
		StyleConstants.setForeground(set, ChatColor.GRAY.getColor());
		insertWithAttSet(message + "\n", set);
	}
	
//	private void reset(MutableAttributeSet set)
//	{
//		set.removeAttribute(StyleConstants.Bold);
//		set.removeAttribute(StyleConstants.Italic);
//		set.removeAttribute(StyleConstants.Underline);
//		set.removeAttribute(StyleConstants.StrikeThrough);
//	}
}
