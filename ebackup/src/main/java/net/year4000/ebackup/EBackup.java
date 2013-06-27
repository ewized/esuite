package net.year4000.ebackup;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;


@ComponentInformation(friendlyName = "eBackup", desc = "Save your worlds from what could be the downfall of them.")
public class EBackup extends BukkitComponent{
	
	private LocalConfiguration config;
	private String component = "[eBackup]";
    
    public void enable() {
    	config = configure(new LocalConfiguration());
    	registerCommands(Commands.class);
        Logger.getLogger(component).log(Level.INFO, component+" has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
    
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("save-time") public int saveTime = 50;
    	@Setting("backup-name") public int backupTime = 50;
    }
    
    public class Commands{
		
    	@Command(aliases = {"backup"}, usage = "",
                desc = "Backup your worlds.")
    	 @CommandPermissions({"ebackup.backup"})
        public void backup(CommandContext args, CommandSender player){
    		player.sendMessage(ChatColor.GOLD + "How are you today?");
    	}

    }
    

}