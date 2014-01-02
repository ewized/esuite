import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.ChatColor;

@ComponentInformation(friendlyName = "ePlates", desc = "Minor tweaks to pressure plates.")
public class EPlates extends BukkitComponent implements Listener {

    final String COMPONENT = "[ePlates]";
    final String VERSION = "1.0";
    Logger logger = Logger.getLogger(COMPONENT);

    public void enable() {
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, COMPONENT + " version " + VERSION + " has been enabled.");
    }

    public void reload() {
        super.reload();
        logger.log(Level.INFO, COMPONENT + " has been reloaded.");
    }

    // Then sets the message of the day with various vars.
    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();
        Block underBlock = block.getRelative(BlockFace.DOWN);
        Boolean results = false;

        // Check if the pressure plate is right.
        switch (block.getType()) {
            case WOOD_PLATE:
            case STONE_PLATE:
                results = true;
                break;
        }

        // Check if the plate is on top of the right block.
        switch (underBlock.getType()) {
            case OBSIDIAN:
                results = true;
                break;
        }

        event.setCancelled(results);
    }
}
