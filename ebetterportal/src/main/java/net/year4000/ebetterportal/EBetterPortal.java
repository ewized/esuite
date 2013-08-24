package net.year4000.ebetterportal;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

@ComponentInformation(friendlyName = "eBetterPortal", desc = "Portals have more features than the default ones.")
public class EBetterPortal extends BukkitComponent implements Listener {

	private String component = "[eBetterPortal]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	private Logger logger = Logger.getLogger(component);
	private LocalConfiguration config;

    public void enable() {
    	config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, component + " version " + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("end-spawn") public boolean endSpawn = true;
    	@Setting("messages.overworld") public String overworldMsg = "Welcome back young traveler.";
    	@Setting("messages.nether") public String netherMsg = "Journey beyond the depths of the underworld.";
    	@Setting("messages.end") public String endMsg = "This is not the end but the beginning.";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPortalTravel(PlayerPortalEvent event) {
    	try {
	    	Player p = event.getPlayer();
	    	World toWorld = event.getTo().getWorld();

	    	switch (toWorld.getEnvironment()) {
				case NETHER:
		    		event.getTo().getChunk().load();
		    		if (!config.netherMsg.equals("")) {
		    			p.sendMessage(ChatColor.GOLD + config.netherMsg);
		    		}
					break;
				case NORMAL:
		    		event.getTo().getChunk().load();
		    		if (!config.overworldMsg.equals("")) {
		    			p.sendMessage(ChatColor.GOLD + config.overworldMsg);
		    		}
					break;
				case THE_END:
		    		event.getTo().getChunk().load();
		    		if (config.endSpawn) {
		    			event.useTravelAgent(false);
		    			event.setTo(toWorld.getSpawnLocation());
		    		}
		    		if (!config.endMsg.equals("")) {
		    			p.sendMessage(ChatColor.GOLD + config.endMsg);
		    		}
					break;
				default:
					logger.log(Level.INFO, "Error don't know what this environment is: " + toWorld.getEnvironment());
					break;
	    	}
    	} catch(Exception e){}
    }
}