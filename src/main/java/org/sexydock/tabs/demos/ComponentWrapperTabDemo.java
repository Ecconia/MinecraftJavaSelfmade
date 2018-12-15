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

package org.sexydock.tabs.demos;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.sexydock.tabs.ComponentWrapperTab;
import org.sexydock.tabs.DefaultTabbedPaneWindow;
import org.sexydock.tabs.TestTabFactory;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

public class ComponentWrapperTabDemo implements ISexyTabsDemo
{
	@Override
	public void start( )
	{
		DefaultTabbedPaneWindow window = new DefaultTabbedPaneWindow( );
		
		TestTabFactory tabFactory = new TestTabFactory( );
		window.getTabbedPane( ).putClientProperty( JhromeTabbedPaneUI.TAB_FACTORY , tabFactory );
		
		JButton button = new JButton( "Click Me!" );
		button.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( ( Component ) e.getSource( ) ) , "Pretty cool huh?" );
			}
		} );
		
		JTextField textField = new JTextField( "Edit Me!" );
		textField.setTransferHandler( null );
		
		JPanel renderer = new JPanel( new GridLayout( 2 , 1 , 2 , 2 ) );
		renderer.setBorder( new CompoundBorder( new LineBorder( Color.BLACK ) , new EmptyBorder( 5 , 5 , 5 , 5 ) ) );
		renderer.add( button );
		renderer.add( textField );
		
		JhromeTabbedPaneUI tabbedPaneUI = ( JhromeTabbedPaneUI ) window.getTabbedPane( ).getUI( );
		ComponentWrapperTab tab1 = new ComponentWrapperTab( renderer , new JPanel( ) );
		tabbedPaneUI.addTab( 0 , tab1 , false );
		window.getTabbedPane( ).setSelectedIndex( 0 );
		
		window.setSize( 800 , 600 );
		window.setLocationRelativeTo( null );
		window.setVisible( true );
	}
}
