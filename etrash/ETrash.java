import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Attachable;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

@ComponentInformation(friendlyName = "eTrash", desc = "Turn caudrens into trash cans.")
public class ETrash extends BukkitComponent implements Listener {

	private String component = "[eTrash]";
	public Logger logger = Logger.getLogger(component);
	Set<BlockFace> blockFaces = EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

    public void enable() {
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, component + " has been enabled.");
    }

    public void reload() {
        super.reload();
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Block block = event.getClickedBlock();
    	Player player = event.getPlayer();
    	Inventory trash = Bukkit.createInventory(player, 27, "Trash Bin");
    	if (block != null) {
	    	if (block.getType() == Material.CAULDRON) {
	    		for (BlockFace blockface : blockFaces) {
	    			Block face = block.getRelative(blockface);
					if (face.getType() == Material.WALL_SIGN) {
						Sign sign = (Sign) face.getState();
						Attachable direction = (Attachable) sign.getData();
						BlockFace blockfacesign = direction.getAttachedFace().getOppositeFace();
						if (blockface.equals(blockfacesign)) {
							if (sign.getLine(1).equals("[Trash]")) {
								player.openInventory(trash);
							}
						}
					}
	    		}
	    	}
    	}
    }

}
