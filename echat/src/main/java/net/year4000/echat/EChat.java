package net.year4000.echat;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.Messenger;

@ComponentInformation(friendlyName = "eChat",
        desc = "Chat formatting with features.")
public class EChat extends BukkitComponent {

	private final String component = "[eChat]";
	private Logger logger = Logger.getLogger(component);
	private final String version =
            this.getClass().getPackage().getImplementationVersion();
	private static EChat instance;
	private Configuration configuration;

    /**
     * Get the instance of EChat
     */
    public EChat() {
        super();
        instance = this;
    }

    /**
     * Returns the instance of EChat.
     *
     * @return EChat instance.
     */
    public static EChat inst() {
        return instance;
    }

    public void enable() {
        // Give the other classes an instance of EChat.
        configuration = configure(new Configuration());
        CommandBook.registerEvents(new ChatListener());

        // Send to other servers when you have BungeeCord enabled.
        if (configuration.bungeecord) {
            Messenger messenger = Bukkit.getServer().getMessenger();
            messenger.registerOutgoingPluginChannel(CommandBook.inst(),
                    "BungeeCord");
            messenger.registerIncomingPluginChannel(CommandBook.inst(),
                    "BungeeCord", new BungeeCord());
        }
        logger.log(Level.INFO, component + " version " + version
                + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(configuration);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    /**
     * Gets the instance of the Configuration class.
     *
     * @return Configuration instance
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }
}
