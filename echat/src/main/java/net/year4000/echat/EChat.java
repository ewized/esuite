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
	
	private PermissionsResolverManager wepif = PermissionsResolverManager.getInstance();
	private LocalConfiguration config;
	private String component = "[eChat]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	private String player;
	private String displayName;
	private String message;
	private String world;
	private String[] group;
	
    
    public void enable() {
    	config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        Logger.getLogger(component).log(Level.INFO, component+" version "+version+" has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
	
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("chat-format") public String chatFormat = "<%player%> %message%";
    	@Setting("groups.group.prefix") public String groupPrefix = "groupprefix";
    	@Setting("groups.group.suffix") public String groupSuffix = "groupprefix";
    }
    
    public String getOption(String group, String option){
    	Object object = this.getRawConfiguration().getProperty("groups." + group + "." + option);
    	if(object != null){
    		return object.toString();
    	}
    	return option;
    }
    
	public void sendChat(String msg){
		for(Player p : Bukkit.getOnlinePlayers()){
			String pname = p.getName();
			String nameMin = pname.substring(0, 3);
			if(message.contains(nameMin) && p.getName().contains(nameMin)){
				p.getWorld().playSound(p.getLocation(), Sound.NOTE_PLING, 1, 0);
				msg = ChatColor.RED +""+ ChatColor.ITALIC + ChatColor.stripColor(msg);
			}
			p.sendMessage(msg);
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(msg));
	}

	public String formatChat(String msgformat, Player players){
    	msgformat = msgformat.replace("%player%",player);
    	msgformat = msgformat.replace("%displayname%",displayName);
    	msgformat = msgformat.replace("%world%",world);
    	msgformat = msgformat.replace("%group%",group[0]);
    	msgformat = msgformat.replace("%prefix%", getOption(group[0],"prefix"));
    	msgformat = msgformat.replace("%suffix%", getOption(group[0],"suffix"));
    	msgformat = replaceColor(msgformat);
    	if(CommandBook.inst().hasPermission(players, "echat.colors")){
    		msgformat = replaceColor(msgformat.replace("%message%",message));
    	} else{
        	msgformat = msgformat.replace("%message%",message);
    	}
    	return msgformat;
    }
	
	public String replaceColor(String msg){
		for(ChatColor c : ChatColor.values()){
			msg = msg.replaceAll("&"+c.getChar(),c.toString()); 
    	}
		return msg;
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
    	player = event.getPlayer().getName();
    	world = event.getPlayer().getWorld().getName();
    	message = event.getMessage();
    	displayName = event.getPlayer().getDisplayName();
    	group = wepif.getGroups(event.getPlayer());
    	
    	sendChat(formatChat(config.chatFormat,event.getPlayer()));
    	event.setCancelled(true);
    }

}