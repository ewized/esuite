package net.year4000.eminingworld;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@ComponentInformation(friendlyName = "eMiningWorld", desc = "A special world that you can visit to mine ores.")
@Depend(components = SessionComponent.class)
public class EMiningWorld extends BukkitComponent implements Listener {
	
	private LocalConfiguration config;
	private String component = "[eMiningWorld]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	@InjectComponent private SessionComponent sessions;
	
    public void enable() {
    	config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        miningWorld();
        Logger.getLogger(component).log(Level.INFO, component+" version "+version+" has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
    
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("to-msg") public String toMsg = "Mine to your hearts content.";
    	@Setting("from-msg") public String fromMsg = "Welcome back!";
    	@Setting("world.name") public String worldName = "mining";
    	@Setting("world.seed") public long worldSeed = 123456789;
    	@Setting("world.enviroment") public String worldEnviroment = "NORMAL";
    	@Setting("world.type") public String worldType = "NORMAL";
    	@Setting("world.generate-structures") public boolean generateStructures = true;
    }
    
    public World miningWorld(){
    	return WorldCreator.name(config.worldName)
    	.generateStructures(config.generateStructures)
    	.environment(Environment.valueOf(config.worldEnviroment))
    	.seed(config.worldSeed)
    	.type(WorldType.valueOf(config.worldType))
    	.createWorld();
    }
    public Location location(World world, Location cords){
    	return new Location(world,(double)cords.getBlockX(),(double)cords.getBlockY(),(double)cords.getBlockZ());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPortalTravel(PlayerPortalEvent event){
    	Player p = event.getPlayer();
    	Location from = event.getFrom();
    	World mining = Bukkit.getWorld(Bukkit.getWorld(config.worldName).getUID());
    	Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
    	TeleportCause teleportCause = event.getCause();
    	EMiningWorldSession session = sessions.getSession(EMiningWorldSession.class, p);
    	
    	if(teleportCause == TeleportCause.NETHER_PORTAL){
		    if(p.getItemInHand().getType() == Material.COMPASS && p.getWorld() == spawn.getWorld()){
		    	event.setTo(location(mining,from));
		    	session.setLocation(p.getWorld().getName(),from.getBlockX(),from.getBlockY(),from.getBlockZ());
		    	if(!config.toMsg.equals(""))p.sendMessage(ChatColor.GOLD + config.toMsg);
		    }
		    if(from.getWorld().equals(mining)){
		    	event.setTo(session.getLocation());
		    	event.useTravelAgent(false);
		    	if(!config.fromMsg.equals(""))p.sendMessage(ChatColor.GOLD + config.fromMsg);
		    }
    	}
	    
    }
}