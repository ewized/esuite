package net.year4000.ehub;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eHub", desc = "Stuff for a hub server.")
public class EHub extends BukkitComponent {

	private String component = "[eHub]";
	public Logger logger = Logger.getLogger(component);
	private Configuration config;
    private static EHub instance;

    public static EHub() {
        super;
        instance = this;
    }

    public static EHub inst() {
        return instance;
    }

    public void enable() {
        config = configure(new Configuration());
        CommandBook.registerEvents(new ServerJump());
        CommandBook.registerEvents(new ServerStatus());
        CommandBook.registerEvents(new ServerCatch());
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(CommandBook.inst(), "BungeeCord");
        logger.log(Level.INFO, component + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    public static class Configuration extends ConfigurationBase {
    	@Setting("void-spawn") public boolean voidSpawn = true;
    	@Setting("void-catch") public int voidCatch = 0;
    }

    /**
     * Get the instance of the configuration.
     *
     * @return Instance of configuration
     */
    public Configuration getConfig() {
        return config;
    }
}
