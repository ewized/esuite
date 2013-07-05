package net.year4000.eprotect;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;

public class ProtectEvents implements Listener{
	private Protected protect = new Protected();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
    	if(protect.isProtected(block, player)){
    		event.setCancelled(true);
    		protect.result = false;
    	}
    }

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        Action action = event.getAction();
        if(action==Action.RIGHT_CLICK_BLOCK){
        	if(protect.isProtected(block, player)){
        		event.setCancelled(true);
        		protect.result = false;
        	}
        }
    }

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for(Block block : event.blockList()){
        	if(protect.isProtected(block, null)){
        		event.setCancelled(true);
        		protect.result = false;
        		break;
        	}
        }
    }

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
    	if(protect.isProtected(block, null)){
    		event.setCancelled(true);
    		protect.result = false;
    	}
    }

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
	        if(event.getLine(0).equalsIgnoreCase("[Protect]")){
	        	try {
					CommandBook.inst().checkPermission(player, "eprotect.create.other");
					event.setLine(0, "[Protect]");
					if(event.getLine(1).equals("")){
						event.setLine(1, player.getName());
					}
				} catch (CommandPermissionsException e) {
		        	event.setLine(0, "[Protect]");
		        	event.setLine(1, player.getName());
				}
	        }
		}
    }
}
