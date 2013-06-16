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
import org.bukkit.World.Environment;
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
    	@Setting("nether.spawn") public boolean netherSpawn = true;
    	@Setting("nether.msg") public String netherMsg = "";
    	@Setting("end.spawn") public boolean endSpawn = true;
    	@Setting("end.msg") public String endMsg = "";
    	@Setting("overworld.spawn") public boolean overworldSpawn = true;
    	@Setting("overworld.msg") public String overworldMsg = "";
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPortalTravel(PlayerPortalEvent event){
    	Player p = event.getPlayer();
    	World toWorld = event.getTo().getWorld();
    	System.out.println(toWorld.getName());
    	if(toWorld.getEnvironment().equals(Environment.NETHER)){
    		if(config.netherSpawn){
        		event.useTravelAgent(false);
        		event.setTo(toWorld.getSpawnLocation());
    		}
    		if(!config.netherMsg.equals("")) p.sendMessage(ChatColor.YELLOW + config.netherMsg);
    	} else if(toWorld.getEnvironment().equals(Environment.NORMAL)){
    		if(config.overworldSpawn){
        		event.useTravelAgent(false);
        		event.setTo(toWorld.getSpawnLocation());
    		}
    		if(!config.overworldMsg.equals("")) p.sendMessage(ChatColor.YELLOW + config.overworldMsg);
    	} else if(toWorld.getEnvironment().equals(Environment.THE_END)){
    		if(config.endSpawn){
        		event.useTravelAgent(false);
        		event.setTo(toWorld.getSpawnLocation());
    		}
    		if(!config.endMsg.equals("")) p.sendMessage(ChatColor.YELLOW + config.endMsg);
    	} else{
    		System.out.println("Error dont know what this environment is: " + toWorld.getEnvironment());
    	}
    }
}