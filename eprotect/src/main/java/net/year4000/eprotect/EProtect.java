package net.year4000.eprotect;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

@ComponentInformation(friendlyName = "eProtect", desc = "Protect block that you dont want others to break or interact with.")
public class EProtect extends BukkitComponent{

	private String component = "[eProtect]";
	
	
	public void enable() {
		Logger.getLogger(component).log(Level.INFO, component+" has been enabled.");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerInteractEvent event) {
    	
        Block clickedBlock = event.getClickedBlock();
        Material clickedBlockType = clickedBlock.getType();
        Material clickedItem = event.getPlayer().getItemInHand().getType();
        Action playerAction = event.getAction();
        Player player = event.getPlayer();
        
        if(clickedBlock != null && clickedBlockType == Material.ENDER_CHEST && playerAction == Action.RIGHT_CLICK_BLOCK){
        	if(clickedItem == Material.SIGN && (event.getBlockFace().toString()=="NORTH" || event.getBlockFace().toString()=="SOUTH" || event.getBlockFace().toString()=="EAST" || event.getBlockFace().toString()=="WEST")){
        		
                World world = event.getClickedBlock().getWorld();
                Location placeSign = clickedBlock.getLocation();
        		event.setCancelled(true);
        		Block signLoc = null;
        		Boolean pass = false;
        		
        		if(!player.getGameMode().toString().equalsIgnoreCase("Creative")){
        			player.getInventory().remove(clickedItem);
        		}

        		if(event.getBlockFace().toString() == "NORTH"){
        			signLoc = world.getBlockAt(placeSign.subtract(0, 0, 1));
        			if(signLoc.isEmpty()){
        				signLoc.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 2, true);
        				pass = true;
        			}
        		} else if (event.getBlockFace().toString() == "SOUTH"){
        			signLoc = world.getBlockAt(placeSign.add(0, 0, 1));
        			if(signLoc.isEmpty()){
        				signLoc.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 3, true);
        				pass = true;
        			}
        		} else if (event.getBlockFace().toString() == "WEST"){
        			signLoc = world.getBlockAt(placeSign.subtract(1, 0, 0));
        			if(signLoc.isEmpty()){
        				signLoc.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, true);
        				pass = true;
        			}
        		} else if (event.getBlockFace().toString() == "EAST"){
        			signLoc = world.getBlockAt(placeSign.add(1, 0, 0));
        			if(signLoc.isEmpty()){
        				signLoc.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 5, true);
        				pass = true;
        			}
        		}
        		
        		if(pass){
	        		Sign sign = (Sign) signLoc.getState();
	        		sign.setLine(0, "[Protect]");
	        		sign.setLine(1, event.getPlayer().getName());
	        		sign.update(true);
        		}
        	}
        }
	}
}
