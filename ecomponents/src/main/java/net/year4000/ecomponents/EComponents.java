package net.year4000.ecomponents;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.ComponentManager;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

@ComponentInformation(friendlyName = "eComponents", desc = "Shows the components that are running in this server.")
public class EComponents extends BukkitComponent{

	private String component = "[eComponents]";
	
	
	public void enable() {
		registerCommands(Commands.class);
		Logger.getLogger(component).log(Level.INFO, component+" has been enabled.");
	}
    
    public void reload() {
    	super.reload();
    	Logger.getLogger(component).log(Level.INFO, component+" has been reloaded.");
    }
    
	public class Commands{
		@Command(aliases = {"components", "comp"}, desc = "Gets a list of plugins/components running on this server.")
		@CommandPermissions({"ecomponents.components"})
		public void components(CommandContext args, CommandSender player){
			//Bukkit Plugins
			/*
			PluginManager plugins = Bukkit.getPluginManager();
			String pluginNames = ",plugins";
			for(Plugin plugin : plugins.getPlugins()){
				ChatColor componentColor;
				if(plugin.isEnabled()){
					componentColor = ChatColor.GREEN;
				} else{
					componentColor = ChatColor.RED;
				}
				pluginNames = pluginNames + ", " + componentColor + plugin.getName() + ChatColor.RESET;
			}
			String pluginPrefix = "Plugins (" + plugins.getPlugins().length + "):";
			player.sendMessage(pluginNames.replaceFirst(",plugins,", pluginPrefix));
			*/
			//CommandBook Components
			ComponentManager<BukkitComponent> components = CommandBook.inst().getComponentManager();
			String componentNames = ",components";
			for(BukkitComponent component : components.getComponents()){
				ChatColor componentColor;
				if(component.isEnabled()){
					componentColor = ChatColor.GREEN;
				} else{
					componentColor = ChatColor.RED;
				}
				componentNames = componentNames + ", " + componentColor + component.getInformation().friendlyName() + ChatColor.RESET;
			}
			String componentPrefix = "Components (" + components.getComponents().size() + "):";
			player.sendMessage(componentNames.replaceFirst(",components,", componentPrefix));
		}
		@Command(aliases = {"componentmanager", "compmana"}, usage = "[enable|disable|reload] [component]", desc = "Enable, Disable, Reload Components", min = 2, max = 2)
		@CommandPermissions({"ecomponents.manage"})
		public void componentmanager(CommandContext args, CommandSender player){
			if(args.argsLength()==2){
				ComponentManager<BukkitComponent> components = CommandBook.inst().getComponentManager();
				if(args.getString(0).equalsIgnoreCase("enable")){
					try{
						components.getComponent("eChat").enable();
						player.sendMessage(ChatColor.GREEN + args.getString(1) + " is enabled.");
					} catch (Exception e){
						player.sendMessage(ChatColor.RED + args.getString(1) + " is not a valid component.");
					}
				} else if(args.getString(0).equalsIgnoreCase("disable")){
					try{
						components.getComponent("eChat").disable();
						player.sendMessage(ChatColor.RED + args.getString(1) + " is disabled.");
					} catch (Exception e){
						player.sendMessage(ChatColor.RED + args.getString(1) + " is not a valid component.");
					}
				} else if(args.getString(0).equalsIgnoreCase("reload")){
					try{
						components.getComponent(args.getString(1)).reload();
						player.sendMessage(ChatColor.GOLD + args.getString(1) + " is reloaded.");
					} catch (Exception e){
						player.sendMessage(ChatColor.RED + args.getString(1) + " is not a valid component.");
					}
				}
			}
		}
	}
}
