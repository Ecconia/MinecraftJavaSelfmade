package de.ecconia.mc.jclient.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ScrollBarUI;

import de.ecconia.mc.jclient.chat.ChatFormatException;
import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.chat.parser.ChatSegment;
import de.ecconia.mc.jclient.gui.elements.ColorTypeArea;
import de.ecconia.mc.jclient.gui.elements.CustomScrollbarUI;
import de.ecconia.mc.jclient.main.PrimitiveDataDude;
import de.ecconia.mc.jclient.tools.json.JSONException;

@SuppressWarnings("serial")
public class ChatPane extends JPanel
{
	private final JTextField inputLine;
	private final JScrollPane scrollpane;
	private final ColorTypeArea history;
	
	public ChatPane(PrimitiveDataDude dataDude)
	{
		setLayout(new BorderLayout());
		
		//Creating & Listeners:
		inputLine = new JTextField();
		inputLine.addActionListener(a -> {
			dataDude.sendChat(inputLine.getText());
			inputLine.setText("");
		});
		history = new ColorTypeArea();
		scrollpane = new JScrollPane(history);
		
		//Style:
		inputLine.setBorder(new EmptyBorder(1, 3, 1, 3));
		inputLine.setBackground(new Color(60, 60, 60));
		inputLine.setForeground(new Color(200, 200, 200));
		inputLine.setPreferredSize(new Dimension(0, 30));
		inputLine.setFont(new Font("hack", Font.PLAIN, 20));
		inputLine.setCaretColor(new Color(200, 200, 200));
		
		history.setBackground(new Color(30, 30, 30));
		history.setForeground(Color.white);
		history.setBorder(new EmptyBorder(1, 1, 1, 1));
		
		scrollpane.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		ScrollBarUI scrollbarUI = new CustomScrollbarUI();
		scrollpane.getVerticalScrollBar().setUI(scrollbarUI);
		
		//Adding:
		add(inputLine, BorderLayout.SOUTH);
		add(scrollpane, BorderLayout.CENTER);
	}
	
	public void addJSONLine(ParsedMessageContainer message)
	{
		boolean scrolled = scrollpane.getVerticalScrollBar().getValue() + scrollpane.getVerticalScrollBar().getVisibleAmount() == scrollpane.getVerticalScrollBar().getMaximum();
		
		try
		{
			ChatSegment segment = message.getChatSegment();
			history.addSegment(segment);
			history.addContent("\n");
		}
		catch(JSONException | ChatFormatException e)
		{
			//TODO: Ehm console won't print. Lets hack it in. (Clean up this...)
			//TBI: Whats that TODO about? :P Probably cause thats no good place for error reporting.
			addSystemMessage("Error parsing chat message, see console!");
			System.out.println("Original JSON: " + message.getRawJson());
			e.printStackTrace(System.out);
		}
		
		if(scrolled)
		{
			//TBI: Why two?
			EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
						}
					});
				}
			});
		}
	}
	
	public void addSystemMessage(String message)
	{
		history.addSegment(new ChatSegment("§f<[§eSystem§f]> §7" + message + "\n"));
	}
}
