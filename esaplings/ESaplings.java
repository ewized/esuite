import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eSaplings", desc = "Save the trees!")
public class ESaplings extends BukkitComponent implements Listener {

    private String component = "[eSaplings]";
    public Logger logger = Logger.getLogger(component);

    public void enable() {
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, component + " has been enabled.");
    }

    public void reload() {
        super.reload();
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority=EventPriority.NORMAL)
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        Location location = event.getLocation();
        ItemStack material = event.getEntity().getItemStack();

        if (material.getType() == Material.SAPLING) {
            Block sapling = location.getWorld().getBlockAt(location);
            Block ground = location.getWorld().getBlockAt(location.subtract(0, 1, 0));

            if (ground.getType() == Material.DIRT || ground.getType() == Material.GRASS) {
                Random chance = new Random();
                if (chance.nextInt(2) == 1) {
                    MaterialData saplingType = material.getData();
                    sapling.setTypeIdAndData(saplingType.getItemTypeId(), saplingType.getData() , false);
                }
            }
        }
    }
}
