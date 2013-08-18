package net.year4000.erespawn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eRespawn", desc = "Bring back classic death spawning.")
public class ERespawn extends BukkitComponent implements Listener{
	
	private LocalConfiguration config;
	private String component = "[eRespawn]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	
    public void enable() {
    	config = configure(new LocalConfiguration());
    	CommandBook.registerEvents(this);
    	registerCommands(Commands.class);
    	Logger.getLogger(component).log(Level.INFO, component+" version "+version+" has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
	
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("respawn-default-world") public boolean respawnDefaultWorld = true;
    	@Setting("bed-day") public boolean bedDay = true;
    }
    
    public class Commands{
    	@Command(aliases = {"home"}, usage = "", desc = "Teleport to your bed location.")
    	@CommandPermissions({"erespawn.home"})
        public void home(CommandContext args, CommandSender player){
    		Player p = (Player) player;
    		sendToBed(p);
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event){
    	Player p = event.getPlayer();
    	Location bl = p.getBedSpawnLocation();
    	Location l = event.getRespawnLocation();
    	
    	event.setRespawnLocation(setRespawn(p, bl, l));
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void setBed(PlayerInteractEvent event){
		Player p = event.getPlayer();
    	if(config.bedDay){
    		Block b = event.getClickedBlock();
    		Action a = event.getAction();

			if(b != null && b.getType() == Material.BED_BLOCK && a == Action.RIGHT_CLICK_BLOCK){
				Location l = b.getLocation();
				Location bl = p.getBedSpawnLocation();
				
				if(p.getWorld().getEnvironment() == Environment.NORMAL){
					if(p.getWorld().getTime() < 12500){
		    			p.setBedSpawnLocation(setBedSpawn(bl,l,p), true);
					}
				}
			}
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void overRideEnderPearl(PlayerInteractEvent event){
		Player p = event.getPlayer();
    	if(p.getItemInHand().getType() == Material.ENDER_PEARL){
			event.setUseItemInHand(Result.ALLOW);
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWake(PlayerBedLeaveEvent event){
		Player p = event.getPlayer();
		p.sendMessage(ChatColor.YELLOW + "You will respawn at this location, you may destory the bed.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSleep(BlockBreakEvent event){
    	if(config.bedDay){
			Block b = event.getBlock();
			Player p = event.getPlayer();
			
			if(b != null && b.getType() == Material.BED_BLOCK){
				Location bl = p.getBedSpawnLocation();
		    	double d = 3;
		    	try{
					if(bl != null) d = bl.distance(b.getLocation());
		    	} catch(IllegalArgumentException e){}
				if(d < 2.5){
					p.setBedSpawnLocation(b.getLocation(), true);
				}
			}
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event){
    	Player player = event.getPlayer();
    	TeleportCause teleportCause = event.getCause();
    	Location to = event.getTo();
    	Location from = event.getFrom();
    	
    	if(teleportCause == TeleportCause.ENDER_PEARL){
	    	double d = 2;
	    	try{
				 d = from.distance(to);
	    	} catch(Exception e){}
    		if(d < 2){
    			sendToBed(player);
    			event.setCancelled(true);
    		}
    	}
    }
    
    public void sendToBed(Player player){
    	Location bed = player.getBedSpawnLocation();
		Location spawn = player.getWorld().getSpawnLocation();
		
    	if(player.isSneaking()){
			player.teleport(spawn);
			player.sendMessage(ChatColor.YELLOW + "You have been sent to this world's spawn.");
		} else{
			if(bed != null){
				if(bed.getWorld() != player.getWorld()){
					player.sendMessage(ChatColor.YELLOW + "You need to be in the same world as your bed.");
				} else{
					player.teleport(bed);
	    			player.sendMessage(ChatColor.YELLOW + "You have been sent to your bed.");
				}
			} else{
				player.sendMessage(ChatColor.YELLOW + "You have not slept in a bed yet.");
			}
		}
    }
    
    public Location setBedSpawn(Location bl, Location l, Player p){
    	Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
		double d = 3;
		if(bl == null) bl = spawn;
		try{
			if(bl != null) d = bl.distance(l);
		} catch(IllegalArgumentException e){}
		if(d > 2.5){
			p.sendMessage(ChatColor.YELLOW + "You will respawn at this location, you may destory the bed.");
			return l;
		}
		p.sendMessage(ChatColor.YELLOW + "You will respawn at spawn.");
		return spawn;
    }
    
    public Location setRespawn(Player p, Location bl, Location l){
    	World w = Bukkit.getWorld(Bukkit.getWorlds().get(0).getName());
    	if(bl != null) return bl;
    	if(l != w && config.respawnDefaultWorld) return w.getSpawnLocation();
		return l;
    }
}