package net.year4000.erandomnamecolor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@ComponentInformation(friendlyName = "eRandomNameColor", desc = "When players login their username will be a random color.")
public class ERandomNameColor extends BukkitComponent implements Listener {
	
	private LocalConfiguration config;
	private String component = "[eRandomNameColor]";
	
    
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
    
    private static class LocalConfiguration extends ConfigurationBase {
    	@Setting("random-color-name") public Boolean randomColorName = true;
    	@Setting("tablist-name") public Boolean tabName = true;
    	@Setting("colors") public String colors = "0 1 2 3 4 5 6 7 8 9 a b c d e f";
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(PlayerJoinEvent event){
    	String name = event.getPlayer().getDisplayName();
    	ArrayList<String> vars = new ArrayList<String>();
    	String colors = config.colors + " ";
    	Collections.addAll(vars, colors.split(" "));
    	Random rnd = new Random();
    	int index = rnd.nextInt(vars.size());
    	String color = vars.get(index);
    	name = ChatColor.getByChar(color).toString() + name;
    	if(config.randomColorName)event.getPlayer().setDisplayName(name);
    	if(config.tabName)event.getPlayer().setPlayerListName(name);
    }
}