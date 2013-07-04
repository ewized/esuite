package net.year4000.eprotect;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

@ComponentInformation(friendlyName = "eProtect", desc = "Protect block that you don't want others to break or interact with.")
public class EProtect extends BukkitComponent{

	private String component = "[eProtect]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	public ProtectEvents protectevents = new ProtectEvents();
	
	public void enable() {
		CommandBook.registerEvents(protectevents);
		Logger.getLogger(component).log(Level.INFO, component+" version "+version+" has been enabled.");
	}
	
    public void reload() {
        super.reload();
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }  
    
}
