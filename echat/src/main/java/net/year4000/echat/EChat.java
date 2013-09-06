package net.year4000.echat;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

import org.bukkit.Bukkit;

@ComponentInformation(friendlyName = "eChat", desc = "Chat formatting with features.")
public class EChat extends BukkitComponent{

	private String component = "[eChat]";
	private Logger logger = Logger.getLogger(component);
	private String version = this.getClass().getPackage().getImplementationVersion();
	private static EChat instance;
	private EChatBungeeCord bungeeCord;
	private EChatConfig config;
	private EChatMessage message;
	private EChatSender sender;
	
	// Get the instance of EChat
	public EChat() {
        super();
        instance = this;
    }

	// Returns the instance of EChat.
    public static EChat inst() {
        return instance;
    }

    public void enable() {
    	// Give the other classes an instance of EChat.
    	config = configure(new EChatConfig());
    	sender = new EChatSender();
    	message = new EChatMessage();
    	bungeeCord = new EChatBungeeCord();
        CommandBook.registerEvents(message);
        
        // Send to other servers when you have BungeeCord enabled.
        if (config.bungeecord) {
        	Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(CommandBook.inst(), "BungeeCord");
        	Bukkit.getServer().getMessenger().registerIncomingPluginChannel(CommandBook.inst(), "BungeeCord", bungeeCord);
        }
        logger.log(Level.INFO, component + " version " + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }
    
	// Returns the EChatConfig instance.
    public EChatConfig getEChatConfig() {
    	return this.config;
    }
    
	// Returns the EChatBungeeCord instance.
    public EChatBungeeCord getEChatBungeeCord() {
    	return this.bungeeCord;
    }
    
	// Returns the EChatMessage instance.
    public EChatMessage getEChatMessage() {
    	return this.message;
    }
    
    // Returns the EChatSender instance.
    public EChatSender getEChatSender() {
    	return this.sender;
    }
}