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
    private String playerGroup;
    private String playerFormat;

    /**
     * Listens for each chat message and sets up the vars.
     */
    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        // Sets the vars to be used later.
        player = event.getPlayer();
        playerName = player.getName();
        playerDisplayName = player.getDisplayName();
        playerWorldName = player.getWorld().getName();
        playerServer = player.getServer().getServerName();
        playerMessage = event.getMessage();
        playerGroup = wepif.getGroups(player)[0]; 

        //Checks where to send the chat.
        if (EChat.inst().getConfiguration().bungeecord) {
            playerFormat = EChat.inst().getConfiguration().server;
            EChat.inst().getBungeeCord().sendChatBungeeCord();
        }
        playerFormat = EChat.inst().getConfiguration().chat;
        EChat.inst().getSender().sendChatMessage();
    	
        // Cancels the message as the plugin will send the messages itself.
        event.setCancelled(true);
    }

    /**
     * Gets the player's name.
     *
     * @return player's name.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Gets the player's display name on the server the player is log in to.
     *
     * @return player's display name.
     */
    public String getPlayerDisplayName() {
        return this.playerDisplayName;
    }

    /**
     * Gets the world that the player is in on the server.
     *
     * @return player's current world.
     */
    public String getPlayerWorldName() {
        return this.playerWorldName;
    }

    /**
     * Gets the name of the server that the player is log in to.
     *
     * @return player's current server.
     */
    public String getPlayerServer() {
        return this.playerServer;
    }

    /**
     * Gets the player's chat message from the server that the player is log
     * in to.
     *
     * @return player's chat message.
     */
    public String getPlayerMessage() {
        return this.playerMessage;
    }

    /**
     * Gets the default group from the server that the player is log in to.
     *
     * @return player's default group name.
     */
    public String getPlayerGroup() {
        return this.playerGroup;
    }

    /**
     * Gets the message format from the server that the player is log in to.
     *
     * @return message format from the server.
     */
    public String getPlayerFormat() {
        return this.playerFormat;
    }

    /**
     * Sets the player's name.
     *
     * @param playerName The name of the player.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Sets the player's display name.
     *
     * @param playerDisplayName The display name of the player.
     */
    public void setPlayerDisplayName(String playerDisplayName) {
        this.playerDisplayName = playerDisplayName;
    }

    /**
     * Sets the player's world.
     *
     * @param playerWorldName The player's current world name.
     */
    public void setPlayerWorldName(String playerWorldName) {
        this.playerWorldName = playerWorldName;
    }

    /**
     * Sets the player's server.
     *
     * @param playerServer The player's server name.
     */
    public void setPlayerServer(String playerServer) {
        this.playerServer = playerServer;
    }

    /**
     * Sets the player's message.
     *
     * @param playerMessage The player's message.
     */
    public void setPlayerMessage(String playerMessage) {
        this.playerMessage = playerMessage;
    }

    /**
     * Sets the player's single group.
     *
     * @param group The group the players in
     */
    public void setPlayerGroup(String group) {
        this.playerGroup = group;
    }

    /**
     * Sets the player's format.
     *
     * @param format The chats format.
     */
    public void setPlayerFormat(String format) {
        this.playerFormat = format;
    }
}
