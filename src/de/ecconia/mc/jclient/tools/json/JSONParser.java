package de.ecconia.mc.jclient.tools.json;

import de.ecconia.mc.jclient.tools.json.JSONToken.TokenIterator;
import de.ecconia.mc.jclient.tools.json.JSONToken.Type;

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
	
	public static String debug = "";
	
	public static JSONNode parse(String s)
	{
		TokenIterator i = JSONToken.parse(s);
		
		Type type = i.next().getType();
		if(type == Type.OBJECT_OPEN)
		{
			return parseObject(i, Scope.INNER);
		}
		else if(type == Type.ARRAY_OPEN)
		{
			return parseArray(i, Scope.INNER);
		}
		else
		{
			throw new JSONException("Parsed JSON either has to be an Object or an Array.");
		}
	}
	
	private static JSONObject parseObject(TokenIterator i, Scope scope)
	{
		JSONObject ret = new JSONObject();
		
		String key = null;
		while(true)
		{
			JSONToken currentToken = i.next();
			
			if(scope == Scope.OUTTER)
			{
				// >{}
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.OBJECT_OPEN)
				{
					scope = Scope.INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.INNER)
			{
				// {>"":X}
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.OBJECT_CLOSE)
				{
					//We are done reading the object at this point.
					break;
				}
				else if(currentToken.getType() == Type.QUOTE)
				{
					//A Key should start here.
					key = parseString(i, Scope.INNER);
					scope = Scope.DOUBLES;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.DOUBLES)
			{
				// {"">:X}
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.PAIR_SEPARATOR)
				{
					scope = Scope.EXPECT_VALUE;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.EXPECT_VALUE)
			{
				// {"":>X}
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.ARRAY_OPEN)
				{
					JSONArray arr = parseArray(i, Scope.INNER);
					ret.put(key, arr);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.OBJECT_OPEN)
				{
					JSONObject obj = parseObject(i, Scope.INNER);
					ret.put(key, obj);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.QUOTE)
				{
					String string = parseString(i, Scope.INNER);
					ret.put(key, string);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.UNKNOWN)
				{
					String string = parseUnknown(i, currentToken.getContent());
					ret.put(key, string);
					scope = Scope.NEXT_INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.NEXT_INNER)
			{
				// {"":X>,"":X}
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.SEPARATOR)
				{
					scope = Scope.EXPECT_ENTRY;
				}
				else if(currentToken.getType() == Type.OBJECT_CLOSE)
				{
					//We are done reading the object at this point.
					break;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.EXPECT_ENTRY)
			{
				// {"":X,>"":X}
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.QUOTE)
				{
					//A Key should start here.
					key = parseString(i, Scope.INNER);
					scope = Scope.DOUBLES;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
		}
		
		return ret;
	}
	
	private static String parseString(TokenIterator i, Scope scope)
	{
		String value = "";
		
		while(true)
		{
			JSONToken currentToken = i.next();
			
			if(scope == Scope.OUTTER)
			{
				// >"X"
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.QUOTE)
				{
					scope = Scope.INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.INNER)
			{
				// ">X"
				if(currentToken.getType() == Type.TEXT)
				{
					value += currentToken.getContent();
				}
				else if(currentToken.getType() == Type.QUOTE)
				{
					//The string has ended.
					break;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
		}
		
		return value;
	}
	
	private static String parseUnknown(TokenIterator i, char firstChar)
	{
		String value = String.valueOf(firstChar);
		
		while(true)
		{
			JSONToken currentToken = i.next();
			
			// >"X"
			if(currentToken.getType() == Type.SPACE || currentToken.getType() == Type.SEPARATOR)
			{
				//A space is in none of the special type regexes.
				//A comma ends the value.
				
				//Read something which the super parser may need, undo the iterator:
				i.undo();
				break;
			}
			else if(currentToken.getType() == Type.UNKNOWN)
			{
				value += currentToken.getContent();
			}
			else
			{
				throw new JSONException("Unexpected token. History: " + i.history());
			}
		}
		
		if(!value.matches("(true|false|-?(0|[1-9][0-9]*)(.[0-9]+)?([eE][\\-+]?[0-9]+))"))
		{
			throw new JSONException("Value does not match a valid format: >" + value + "< " + i.history());
		}
		
		return value;
	}
	
	private static JSONArray parseArray(TokenIterator i, Scope scope)
	{
		JSONArray ret = new JSONArray();
		
		while(true)
		{
			JSONToken currentToken = i.next();
			
			if(scope == Scope.OUTTER)
			{
				// >[]
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.ARRAY_OPEN)
				{
					scope = Scope.INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.INNER)
			{
				// [>X,X]
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.ARRAY_CLOSE)
				{
					//We are done reading the array at this point.
					break;
				}
				else if(currentToken.getType() == Type.ARRAY_OPEN)
				{
					JSONArray arr = parseArray(i, Scope.INNER);
					ret.add(arr);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.OBJECT_OPEN)
				{
					JSONObject obj = parseObject(i, Scope.INNER);
					ret.add(obj);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.QUOTE)
				{
					String string = parseString(i, Scope.INNER);
					ret.add(string);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.UNKNOWN)
				{
					String string = parseUnknown(i, currentToken.getContent());
					ret.add(string);
					scope = Scope.NEXT_INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.NEXT_INNER)
			{
				// [X>,X]
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.SEPARATOR)
				{
					scope = Scope.EXPECT_ENTRY;
				}
				else if(currentToken.getType() == Type.ARRAY_CLOSE)
				{
					//We are done reading the array at this point.
					break;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
			else if(scope == Scope.EXPECT_ENTRY)
			{
				// [X,>X]
				if(currentToken.getType() == Type.SPACE)
				{
				}
				else if(currentToken.getType() == Type.ARRAY_OPEN)
				{
					JSONArray arr = parseArray(i, Scope.INNER);
					ret.add(arr);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.OBJECT_OPEN)
				{
					JSONObject obj = parseObject(i, Scope.INNER);
					ret.add(obj);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.QUOTE)
				{
					String string = parseString(i, Scope.INNER);
					ret.add(string);
					scope = Scope.NEXT_INNER;
				}
				else if(currentToken.getType() == Type.UNKNOWN)
				{
					String string = parseUnknown(i, currentToken.getContent());
					ret.add(string);
					scope = Scope.NEXT_INNER;
				}
				else
				{
					throw new JSONException("Unexpected token. History: " + i.history());
				}
			}
		}
		
		return ret;
	}
}
