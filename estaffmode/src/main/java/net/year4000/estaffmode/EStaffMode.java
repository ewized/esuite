package net.year4000.estaffmode;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eStaffMode", desc = "Staff member can switch to their staff mode with permissions.")
@Depend(components = SessionComponent.class)
public class EStaffMode extends BukkitComponent implements Listener {
	
	private PermissionManager pex = PermissionsEx.getPermissionManager();
	private LocalConfiguration config;
	private String component = "[eStaffMode]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	@InjectComponent private SessionComponent sessions;
	
	
	public void enable() {
		config = configure(new LocalConfiguration());
		registerCommands(Commands.class);
		CommandBook.registerEvents(this);
		Logger.getLogger(component).log(Level.INFO, component+" version "+version+" has been enabled.");
	}
	
    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
    
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("staff-group") public String staffGroup = "StaffMode";
    	@Setting("op-group") public String opGroup = "OPMode";
    	@Setting("set-op") public Boolean setOP = false;
    }
	
	public class Commands{
		@Command(aliases = { "staffmode", "sm" }, desc = "Controls what mode you are in.", usage = "[on|off] [GROUP]", min = 1, max = 2)
		@CommandPermissions(value = { "estaffmode.staff", "estaffmode.op" })
		public void staffMode(CommandContext args, CommandSender p) throws CommandPermissionsException{
			Player player = Bukkit.getPlayer(p.getName());
			if(args.getString(0).equalsIgnoreCase("on")){
				if(args.argsLength()==2){
					if(args.getString(1).equalsIgnoreCase(config.staffGroup)){
						CommandBook.inst().checkPermission(player, "estaffmode.staff");
						setStaffMode(player,config.staffGroup);
					} else if(args.getString(1).equalsIgnoreCase(config.opGroup)){
						CommandBook.inst().checkPermission(player, "estaffmode.op");
						setStaffMode(player,config.opGroup);
					}
				} else{
					CommandBook.inst().checkPermission(player, "estaffmode.staff");
					setStaffMode(player,config.staffGroup);
				}
			} else if(args.getString(0).equalsIgnoreCase("off")){
				removeStaffMode(player, false);
			} else{
				player.sendMessage(ChatColor.RED + "Incorrect usage.");
				player.sendMessage(ChatColor.RED + "/staffmode [on|off] [GROUP]");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event){
    	Player player = event.getPlayer();
    	EStaffModeSession session = sessions.getSession(EStaffModeSession.class, player);
    	if(player.hasPlayedBefore() && session.getStaffMode()){
    		removeStaffMode(player, true);
    	}
    }
    
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event){
    	Player player = event.getPlayer();
    	EStaffModeSession session = sessions.getSession(EStaffModeSession.class, player);
    	if(session.getStaffMode()){
    		removeStaffMode(player, true);
    	}
    }
    
    public void setStaffMode(Player player, String group) throws CommandPermissionsException{
    	try{
    		EStaffModeSession session = sessions.getSession(EStaffModeSession.class, player);
    		if(!session.getStaffMode()){
    			if(group.equalsIgnoreCase(config.opGroup) || config.setOP){
    				player.setOp(true);
    			}
    	    	pex.getUser(player.getName()).addGroup(group);
    	    	pex.resetUser(player.getName());
    			session.setStaffGroup(group);
    			session.setStaffMode(true);
    	    	player.sendMessage(ChatColor.YELLOW +  "You are now set in staff mode.");
    		} else{
    			player.sendMessage(ChatColor.RED +  "You are all ready in staff mode.");
    		}
		} catch(ClassCastException e){
			player.sendMessage(ChatColor.RED +  "You are not a player.");
		}
    }
    
    public void removeStaffMode(Player player, Boolean slient){
    	try{
    		EStaffModeSession session = sessions.getSession(EStaffModeSession.class, player);
    		if(session.getStaffMode()){
    			if(session.getStaffGroup().equalsIgnoreCase(config.opGroup) || config.setOP)player.setOp(false);
    	    	pex.getUser(player.getName()).removeGroup(session.getStaffGroup());
    	    	pex.resetUser(player.getName());
    			session.setStaffGroup("");
    			session.setStaffMode(false);
    			player.setGameMode(Bukkit.getDefaultGameMode());
    			if(!player.getGameMode().equals(GameMode.CREATIVE))player.setFlying(false);
    	    	if(!slient)player.sendMessage(ChatColor.YELLOW + "You are set back to normal staff.");
    		} else{
    			if(!slient)player.sendMessage(ChatColor.RED + "You are not in staff mode.");
    		}
		} catch(ClassCastException e){
			player.sendMessage(ChatColor.RED + "You are not a player.");
		}
    }
}