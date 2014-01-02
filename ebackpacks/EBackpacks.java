package net.year4000.ebackpacks;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;

@ComponentInformation(friendlyName = "eBackpacks", desc = "Give your player more space with a backpack.")
@Depend(components = SessionComponent.class)
public class EBackpacks extends BukkitComponent implements Listener{

	private LocalConfiguration config;
	private String component = "[eBackpacks]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	@InjectComponent private SessionComponent sessions;
	
    public void enable() {
    	config = configure(new LocalConfiguration());
    	CommandBook.registerEvents(this);
    	Logger.getLogger(component).log(Level.INFO, component+" version "+version+" has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
	
    public static class LocalConfiguration extends ConfigurationBase {
    	//@Setting("respawn-default-world") public boolean respawnDefaultWorld = true;
    	//@Setting("bed-day") public boolean bedDay = true;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event){
    	Player player = event.getPlayer();
    	EBackpacksSession session = sessions.getSession(EBackpacksSession.class, player);
    	if(session.getNewInventory()){
    		Inventory inventory = Bukkit.createInventory(player, 9, "Backpack");
    		session.setInventory(inventory);
    		session.setNewInventory();
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void openBackpack(InventoryOpenEvent event){
    	Player player = (Player) event.getPlayer();
    	EBackpacksSession session = sessions.getSession(EBackpacksSession.class, player);
    	if(player.isSneaking()){
    		event.getPlayer().openInventory(session.getInventory());
    	}
    }
    
}