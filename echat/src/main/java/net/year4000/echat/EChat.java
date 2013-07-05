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
import org.bukkit.entity.Player;
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
	private ChatThread chatThread;
	private LocalConfiguration config;
	private String component = "[eChat]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	private String player;
	private String displayName;
	private String message;
	private String world;
	private String[] group;
	private String prefix;
	private String suffix;
	
    
    public void enable() {
    	config = configure(new LocalConfiguration());
    	chatThread = new ChatThread();
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
    }
    
    public class ChatThread extends Thread{
    	public void sendChat(String msg){
    		msg = replaceColor(msg);
    		
    		for(Player p : Bukkit.getOnlinePlayers()){
    			p.sendMessage(msg);
    		}
    		Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(msg));
    	}
    	public void chat(AsyncPlayerChatEvent event){
        	player = event.getPlayer().getName();
        	world = event.getPlayer().getWorld().getName();
        	message = event.getMessage();
        	displayName = event.getPlayer().getDisplayName();
        	group = wepif.getGroups(event.getPlayer());
        	prefix = pex.getUser(event.getPlayer()).getPrefix();
        	suffix = pex.getUser(event.getPlayer()).getSuffix();
        	
	    	sendChat(formatChat(config.chatFormat));
    	}
    	public String formatChat(String msgformat){
        	msgformat = msgformat.replace("%player%",player);
        	msgformat = msgformat.replace("%displayname%",displayName);
        	msgformat = msgformat.replace("%message%",message);
        	msgformat = msgformat.replace("%world%",world);
        	msgformat = msgformat.replace("%group%",group[0]);
        	msgformat = msgformat.replace("%prefix%",prefix);
        	msgformat = msgformat.replace("%suffix%",suffix);

        	return msgformat;
        }
    	public String replaceColor(String msg){
    		for(ChatColor c : ChatColor.values()){
    			msg = msg.replaceAll("&"+c.getChar(),c.toString()); 
        	}
    		return msg;
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
    	chatThread.chat(event);
    	event.setCancelled(true);
    }

}