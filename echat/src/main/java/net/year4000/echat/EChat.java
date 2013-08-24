package net.year4000.echat;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.wepif.PermissionsResolverManager;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@ComponentInformation(friendlyName = "eChat", desc = "Chat formatting with features.")
public class EChat extends BukkitComponent implements Listener {

	private String component = "[eChat]";
	private Logger logger = Logger.getLogger(component);
	private String version = this.getClass().getPackage().getImplementationVersion();
	private PermissionsResolverManager wepif = PermissionsResolverManager.getInstance();
	private LocalConfiguration config;
	private String player;
	private String displayName;
	private String message;
	private String world;
	private String[] group;


    public void enable() {
    	config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, component + " version " + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("chat-format") public String chatFormat = "<%player%> %message%";
    	@Setting("groups.group.prefix") public String groupPrefix = "groupprefix";
    	@Setting("groups.group.suffix") public String groupSuffix = "groupprefix";
    }

    public String getOption(String group, String option) {
    	Object config = getRawConfiguration().getProperty("groups." + group + "." + option);
    	if (config != null) {
    		return config.toString();
    	}
    	return option;
    }

	public void sendChat(String players, String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (checkName(p, msg)) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 0);
				p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + ChatColor.stripColor(formatChatMessage(players, msg, player)));
			} else {
				p.sendMessage(formatChatMessage(players, msg, player));
			}
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(formatChatMessage(players, msg, player)));
	}

	public boolean checkName(Player sender, String msg) {
		for (String word : msg.split(" ")) {
			if (word.length() > 2 && word.length() <= sender.getName().length()) {
				word = word.toLowerCase();
				if (word.startsWith(sender.getName().substring(0, word.length()-1).toLowerCase())) {
					if (!player.contains(word)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String formatChat(String msgformat, Player players) {
    	msgformat = msgformat.replace("%player%", player);
    	msgformat = msgformat.replace("%displayname%", displayName);
    	msgformat = msgformat.replace("%world%", world);
    	msgformat = msgformat.replace("%group%", group[0]);
    	msgformat = msgformat.replace("%prefix%", getOption(group[0],"prefix"));
    	msgformat = msgformat.replace("%suffix%", getOption(group[0],"suffix"));
    	msgformat = replaceColor(msgformat);

    	return msgformat;
    }

	public String formatChatMessage(String msgformat, String msg, String players) {
		Player p = Bukkit.getPlayer(players);
    	if (CommandBook.inst().hasPermission(p, "echat.colors")) {
    		msgformat = replaceColor(msgformat.replace("%message%", msg));
    	} else {
        	msgformat = msgformat.replace("%message%", msg);
    	}
    	return msgformat;
    }

	public String replaceColor(String msg) {
		for (ChatColor c : ChatColor.values()) {
			msg = msg.replaceAll("&" + c.getChar(), c.toString()); 
    	}
		return msg;
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
    	player = event.getPlayer().getName();
    	world = event.getPlayer().getWorld().getName();
    	message = event.getMessage();
    	displayName = event.getPlayer().getDisplayName();
    	group = wepif.getGroups(event.getPlayer());

    	sendChat(formatChat(config.chatFormat, event.getPlayer()), message);
    	event.setCancelled(true);
    }

}