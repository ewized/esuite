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

@ComponentInformation(friendlyName = "eBetterPortal", desc = "Allows end and nether portals send you to spawn point.")
public class EBetterPortal extends BukkitComponent implements Listener {
	
	private LocalConfiguration config;
	private String component = "[eBetterPortal]";	
    
    public void enable() {
    	config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        Logger.getLogger(component).log(Level.INFO, component+" has been enabled.");
    }

	
    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
	
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("classic-portal") public boolean classicPortal = true;
    	@Setting("end-spawn") public boolean endSpawn = true;
    	@Setting("messages.overworld") public String overworldMsg = "Welcome back young traveler.";
    	@Setting("messages.nether") public String netherMsg = "Journey beyond the depths of the underworld.";
    	@Setting("messages.end") public String endMsg = "This is not the end but the beginning.";
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPortalTravel(PlayerPortalEvent event){
    	Player p = event.getPlayer();
    	World toWorld = event.getTo().getWorld();
    	Logger.getLogger(component).log(Level.INFO, p + " has travled to " + toWorld.getName());
    	switch(toWorld.getEnvironment()){
			case NETHER:
	    		event.getTo().getChunk().load();
	    		if(!config.netherMsg.equals("")) p.sendMessage(ChatColor.GOLD + config.netherMsg);
				break;
			case NORMAL:
	    		event.getTo().getChunk().load();
	    		if(!config.overworldMsg.equals("")) p.sendMessage(ChatColor.GOLD + config.overworldMsg);
				break;
			case THE_END:
	    		event.getTo().getChunk().load();
	    		if(config.endSpawn){
	    			event.setTo(toWorld.getSpawnLocation());
	    		}
	    		if(!config.endMsg.equals("")) p.sendMessage(ChatColor.GOLD + config.endMsg);
				break;
			default:
				Logger.getLogger(component).log(Level.INFO, "Error dont know what this environment is: " + toWorld.getEnvironment());
				break;
    	}
    }
}