package net.year4000.echat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.sk89q.wepif.PermissionsResolverManager;

public class EChatMessage implements Listener {
	// Grabs the needed classes to make this work.
	private PermissionsResolverManager wepif = PermissionsResolverManager.getInstance();
	private EChatSender sendLocalMessage = EChat.inst().getEChatSender();

	// The vars of the plugin.
	private Player player;
	private String playerName;
	private String playerDisplayName;
	private String playerWorldName;
	private String playerServer;
	private String playerMessage;
	private String[] playerGroups;
	
	// Listens for each chat message and sets up the vars.
    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    	// Sets the vars to be used later.
    	player = event.getPlayer();
    	playerName = player.getName();
    	playerDisplayName = player.getDisplayName();
    	playerWorldName = player.getWorld().getName();
    	playerServer = player.getServer().getServerName();
    	playerMessage = event.getMessage();
    	playerGroups = wepif.getGroups(player); 
    	
    	//Checks where to send the chat.
    	if (EChat.inst().getEChatConfig().bungeecord) {
    		EChat.inst().getEChatBungeeCord().sendChatBungeeCord();
    	}
    	sendLocalMessage.sendChatMessage();
    	
    	// Cancels the message as the plugin will send the messages itself.
    	event.setCancelled(true);
    }

    // Gets the player's name.
    public String getPlayerName() {
    	return this.playerName;
    }

    // Gets the player's display name.
    public String getPlayerDisplayName() {
    	return this.playerDisplayName;
    }

    // Gets the player's world.
    public String getPlayerWorldName() {
    	return this.playerWorldName;
    }

    // Gets the player's server.
    public String getPlayerServer() {
    	return this.playerServer;
    }

    // Gets the player's message.
    public String getPlayerMessage() {
    	return this.playerMessage;
    }
    
    // Gets an array of the player's groups.
    public String[] getPlayerGroups() {
    	return this.playerGroups;
    }
    
    // Gets a single group
    public String getPlayerGroupName(int index) {
    	return this.playerGroups[index];
    }
    
    // Sets the player's name.
    public void setPlayerName(String playerName) {
    	this.playerName = playerName;
    }

    // Sets the player's display name.
    public void setPlayerDisplayName(String playerDisplayName) {
    	this.playerDisplayName = playerDisplayName;
    }

    // Sets the player's world.
    public void setPlayerWorldName(String playerWorldName) {
    	this.playerWorldName = playerWorldName;
    }

    // Sets the player's server.
    public void setPlayerServer(String playerServer) {
    	this.playerServer = playerServer;
    }

    // Sets the player's message.
    public void setPlayerMessage(String playerMessage) {
    	this.playerMessage = playerMessage;
    }
    
    // Sets a single group
    public void setPlayerGroups(String group) {
    	this.playerGroups[0] = group;
    }
}
