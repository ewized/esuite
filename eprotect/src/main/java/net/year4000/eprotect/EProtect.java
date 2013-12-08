package net.year4000.eprotect;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

@ComponentInformation(friendlyName = "eProtect",
        desc = "Protect everything from other players.")
public class EProtect extends BukkitComponent {

    private final String component = "[eProtect]";
    private final Logger logger = Logger.getLogger(component);
    private final String version =
            this.getClass().getPackage().getImplementationVersion();

    public void enable() {
        CommandBook.registerEvents(new ProtectEvents());
        logger.log(Level.INFO, component + " version "
                + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        logger.log(Level.INFO, component + " has been reloaded.");
    }
}
