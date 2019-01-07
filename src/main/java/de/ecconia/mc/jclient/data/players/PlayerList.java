package de.ecconia.mc.jclient.data.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.ecconia.mc.jclient.Logger;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.gui.tabs.Statscreen;

public class PlayerList
{
	private final Statscreen screen = new Statscreen();
	
	private final Map<UUID, PlayerEntry> playersByUUID = new HashMap<>();
	private final Map<String, PlayerEntry> playersByName = new HashMap<>();
	
	private String header;
	private String footer;
	
	public PlayerList()
	{
		L.addCustomPanel("PlayerList", screen);
	}
	
	public void newPlayerEntry(UUID uuid, String username, Map<String, String> properties, int gamemode, int ping, String displayname)
	{
		PlayerEntry entry = new PlayerEntry(uuid, username, properties, displayname, ping, gamemode);
		
		PlayerEntry oldByName = playersByName.put(username, entry);
		PlayerEntry oldByUUID = playersByUUID.put(uuid, entry);
		
		boolean nameErr = oldByName != null;
		boolean uuidErr = oldByUUID != null;
		
		if(nameErr || uuidErr)
		{
			Logger.perr("New playerentry, but " + (nameErr ? "playername " : "") + (nameErr && uuidErr ? "and " : "") + (uuidErr ? "uuid " : "") + "still was registered.");
			
			screen.removeKey("player." + uuid);
			screen.removeKey("playerping." + uuid);
			screen.removeKey("playergm." + uuid);
			screen.removeKey("playerdisplay." + uuid);
		}
		
		screen.addKey("player." + uuid, "", uuid + " - " + username);
		screen.addKey("playergm." + uuid, "GM: ", gamemode);
		screen.addKey("playerping." + uuid, "Ping: ", ping);
		screen.addKey("playerdisplay." + uuid, "Name: ", displayname);
	}
	
	public void updatePlayerGamemode(UUID uuid, int gamemode)
	{
		PlayerEntry entry = playersByUUID.get(uuid);
		if(entry == null)
		{
			Logger.perr("Server attempted to update gamemode of player with uuid " + uuid + ", but player is not registered.");
			return;
		}
		entry.setGamemode(gamemode);
		
		screen.updateKey("playergm." + uuid, gamemode);
	}
	
	public void updatePlayerPing(UUID uuid, int ping)
	{
		PlayerEntry entry = playersByUUID.get(uuid);
		if(entry == null)
		{
			Logger.perr("Server attempted to update ping of player with uuid " + uuid + ", but player is not registered.");
			return;
		}
		entry.setPing(ping);
		
		screen.updateKey("playerping." + uuid, ping);
	}
	
	public void updatePlayerDisplayName(UUID uuid, String displayname)
	{
		PlayerEntry entry = playersByUUID.get(uuid);
		if(entry == null)
		{
			Logger.perr("Server attempted to update displayname of player with uuid " + uuid + ", but player is not registered.");
			return;
		}
		entry.setDisplayname(displayname);
		
		screen.updateKey("playerdisplay." + uuid, displayname);
	}
	
	public void removePlayerEntry(UUID uuid)
	{
		PlayerEntry entry = playersByUUID.remove(uuid);
		if(entry == null)
		{
			Logger.perr("Server attempted to remove player with uuid " + uuid + ", but player is not registered.");
			return;
		}
		playersByName.remove(entry.getUsername());
		
		//Remove.
		screen.removeKey("player." + uuid);
		screen.removeKey("playerping." + uuid);
		screen.removeKey("playergm." + uuid);
		screen.removeKey("playerdisplay." + uuid);
	}
	
	public void setHeaderFooter(String header, String footer)
	{
		this.header = header;
		this.footer = footer;
		
		screen.addKey("header", "Header: ", this.header);
		screen.addKey("footer", "Footer: ", this.footer);
	}
	
	public static class PlayerEntry
	{
		private final UUID uuid;
		private final String username;
		private final Map<String, String> properties;
		
		private int ping;
		private int gamemode;
		private String displayname;
		
		public PlayerEntry(UUID uuid, String username, Map<String, String> properties, String displayname, int ping, int gamemode)
		{
			this.uuid = uuid;
			this.username = username;
			this.properties = properties;
			this.ping = ping;
			this.gamemode = gamemode;
			this.displayname = displayname;
		}
		
		public void setPing(int ping)
		{
			this.ping = ping;
		}
		
		public void setGamemode(int gamemode)
		{
			this.gamemode = gamemode;
		}
		
		public void setDisplayname(String displayname)
		{
			this.displayname = displayname;
		}
		
		public UUID getUuid()
		{
			return uuid;
		}
		
		public String getUsername()
		{
			return username;
		}
		
		public Map<String, String> getProperties()
		{
			return properties;
		}
		
		public String getDisplayname()
		{
			return displayname;
		}
		
		public int getGamemode()
		{
			return gamemode;
		}
		
		public int getPing()
		{
			return ping;
		}
	}
}
