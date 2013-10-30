package net.year4000.echat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.sk89q.wepif.PermissionsResolverManager;

public class Message implements Listener {
    // Grabs the needed classes to make this work.
    private PermissionsResolverManager wepif = PermissionsResolverManager.getInstance();

    // The vars of the plugin.
    private Player player;
    private String playerName;
    private String playerDisplayName;
    private String playerWorldName;
    private String playerServer;
    private String playerMessage;
    private String[] playerGroups;
    private String playerFormat;

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
            playerFormat = EChat.inst().getConfig().serverFormat;
            EChat.inst().getBungeeCord().sendChatBungeeCord();
        }
        playerFormat = EChat.inst().getConfig().chatFormat;
        EChat.inst().getSender().sendChatMessage();
    	
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

    // Gets a single group
    public String getPlayerFormat() {
        return this.playerFormat;
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

    // Sets the player's single group.
    public void setPlayerGroups(String group) {
        // Make sure the array is longer then zero.
        if (this.playerGroups.length >= 0) {
            this.playerGroups[0] = group;
        }
    }

    // Sets the player's format.
    public void setPlayerFormat(String format) {
        this.playerFormat = format;
    }
}
