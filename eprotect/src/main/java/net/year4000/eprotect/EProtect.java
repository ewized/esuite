package net.year4000.eprotect;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eProtect", desc = "Protect block that you dont want others to break or interact with.")
public class EProtect extends BukkitComponent{

	private String component = "[eProtect]";
	public LocalConfiguration config;
	public ProtectEvents protectevents = new ProtectEvents();
	
	public void enable() {
		config = configure(new LocalConfiguration());
		CommandBook.registerEvents(protectevents);
		Logger.getLogger(component).log(Level.INFO, component+" has been enabled.");
	}
	
    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
    
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("sign-name") public String signName = "Protect";
    }
    
    
}
