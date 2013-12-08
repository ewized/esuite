package net.year4000.ejoinmessage;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.Messenger;

@ComponentInformation(friendlyName = "eJoinMessage",
        desc = "Login messages that depends on last join.")
public class EJoinMessage extends BukkitComponent implements Listener {
    private String component = "[eJoinMessage]";
    private String version =
            this.getClass().getPackage().getImplementationVersion();
    private Logger logger = Logger.getLogger(component);
    private LocalConfiguration config;
    private String player;
    private String message;
    private String world;

    public void enable() {
        config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, component + " version "
                + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    /**
     * Configuration options
     */
    public static class LocalConfiguration extends ConfigurationBase {
        @Setting("first-join") public String firstJoin = "&a%player% has joined the game for the first time.";
        @Setting("normal-join") public String normalJoin = "&a%player% has joined the game.";
        @Setting("normal-leave") public String normalLeave = "&a%player% has left the game.";
        @Setting("break-join") public String breakJoin = "&a%player% is back from a break.";
        @Setting("break-time") public Long breakTime = (long) 1209600000;
    }

    /**
     * Listen to the join events.
     *
     * @param event PlayerJoinEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.player = player.getName();
        this.world = player.getWorld().getName();
        this.message = config.normalJoin;
        long lastPlay = player.getLastPlayed();

        // Decide what message to display.
        if (lastPlay == 0) {
            this.message = config.firstJoin;
        }
        else if ((lastPlay + config.breakTime) < System.currentTimeMillis()) {
            this.message = config.breakJoin;
        }

        event.setJoinMessage(replaceVars(this.message));
    }

    /**
     * Listen to the quit events.
     *
     * @param event PlayerQuitEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.player = player.getName();
        this.message = config.normalLeave;
        this.world = player.getWorld().getName();

        event.setQuitMessage(replaceVars(this.message));
    }

    private String replaceVars(String msgFormat) {
        msgFormat = msgFormat.replace("%player%", this.player);
        msgFormat = msgFormat.replace("%world%", this.world);

        for (ChatColor c : ChatColor.values()) {
            msgFormat = msgFormat.replaceAll("&" + c.getChar(), c.toString()); 
        }

        return msgFormat;
    }
}
