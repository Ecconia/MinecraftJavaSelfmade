package de.ecconia.mc.jclient.gui.elements;

import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

//Class from: http://stackoverflow.com/questions/30590031/jtextpane-line-wrap-behavior
@SuppressWarnings("serial")
public class WrapTextPane extends JTextPane
{
	public WrapTextPane()
	{
		setEditorKit(new WrapEditorKit());
	}
	
	private class WrapEditorKit extends StyledEditorKit
	{
		private final ViewFactory defaultFactory = new WrapColumnFactory();
		
		@Override
		public ViewFactory getViewFactory()
		{
			return defaultFactory;
		}
	}
	
	private class WrapColumnFactory implements ViewFactory
	{
		@Override
		public View create(final Element element)
		{
			final String kind = element.getName();
			if(kind != null)
			{
				switch(kind)
				{
				case AbstractDocument.ContentElementName:
					return new WrapLabelView(element);
				case AbstractDocument.ParagraphElementName:
					return new ParagraphView(element);
				case AbstractDocument.SectionElementName:
					return new BoxView(element, View.Y_AXIS);
				case StyleConstants.ComponentElementName:
					return new ComponentView(element);
				case StyleConstants.IconElementName:
					return new IconView(element);
				}
			}
			
			return new LabelView(element);
		}
	}
	
	private class WrapLabelView extends LabelView
	{
		public WrapLabelView(final Element element)
		{
			super(element);
		}
		
		@Override
		public float getMinimumSpan(final int axis)
		{
			switch(axis)
			{
			case View.X_AXIS:
				return 0;
			case View.Y_AXIS:
				return super.getMinimumSpan(axis);
			default:
				throw new IllegalArgumentException("Invalid axis: " + axis);
			}
		}
	}
}
