package de.ecconia.mc.jclient.gui.chatwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.chat.ParsedMessageContainer;
import de.ecconia.mc.jclient.gui.chatwindow.ctf.ColoredTextPane;

public class ChatWindow
{
	private final JFrame frame;
	private final JPanel content;
	
	private final PrimitiveDataDude dataDude;
	
	public ChatWindow(PrimitiveDataDude dataDude)
	{
		this.dataDude = dataDude;
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(850, 500);
		frame.setLocationRelativeTo(null);
		
		content = new JPanel(new BorderLayout());
		frame.setContentPane(content);
		
		addContent();
		
		frame.setVisible(true);
	}
	
	//TODO: Final.
	private JTextField inputLine;
	private JScrollPane scrollpane;
	private ColoredTextPane history;
	
	private void addContent()
	{
		//Creating & Listeners:
		inputLine = new JTextField();
		inputLine.addActionListener(a -> {
			dataDude.sendChat(inputLine.getText());
			inputLine.setText("");
		});
		history = new ColoredTextPane();
		scrollpane = new JScrollPane(history);
		
		//Style:
		inputLine.setBorder(new EmptyBorder(1, 3, 1, 3));
		inputLine.setBackground(new Color(60, 60, 60));
		inputLine.setForeground(new Color(200, 200, 200));
		inputLine.setPreferredSize(new Dimension(0, 30));
		inputLine.setFont(new Font("hack", Font.PLAIN, 20));
		
		history.setBackground(new Color(30, 30, 30));
		history.setForeground(Color.white);
		history.setBorder(new EmptyBorder(1, 1, 1, 1));
		
		scrollpane.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		//Adding:
		content.add(inputLine, BorderLayout.SOUTH);
		content.add(scrollpane, BorderLayout.CENTER);
	}
	
	public void addJSONLine(ParsedMessageContainer message)
	{
		boolean scrolled = scrollpane.getVerticalScrollBar().getValue() + scrollpane.getVerticalScrollBar().getVisibleAmount() == scrollpane.getVerticalScrollBar().getMaximum();
		
		history.addChatLine(message);
		
		if (scrolled)
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
		history.addSystemMessage(message);
	}
}
