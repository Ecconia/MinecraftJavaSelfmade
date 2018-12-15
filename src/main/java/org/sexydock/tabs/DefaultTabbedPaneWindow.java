/*
Copyright 2012 James Edwards

This file is part of Jhrome.

Jhrome is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Jhrome is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Jhrome.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sexydock.tabs;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

/**
 * The default implementation of {@link ITabbedPaneWindow}.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class DefaultTabbedPaneWindow extends JFrame implements ITabbedPaneWindow
{
	
	public DefaultTabbedPaneWindow( ) throws HeadlessException
	{
		super( );
		init( );
	}
	
	public DefaultTabbedPaneWindow( GraphicsConfiguration gc )
	{
		super( gc );
		init( );
	}
	
	public DefaultTabbedPaneWindow( String title , GraphicsConfiguration gc )
	{
		super( title , gc );
		init( );
	}
	
	public DefaultTabbedPaneWindow( String title ) throws HeadlessException
	{
		super( title );
		init( );
	}
	
	JTabbedPane	tabbedPane;
	
	private void init( )
	{
		tabbedPane = new JTabbedPane( );
		tabbedPane.setUI( new JhromeTabbedPaneUI( ) );
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.NEW_TAB_BUTTON_VISIBLE , true );
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.TAB_CLOSE_BUTTONS_VISIBLE , true );
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.TAB_DROP_FAILURE_HANDLER , new DefaultTabDropFailureHandler( new DefaultTabbedPaneWindowFactory( this ) ) );
		tabbedPane.putClientProperty( JhromeTabbedPaneUI.DND_POLICY , new DefaultTabbedPaneDndPolicy( true , true ) );
		
		tabbedPane.setBorder( new EmptyBorder( 3 , 3 , 3 , 3 ) );
		getContentPane( ).add( tabbedPane , BorderLayout.CENTER );
		
		tabbedPane.addContainerListener( new DefaultTabsRemovedHandler( ) );
		
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		addWindowListener( new DefaultWindowsClosedHandler( ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhrome.IJhromeWindow#getTabbedPane()
	 */
	@Override
	public JTabbedPane getTabbedPane( )
	{
		return tabbedPane;
	}
	
	@Override
	public Window getWindow( )
	{
		return this;
	}
}
