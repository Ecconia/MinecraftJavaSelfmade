package de.ecconia.mc.jclient.tools.json;

import java.math.BigDecimal;

public abstract class JSONNode
{
	/**
	 * Print a tree of this JSON element to console.
	 * 
	 * @param prefix - Can be used, will be added something in front of all lines
	 */
	public abstract void debugTree(String prefix);
	
	/**
	 * Get the JSON for this object.
	 * 
	 * @return String - JSON for this object
	 */
	public abstract String printJSON();
	
	protected static String printJSON(Object o)
	{
		if(o == null)
		{
			return "null";
		}
		else if(o instanceof Boolean)
		{
			return o.toString();
		}
		else if(o instanceof JSONNode)
		{
			return ((JSONNode) o).printJSON();
		}
		else if(o instanceof Number)
		{
			//TODO: May throw NumberFormatException, catch...
			if(o instanceof Double)
			{
				return new BigDecimal((Double) o).toString();
			}
			else if(o instanceof Float)
			{
				return new BigDecimal((Float) o).toString();
			}
			
			return o.toString();
		}
		else
		{
			//String will be catched here.
			//TBI: As string? Should not happen right?
			return "\"" + escapeString(o.toString()) + "\"";
		}
	}
	
	private static String escapeString(String s)
	{
		//TBI: Regex, or different method?
		s = s.replace("\\", "\\\\");
		s = s.replace("\"", "\\\"");
		s = s.replace("/", "\\/");
		s = s.replace("\b", "\\b");
		s = s.replace("\f", "\\f");
		s = s.replace("\n", "\\n");
		s = s.replace("\r", "\\r");
		s = s.replace("\t", "\\t");
		//TBI: Unicode chars?
		
		return s;
	}
}
