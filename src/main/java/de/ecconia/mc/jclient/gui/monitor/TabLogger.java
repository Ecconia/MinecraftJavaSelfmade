package de.ecconia.mc.jclient.gui.monitor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.sexydock.tabs.DefaultTabbedPaneWindow;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class TabLogger
{
	private final Map<String, DebugLog> tabs = new HashMap<>();
	
	private DefaultTabbedPaneWindow window;
	
	public TabLogger()
	{
		SwingUtilities.invokeLater(() -> {
			window = new DefaultTabbedPaneWindow("Logging");
			//There is no reason as of now to control the tabs by keyboard
			
			window.getTabbedPane().setFocusable(false);
			window.getTabbedPane().putClientProperty(JhromeTabbedPaneUI.USE_UNIFORM_WIDTH, false);
			window.getTabbedPane().putClientProperty(JhromeTabbedPaneUI.NEW_TAB_BUTTON_VISIBLE, false);
			window.getTabbedPane().putClientProperty(JhromeTabbedPaneUI.TAB_CLOSE_BUTTONS_VISIBLE, false);
			
			//Add default content?
			
			window.getWindow().setSize(800, 600);
			window.getWindow().setLocationRelativeTo(null);
			window.getWindow().setVisible(true);
		});
	}
	
	public void writeLineOnChannel(String channel, String line)
	{
		DebugLog log = tabs.get(channel);
		if(log == null)
		{
			log = new DebugLog();
			tabs.put(channel, log);
			
			SwingUtilities.invokeLater(() -> {
				window.getTabbedPane().add(channel, tabs.get(channel));
			});
		}
		
		log.write(line);
	}
	
	public void addCustomPanel(String name, JComponent panel)
	{
		SwingUtilities.invokeLater(() -> {
			window.getTabbedPane().add(name, panel);
		});
	}
	
	@SuppressWarnings("serial")
	private static class DebugLog extends JPanel
	{
		private final JTextArea area;
		private final JScrollPane pane;
		
		public DebugLog()
		{
			setLayout(new BorderLayout());
			
			area = new JTextArea();
			area.setEditable(false);
//			area.setLineWrap(true);
			pane = new JScrollPane(area);
			
			add(pane, BorderLayout.CENTER);
		}
		
		private void write(String line)
		{
			boolean scrolled = pane.getVerticalScrollBar().getValue() + pane.getVerticalScrollBar().getVisibleAmount() == pane.getVerticalScrollBar().getMaximum();
			
			area.append(line + "\n");
			
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
								pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMaximum());
							}
						});
					}
				});
			}
		}
	}
}
