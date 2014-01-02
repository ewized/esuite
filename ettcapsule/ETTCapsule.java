package net.year4000.ettcapsule;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

@ComponentInformation(friendlyName = "eTTCapsule", desc = "Time And Relative Dimension In Space")
@Depend(components = SessionComponent.class)
public class ETTCapsule extends BukkitComponent implements Listener {
	
	private String component = "[eTTCapsule]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	private ETTCapsuleBuild build = new ETTCapsuleBuild();
	private ETTCapsuleSign sign = new ETTCapsuleSign();
	private ETTCapsuleConfig config = new ETTCapsuleConfig();
	@InjectComponent private SessionComponent sessions;
	
    public void enable() {
    	config = configure(config);
    	CommandBook.registerEvents(this);
    	registerCommands(Commands.class);
    	Logger.getLogger(component).log(Level.INFO, component+" version "+version+" has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }

    public World getTTCaspuleWorld(String id){
    	return WorldCreator.name("TT_Capsule/TT-"+id)
    	.environment(Environment.THE_END)
    	.type(WorldType.FLAT)
    	.generator(new ETTCapsuleGenerator())
    	.createWorld();
    }
    
	public class Commands{
		
		@Command(aliases = {"deletetardis"}, usage = "", desc = "Deletes a tardis world")
	    public void delete(CommandContext args, CommandSender player){
			World world = Bukkit.getWorld(args.getString(0));
			Bukkit.unloadWorld(world, false);
		}

	}
    
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(action == Action.RIGHT_CLICK_BLOCK){
			Block block = event.getClickedBlock();
			if(block.getType() == Material.DAYLIGHT_DETECTOR){
				World world = block.getWorld();
				Location location = block.getLocation().add(0, -1, 0);
				if(world.getBlockAt(location).getType() == Material.BEACON){
					//Inventory beacon = Bukkit.createInventory(null, 9, "Place a nether star inside.");
				}
			}
		}
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spawnEvent(CreatureSpawnEvent event) {
	  World world = event.getLocation().getWorld();
	  SpawnReason spawnReason = event.getSpawnReason();
	  if(world.getName().contains("TT_Capsule") && spawnReason == SpawnReason.NATURAL){
		  event.setCancelled(true);
	  }
	}
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPortalTravel(PlayerPortalEvent event){
    	Player player = event.getPlayer();
    	Location l = travelTTCapsule(event.getFrom(), player);

		if(l != null){
			TravelAgent ta = event.getPortalTravelAgent();
			ta.setCanCreatePortal(false);
			event.setTo(l);
		}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityPortalTravel(EntityPortalEvent  event){
    	Location l = travelTTCapsule(event.getFrom(), null);

		if(l != null){
			TravelAgent ta = event.getPortalTravelAgent();
			ta.setCanCreatePortal(false);
			event.setTo(l);
		}
    }
    
	public Location travelTTCapsule(Location location, Player player){
		if(sign.readSign(sign.searchSign(location))){
			if (player != null) {
				if(!(sign.getIDWorld()).equalsIgnoreCase(location.getWorld().getName())){
					ETTCapsuleSession session = sessions.getSession(ETTCapsuleSession.class, player);
					Block signl = sign.getBlock();
					String world = signl.getWorld().getName();
					int x = signl.getX();
					int y = signl.getY()-2;
					int z = signl.getZ();
					session.setLocation(world,x,y,z);
					return enterTTCapsule();
				}else{
					return exitTTCapsule(player);
				}
			}
		}
		return null;
	}
	
	public Location enterTTCapsule(){
		World ttcapsuleWorld = getTTCaspuleWorld(sign.getID());
		build.buildBoxInSide(ttcapsuleWorld
				,ttcapsuleWorld.getSpawnLocation().getBlockX()
				,ttcapsuleWorld.getSpawnLocation().getBlockY()
				,ttcapsuleWorld.getSpawnLocation().getBlockZ()
				,sign.getOwner());
		return ttcapsuleWorld.getSpawnLocation();
	}
	
	public Location exitTTCapsule(Player player){
		ETTCapsuleSession session = sessions.getSession(ETTCapsuleSession.class, player);
		build.buildBoxOutSide(session.getLocation().getWorld()
				,session.getLocation().getBlockX()
				,session.getLocation().getBlockY()
				,session.getLocation().getBlockZ()
				,Bukkit.getWorld(sign.getIDWorld())
				,sign.getOwner());
		return session.getLocation();
	}
    
}