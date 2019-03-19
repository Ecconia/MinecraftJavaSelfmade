package de.ecconia.mc.jclient.tools.json;

import java.math.BigDecimal;

import de.ecconia.mc.jclient.tools.json.token.JSONToken;
import de.ecconia.mc.jclient.tools.json.token.JSONTokenIterator;
import de.ecconia.mc.jclient.tools.json.token.JSONType;

//TODO: Support other data types, to prevent fails!
public class JSONParser
{
	enum Scope
	{
		OUTTER, //Outter scope, if here get in.
		INNER, //Inner scope, here all the entries can be found.
		READING_KEY, //Key scope, here the key string will be created.
		DOUBLES, //Inner key scope, here a ':' is expected.
		EXPECT_VALUE, //Expect value scope, searching for the first character of the value here.
		NEXT_INNER, //Like inner scope, but a value has already been read.
		EXPECT_ENTRY, //Expect a key/value instead of an end.
	}
	
	public static JSONNode parse(String s)
	{
		return parse(s, false);
	}
	
	public static JSONNode parse(String s, boolean debug)
	{
		JSONTokenIterator i = JSONToken.parse(s, debug);
		
		JSONType type = i.next().getType();
		if(type == JSONType.OBJECT_OPEN)
		{
			return parseObject(i, Scope.INNER);
		}
		else if(type == JSONType.ARRAY_OPEN)
		{
			return parseArray(i, Scope.INNER);
		}
		else
		{
			throw new JSONException("Parsed JSON either has to be an Object or an Array.");
		}
	}
	
	private static JSONObject parseObject(JSONTokenIterator i, Scope scope)
	{
		JSONObject ret = new JSONObject();
		
		String key = null;
		while(true)
		{
			JSONToken currentToken = i.next();
			
			if(scope == Scope.OUTTER)
			{
				// >{}
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.OBJECT_OPEN)
				{
					scope = Scope.INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.INNER)
			{
				// {>"":X}
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.OBJECT_CLOSE)
				{
					//We are done reading the object at this point.
					break;
				}
				else if(currentToken.getType() == JSONType.QUOTE)
				{
					//A Key should start here.
					key = parseString(i, Scope.INNER);
					scope = Scope.DOUBLES;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.DOUBLES)
			{
				// {"">:X}
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.PAIR_SEPARATOR)
				{
					scope = Scope.EXPECT_VALUE;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.EXPECT_VALUE)
			{
				// {"":>X}
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.ARRAY_OPEN)
				{
					JSONArray arr = parseArray(i, Scope.INNER);
					ret.put(key, arr);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.OBJECT_OPEN)
				{
					JSONObject obj = parseObject(i, Scope.INNER);
					ret.put(key, obj);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.QUOTE)
				{
					String string = parseString(i, Scope.INNER);
					ret.put(key, string);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.UNKNOWN)
				{
					Object obj = parseUnknown(i, currentToken.getContent());
					ret.put(key, obj);
					scope = Scope.NEXT_INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.NEXT_INNER)
			{
				// {"":X>,"":X}
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.SEPARATOR)
				{
					scope = Scope.EXPECT_ENTRY;
				}
				else if(currentToken.getType() == JSONType.OBJECT_CLOSE)
				{
					//We are done reading the object at this point.
					break;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.EXPECT_ENTRY)
			{
				// {"":X,>"":X}
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.QUOTE)
				{
					//A Key should start here.
					key = parseString(i, Scope.INNER);
					scope = Scope.DOUBLES;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
		}
		
		return ret;
	}
	
	private static String parseString(JSONTokenIterator i, Scope scope)
	{
		String value = "";
		
		while(true)
		{
			JSONToken currentToken = i.next();
			
			if(scope == Scope.OUTTER)
			{
				// >"X"
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.QUOTE)
				{
					scope = Scope.INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.INNER)
			{
				// ">X"
				if(currentToken.getType() == JSONType.TEXT)
				{
					value += currentToken.getContent();
				}
				else if(currentToken.getType() == JSONType.QUOTE)
				{
					//The string has ended.
					break;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
		}
		
		return value;
	}
	
	private static Object parseUnknown(JSONTokenIterator i, char firstChar)
	{
		String value = String.valueOf(firstChar);
		
		while(true)
		{
			JSONToken currentToken = i.next();
			
			// >"X"
			if(currentToken.getType() == JSONType.SPACE || currentToken.getType() == JSONType.SEPARATOR)
			{
				//A space is in none of the special type regexes.
				//A comma ends the value.
				
				//Read something which the super parser may need, undo the iterator:
				i.undo();
				break;
			}
			else if(currentToken.getType() == JSONType.UNKNOWN)
			{
				value += currentToken.getContent();
			}
			else if(currentToken.getType() == JSONType.ARRAY_CLOSE || currentToken.getType() == JSONType.OBJECT_CLOSE)
			{
				i.undo();
				break;
			}
			else
			{
				throw new JSONException("Unexpected token. " + i.history());
			}
		}
		
		if(value.equals("null"))
		{
			return null;
		}
		else if(value.equals("true"))
		{
			return new Boolean(true);
		}
		else if(value.equals("false"))
		{
			return new Boolean(false);
		}
		else if(value.matches("-?(0|[1-9][0-9]*)(.[0-9]+)?([eE][\\-+]?[0-9]+)?"))
		{
			//TBI: Is this an okayish solution, or too much overhead?
			return new BigDecimal(value);
		}
		else
		{
			throw new JSONException("Value does not match a valid format: >" + value + "< " + i.history());
		}
	}
	
	private static JSONArray parseArray(JSONTokenIterator i, Scope scope)
	{
		JSONArray ret = new JSONArray();
		
		while(true)
		{
			JSONToken currentToken = i.next();
			
			if(scope == Scope.OUTTER)
			{
				// >[]
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.ARRAY_OPEN)
				{
					scope = Scope.INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.INNER)
			{
				// [>X,X]
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.ARRAY_CLOSE)
				{
					//We are done reading the array at this point.
					break;
				}
				else if(currentToken.getType() == JSONType.ARRAY_OPEN)
				{
					JSONArray arr = parseArray(i, Scope.INNER);
					ret.add(arr);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.OBJECT_OPEN)
				{
					JSONObject obj = parseObject(i, Scope.INNER);
					ret.add(obj);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.QUOTE)
				{
					String string = parseString(i, Scope.INNER);
					ret.add(string);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.UNKNOWN)
				{
					Object obj = parseUnknown(i, currentToken.getContent());
					ret.add(obj);
					scope = Scope.NEXT_INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.NEXT_INNER)
			{
				// [X>,X]
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.SEPARATOR)
				{
					scope = Scope.EXPECT_ENTRY;
				}
				else if(currentToken.getType() == JSONType.ARRAY_CLOSE)
				{
					//We are done reading the array at this point.
					break;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
			else if(scope == Scope.EXPECT_ENTRY)
			{
				// [X,>X]
				if(currentToken.getType() == JSONType.SPACE)
				{
				}
				else if(currentToken.getType() == JSONType.ARRAY_OPEN)
				{
					JSONArray arr = parseArray(i, Scope.INNER);
					ret.add(arr);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.OBJECT_OPEN)
				{
					JSONObject obj = parseObject(i, Scope.INNER);
					ret.add(obj);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.QUOTE)
				{
					String string = parseString(i, Scope.INNER);
					ret.add(string);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == JSONType.UNKNOWN)
				{
					Object obj = parseUnknown(i, currentToken.getContent());
					ret.add(obj);
					scope = Scope.NEXT_INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. " + i.history());
				}
			}
		}
		
		return ret;
	}
}
