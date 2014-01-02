import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.ChatColor;

@ComponentInformation(friendlyName = "eMotd", desc = "Normal MOTD with some cool features added.")
public class EMotd extends BukkitComponent implements Listener{

    private static final String COMPONENT = "[eMotd]";
    private static final String VERSION = "1.0";
    private Logger logger = Logger.getLogger(COMPONENT);

    public void enable() {
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, COMPONENT + " version " + VERSION + " has been enabled.");
    }

    public void reload() {
        super.reload();
        logger.log(Level.INFO, COMPONENT + " has been reloaded.");
    }

    // Listens for a server list ping.
    // Then sets the message of the day with various vars.
    @EventHandler()
    public void onServerListPing(ServerListPingEvent event) {
        String serverMotd = event.getMotd();
        event.setMotd(replaceColor(serverMotd));
    }

    // Replace any color defined by Minecraft.
    private String replaceColor(String message) {
        for (ChatColor c : ChatColor.values()) {
            message = message.replaceAll("&" + c.getChar(), c.toString()); 
        }
        return message;
    }
}
