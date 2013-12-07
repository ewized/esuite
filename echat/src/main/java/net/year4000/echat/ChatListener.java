package net.year4000.echat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.commandbook.CommandBook;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import net.year4000.echat.Message;
import net.year4000.echat.BungeeCord;

public class ChatListener implements Listener {

    /**
     * Listens for each chat message and sets up the vars.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        // An instance of the Message Class
        PermissionsResolverManager p = PermissionsResolverManager.getInstance();
        Message message = new Message();
        Configuration configuration = EChat.inst().getConfiguration();
        Player player = event.getPlayer();

        // Sets the vars to be used later.
        message.setPlayerName(player.getName());
        message.setPlayerDisplayName(player.getDisplayName());
        message.setPlayerWorldName(player.getWorld().getName());
        message.setPlayerServer(player.getServer().getServerName());
        message.setPlayerMessage(event.getMessage());
        message.setPlayerGroup(p.getGroups(player)[0]);
        message.setPlayerFormat(configuration.chat);

        // Check if the player can use colors in the chat.
        if (CommandBook.inst().hasPermission(player, "echat.colors"))
            message.setPlayerColor("true");
        else
            message.setPlayerColor("false");

        // Checks where to send the chat and send the correct player format.
        if (configuration.bungeecord) {
            String localFormat = message.getPlayerFormat();
            message.setPlayerFormat(configuration.server);
            BungeeCord bungeecord = new BungeeCord(message);
            message.setPlayerFormat(localFormat);
        }

        // Check if Factions is installed
        if (configuration.factions) {
            FPlayer fplayer = FPlayers.i.get(player);
            message.setPlayerFaction(fplayer.getTag());
            message.setPlayerTitle(fplayer.getRole().getPrefix());
        }

        // Send the message in its own thread and cancel this event.
    	message.sendMessage(message);
        event.setCancelled(true);
    }
}
