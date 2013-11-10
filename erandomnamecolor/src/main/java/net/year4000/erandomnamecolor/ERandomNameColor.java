package net.year4000.erandomnamecolor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

@ComponentInformation(friendlyName = "eRandomNameColor",
        desc = "When players login their username will be a random color.")
public class ERandomNameColor extends BukkitComponent implements Listener {

    private String component = "[eRandomNameColor]";
    private String version =
            this.getClass().getPackage().getImplementationVersion();
    private Logger logger = Logger.getLogger(component);
    private LocalConfiguration config;
    private Scoreboard scoreboard;

    public void enable() {
        config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        logger.log(Level.INFO, component
                + " version " + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    private static class LocalConfiguration extends ConfigurationBase {
        @Setting("random-color-name") public Boolean randomColorName = true;
        @Setting("tablist-name") public Boolean tabName = true;
        @Setting("tag-name") public Boolean tagName = true;
        @Setting("colors") public String colors = "2 3 4 5 6 9 a b c d e f";
    }

    // Sets the color of the player upon login.
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getDisplayName();
        ArrayList<String> vars = new ArrayList<String>();
        String colors = config.colors + " ";
        Collections.addAll(vars, colors.split(" "));
        Random rnd = new Random();
        int index = rnd.nextInt(vars.size());
        String color = vars.get(index);
        String namec = ChatColor.getByChar(color).toString() + name;

        // Set the player's chat display name.
        if (config.randomColorName) {
            player.setDisplayName(namec);
        }

        // Sets the player tab list name.
        if (config.tabName && name.length() < 14) {
            player.setPlayerListName(namec);
        }

        // Set the player tag color name.
        if (config.tagName && name.length() < 14) {
            Team team = scoreboard.getTeam(name);
            if (team == null) {
                team = scoreboard.registerNewTeam(name);
            }
            team.addPlayer(player);
            team.setPrefix(ChatColor.getByChar(color).toString());
            player.setScoreboard(scoreboard);
        }
    }
}
