package net.year4000.echat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.wepif.PermissionsResolverManager;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@ComponentInformation(friendlyName = "eChat", desc = "Chat formatting with features.")
public class EChat extends BukkitComponent implements Listener {
	
	private PermissionManager pex = PermissionsEx.getPermissionManager();
	private PermissionsResolverManager wepif = PermissionsResolverManager.getInstance();
	private LocalConfiguration config;
	private String component = "[eChat]";
	private String player;
	private String displayName;
	private String message;
	private String world;
	private String[] group;
	private String prefix;
	private String suffix;
	
    
    public void enable() {
    	config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        Logger.getLogger(component).log(Level.INFO, component+" has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
	
    public static class LocalConfiguration extends ConfigurationBase {
        @Setting("chat-format") public String chatFormat = "<%player%> %message%";
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
    	if(event.isCancelled())return;
    	
    	player = event.getPlayer().getName();
    	world = event.getPlayer().getWorld().getName();
    	message = event.getMessage();
    	displayName = event.getPlayer().getDisplayName();
    	group = wepif.getGroups(event.getPlayer());
    	prefix = pex.getUser(event.getPlayer()).getPrefix();
    	suffix = pex.getUser(event.getPlayer()).getSuffix();
    	
    	event.setFormat(replaceVars(config.chatFormat));
    }
    
    public String replaceVars(String msgformat){
    	msgformat = msgformat.replace("%player%",player);
    	msgformat = msgformat.replace("%displayname%",displayName);
    	msgformat = msgformat.replace("%message%",message);
    	msgformat = msgformat.replace("%world%",world);
    	msgformat = msgformat.replace("%group%",group[0]);
    	msgformat = msgformat.replace("%prefix%",prefix);
    	msgformat = msgformat.replace("%suffix%",suffix);
    	msgformat = msgformat.replaceAll("&r",ChatColor.RESET.toString());
    	
    	ArrayList<String> vars = new ArrayList<String>();
    	Collections.addAll(vars, "&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&k", "&l", "&o", "&n", "&m");
    	
    	for(String index : vars)msgformat = msgformat.replaceAll(index,ChatColor.getByChar(index.substring(1)).toString());

    	return msgformat;
    }  
}