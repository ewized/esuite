import java.util.*;
import java.util.logging.*;
import java.text.*;
import java.util.concurrent.*;
import java.lang.management.ManagementFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

@ComponentInformation(friendlyName = "eRamTweaks", desc = "Get back your server's performance.")
@Depend(components = SessionComponent.class)
public class ERamTweaks extends BukkitComponent implements Listener {

	private String component = "[eRamTweaks]";
	private Logger logger = Logger.getLogger(component);

	public void enable() {
		registerCommands(Commands.class);
		CommandBook.registerEvents(this);
		unloadChunks();
        long l1 = Runtime.getRuntime().totalMemory();
        long l2 = Runtime.getRuntime().maxMemory();
        logger.log(Level.INFO, component + " Memory max: " + l2 + " bytes");
        logger.log(Level.INFO, component + " Memory total: " + l1 + " bytes");
        logger.log(Level.INFO, component + " has been enabled.");
	}

    public void reload() {
        super.reload();
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
    	World w = event.getWorld();
    	w.setKeepSpawnInMemory(false);
    }

    public void unloadChunks() {
		for (World w : Bukkit.getWorlds()) {
			for(Chunk c : w.getLoadedChunks()) {
				c.unload(true, false);
			}
		}
	}

	public class Commands{
        @Command(aliases = { "uptime" }, desc = "Get the uptime of the server.")
        @CommandPermissions(value = {"eramtweaks.uptime"})
		public void uptime(CommandContext args, CommandSender p) throws CommandPermissionsException {
            long starttime = ManagementFactory.getRuntimeMXBean().getStartTime();
            long uptime = System.currentTimeMillis() - starttime;
            long days = TimeUnit.MILLISECONDS.toDays(uptime);
            long hours = TimeUnit.MILLISECONDS.toHours(uptime) - days;
            long minutes = TimeUnit.HOURS.toMinutes(hours) - hours;
            long seconds = TimeUnit.MINUTES.toSeconds(minutes) - minutes;

            // Format the time
            String time = "";
            if (days > 0)
                time += days + "d:";
            if (hours > 0)
                time += hours + "h:";
            if (minutes > 0)
                time += minutes + "m:";    
            time += seconds + "s";
/*
            Date date = new Date(uptime);
            DateFormat formatter = new SimpleDateFormat("W:d:HH:mm:ss");
            String time = formatter.format(date);
*/
            p.sendMessage(ChatColor.GOLD + "Uptime: " + ChatColor.YELLOW + time);
        }

		@Command(aliases = { "unload" }, desc = "Unloads all loaded chunks.", flags = "d")
		@CommandPermissions(value = {"eramtweaks.unload"})
		public void unloadChunks(CommandContext args, CommandSender p) throws CommandPermissionsException {
			Bukkit.broadcastMessage(ChatColor.DARK_RED + "NOTICE: "
					+ ChatColor.GOLD + "Unloading server chunks, Don't build untill its OK!");
			int totalChunks = 0;
			int totalWorlds = 0;

			for  (World w : Bukkit.getWorlds()) {
				totalWorlds++;

				for (Chunk c : w.getLoadedChunks()) {
					totalChunks++;
					c.unload(true, true);

					if (args.hasFlag('d')) {
						p.sendMessage(ChatColor.GREEN + "Unloading: " + ChatColor.GRAY + c + " in " + w.getName());
					}
				}
			}

			Bukkit.broadcastMessage(ChatColor.DARK_RED + "NOTICE: "
					+ ChatColor.GOLD + "It's OK, you may resume your work.");
			p.sendMessage(ChatColor.GREEN + "The server has unloaded " + ChatColor.DARK_GREEN 
					+ totalChunks + ChatColor.GREEN + " chunk(s) from " + ChatColor.DARK_GREEN 
					+ totalWorlds + ChatColor.GREEN + " world(s).");
		}
		
		@Command(aliases = { "chunks", "mem" }, desc = "Get data on each loaded world.")
		@CommandPermissions(value = { "eramtweaks.chunks"})
		public void loadedChunks(CommandContext args, CommandSender p) throws CommandPermissionsException {
			int totalChunks = 0;
			int totalEnties = 0;
			int totalTitleEnties = 0;

			for (World w : Bukkit.getWorlds()) {
				int worldChunks = 0;
				int worldEnties = 0;
				int worldTitleEnties = 0;

				for (Chunk c : w.getLoadedChunks()) {
					worldChunks++;
					totalChunks++;

					for(@SuppressWarnings("unused")Entity e: c.getEntities()){
						worldEnties++;
						totalEnties++;
					}

					for(@SuppressWarnings("unused")BlockState e: c.getTileEntities()){
						worldTitleEnties++;
						totalTitleEnties++;
					}
				}

				p.sendMessage(ChatColor.YELLOW + w.getEnvironment().name()+ ": " + ChatColor.GOLD + w.getName()
						+ ChatColor.YELLOW + " chunks: " + ChatColor.GOLD + worldChunks
						+ ChatColor.YELLOW + " enties: " + ChatColor.GOLD + worldEnties
						+ ChatColor.YELLOW + " title enties: " + ChatColor.GOLD + worldTitleEnties);
			}

			p.sendMessage(ChatColor.YELLOW + "Total:"
					+ ChatColor.YELLOW + " chunks: " + ChatColor.GOLD + totalChunks
					+ ChatColor.YELLOW + " enties: " + ChatColor.GOLD + totalEnties
					+ ChatColor.YELLOW + " title enties: " + ChatColor.GOLD + totalTitleEnties);
		}
	}
}
