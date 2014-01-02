package net.year4000.evotes;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import com.sk89q.commandbook.CommandBook;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eVotes", desc = "Players vote and get stuff.")
public class EVotes extends BukkitComponent implements Listener {

	private String component = "[eVotes]";
	public Logger logger = Logger.getLogger(component);
	private LocalConfiguration config;

    public void enable() {
        config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        logger.log(Level.INFO, component + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("item-id") public int itemId = 264;
    	@Setting("item-ammount") public int itemAmount = 2;
    	@Setting("exp-levers") public int expLevels = 10;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority=EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();
		String username = vote.getUsername();
		Player player = Bukkit.getPlayer(username);
		OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(username);
		if (!offlineplayer.isWhitelisted()) {
			offlineplayer.setWhitelisted(true);
		}
		if (player != null) {
			if (player.isOnline()) {
				ItemStack givenitems = new ItemStack(Material.getMaterial(config.itemId), config.itemAmount);
				player.getInventory().addItem(givenitems);
				player.updateInventory();
				player.giveExpLevels(config.expLevels);
			}
		}
		Bukkit.broadcastMessage(ChatColor.GOLD + username + " has voted for this server!");
    }
}