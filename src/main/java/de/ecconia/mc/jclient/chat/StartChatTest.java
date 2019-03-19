package de.ecconia.mc.jclient.chat;

import de.ecconia.mc.jclient.tools.json.JSONException;
import de.ecconia.mc.jclient.tools.json.JSONObject;
import de.ecconia.mc.jclient.tools.json.JSONParser;

public class StartChatTest
{
	public static void main(String[] args)
	{
		String[] testMessages = new String[] {
			"{\"extra\":[{\"color\":\"dark_aqua\",\"text\":\"\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\n\"},{\"color\":\"gray\",\"text\":\"Welcome \"},{\"color\":\"gold\",\"text\":\"Ecconia\"},{\"color\":\"gray\",\"text\":\", to Stym\u0027s Redstone Server \"},{\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://www.redstone-server.info\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"http://www.redstone-server.info\"}},\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_green\",\"text\":\"Website\"},{\"color\":\"gold\",\"text\":\"]\"}],\"text\":\"\"},{\"extra\":[{\"color\":\"dark_aqua\",\"text\":\"\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\u003d\"}],\"text\":\"\n\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"dark_aqua\",\"text\":\"Joined unvanished. \"},{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vanish\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"/vanish\"}},\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_green\",\"text\":\"toggle vanish\"},{\"color\":\"gold\",\"text\":\"]\"}],\"text\":\"\"},{\"text\":\" \"},{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vanish vanishonlogin\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"/vanish vanishonlogin\"}},\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_green\",\"text\":\"toggle auto-vanish\"},{\"color\":\"gold\",\"text\":\"]\"}],\"text\":\"\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"yellow\",\"text\":\"Ecconia\"},{\"color\":\"yellow\",\"text\":\" joined the game\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_aqua\",\"text\":\"AutoTrial\"},{\"color\":\"gold\",\"text\":\"] \"},{\"color\":\"aqua\",\"text\":\"There are no trials to be reviewed.\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_red\",\"text\":\"Admin\"},{\"color\":\"gold\",\"text\":\"]\"},{\"color\":\"white\",\"text\":\"Ecconia\"},{\"color\":\"dark_blue\",\"text\":\"[\"},{\"color\":\"blue\",\"text\":\"R\"},{\"color\":\"dark_blue\",\"text\":\"]\"},{\"color\":\"dark_purple\",\"text\":\"[\"},{\"color\":\"gold\",\"text\":\"D\"},{\"color\":\"dark_purple\",\"text\":\"]\"},{\"color\":\"white\",\"text\":\": Yes? (Automated message)\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_aqua\",\"text\":\"ChatManager\"},{\"color\":\"gold\",\"text\":\"] \"},{\"color\":\"red\",\"text\":\"Spamming is not allowed on this server!\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_red\",\"text\":\"Console\"},{\"color\":\"gold\",\"text\":\"]\"},{\"color\":\"white\",\"text\":\"Ecconia\"},{\"color\":\"white\",\"text\":\": example\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_red\",\"text\":\"Admin\"},{\"color\":\"gold\",\"text\":\"]\"},{\"color\":\"white\",\"text\":\"Ecconia\"},{\"color\":\"dark_blue\",\"text\":\"[\"},{\"color\":\"blue\",\"text\":\"R\"},{\"color\":\"dark_blue\",\"text\":\"]\"},{\"color\":\"dark_purple\",\"text\":\"[\"},{\"color\":\"gold\",\"text\":\"D\"},{\"color\":\"dark_purple\",\"text\":\"]\"},{\"color\":\"white\",\"text\":\": Yes? (Automated message)\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"white\",\"text\":\"[\"},{\"color\":\"blue\",\"text\":\"Discord\"},{\"color\":\"white\",\"text\":\"]\"},{\"color\":\"gray\",\"text\":\"Ecconia\"},{\"color\":\"white\",\"text\":\": example\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"gold\",\"text\":\"[\"},{\"color\":\"dark_red\",\"text\":\"Console\"},{\"color\":\"gold\",\"text\":\"]\"},{\"color\":\"white\",\"text\":\"Ecconia\"},{\"color\":\"white\",\"text\":\": runcolorcommand\"}],\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"black\",\"text\":\"0\"},{\"color\":\"dark_blue\",\"text\":\"1\"},{\"color\":\"dark_green\",\"text\":\"2\"},{\"color\":\"dark_aqua\",\"text\":\"3\"},{\"color\":\"dark_red\",\"text\":\"4\"},{\"color\":\"dark_purple\",\"text\":\"5\"},{\"color\":\"gold\",\"text\":\"6\"},{\"color\":\"gray\",\"text\":\"7\"},{\"color\":\"dark_gray\",\"text\":\"8\"},{\"color\":\"blue\",\"text\":\"9\"},{\"color\":\"green\",\"text\":\"a\"},{\"color\":\"aqua\",\"text\":\"b\"},{\"color\":\"red\",\"text\":\"c\"},{\"color\":\"light_purple\",\"text\":\"d\"},{\"color\":\"yellow\",\"text\":\"e\"},{\"color\":\"white\",\"text\":\"f (prefixed with a \u0026)\"}],\"text\":\"\"}",
			"{\"extra\":[{\"text\":\" \u0026k \u003d Obfuscated (\"},{\"obfuscated\":true,\"text\":\"obf\"},{\"text\":\")\"}],\"text\":\"\"}",
			"{\"extra\":[{\"text\":\" \u0026l \u003d \"},{\"bold\":true,\"text\":\"Bold\"}],\"text\":\"\"}",
			"{\"extra\":[{\"text\":\" \u0026m \u003d \"},{\"strikethrough\":true,\"text\":\"Strikethrough\"}],\"text\":\"\"}",
			"{\"extra\":[{\"text\":\" \u0026n \u003d \"},{\"underlined\":true,\"text\":\"Underline\"}],\"text\":\"\"}",
			"{\"extra\":[{\"text\":\" \u0026o \u003d \"},{\"italic\":true,\"text\":\"Italic\"}],\"text\":\"\"}",
			"{\"extra\":[{\"text\":\" \u0026r \u003d Reset\"}],\"text\":\"\"}",
			"{\"extra\":{\"text\":\" \u0026r \u003d Reset\"},\"text\":\"\"}",
			"{\"extra\":[{\"color\":\"white\",\"text\":\"[\"},{\"color\":\"blue\",\"text\":\"Discord\"},{\"color\":\"white\",\"text\":\"]\"},{\"color\":\"gray\",\"text\":\"Ecconia\"},{\"color\":\"white\",\"text\":\": \\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\\\"\u0027\u0027\u0027\u0027\u0027\\\"\\\"\\\"\u0027\\\"\u0027\\\"\u0027\u0027\\\"\u0027\\\"////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\n\\\\nnn\\\\\\\\\\\\\\\\n\\\\n\\\\\\\\n!!!\\\"\\\"\\\"\\\\n\\\\\\\"\"}],\"text\":\"\"}",
			"{\"extra\":[{\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://www.redstone-server.info/node/10632\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Creation date: Thursday, December 13, 2018 - 08:57\\nStatus: Review pending\\nComments: 0\\nUrl: www.redstone-server.info/node/10632\"}},\"text\":\"§dFun4Bros\"},{\"text\":\"§a, \"},{\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://www.redstone-server.info/node/10631\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Creation date: Thursday, December 13, 2018 - 03:04\\nStatus: Review pending\\nComments: 0\\nUrl: www.redstone-server.info/node/10631\"}},\"text\":\"§dJaseface7\"},{\"text\":\"§a, \"},{\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://www.redstone-server.info/node/10630\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Creation date: Thursday, December 13, 2018 - 02:49\\nStatus: Review pending\\nComments: 1\\nUrl: www.redstone-server.info/node/10630\"}},\"text\":\"§dTristanH101\"}],\"text\":\"§6[§3AppCheck§6] §aListing all pending applications: \"}",
		};
		
		for(String s : testMessages)
		{
			System.out.println(s.replace("\n", "\\n"));
			try
			{
				JSONObject message = (JSONObject) JSONParser.parse(s);
				message.debugTree("");
			}
			catch(JSONException e)
			{
				System.out.println();
				System.out.println("Exception while parsing: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			System.out.println();
		}
	}
}
