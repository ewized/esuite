package net.year4000.efriends;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

import com.sk89q.minecraft.util.commands.*;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

@ComponentInformation(
    friendlyName = "eFriends",
    desc = "Create groups of friends."
)
public class EFriends extends BukkitComponent {
    private String component = "[eFriends]";
    private String version = this.getClass().getPackage().getImplementationVersion();
    private Logger logger = Logger.getLogger(component);
    private Configuration config;
    private static EFriends instance;
    private Connection connection;

    /**
     * Set up the instance of this class.
     */
    public EFriends() {
        super();
        instance = this;
    }

    /**
     * Get the instance of this class.
     */
    public static EFriends inst() {
        return instance;
    }

    /**
     * Enable the component.
     */
    public void enable() {
        config = configure(new Configuration());
        registerCommands(Commands.class);
        setupDatabase();
        logger.log(Level.INFO, component + " version " + version + " has been enabled.");
    }

    /**
     * Reload the component.
     */
    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    /**
     * Set up the database.
     */
    public void setupDatabase() {
        // Gets the data from the database.
        try {
            connection = DriverManager.getConnection(config.sqlURL, config.sqlUser, config.sqlPassword);
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS efriends(`owner` VARCHAR(16), `list` VARCHAR(32), `member` VARCHAR(16)) DEFAULT CHARACTER SET utf8");
        }
        catch (SQLException e) {
            logger.log(Level.WARNING, component + "  " + e);
            //this.disable();
        }
    }

    /**
     * Get the database connection;
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Get the class for group.
     *
     * @param group The name of the group.
     * @return The instance of the group.
     */
    public Group getGroup(String group) {
        return new Group(group);
    }

    public class Configuration extends ConfigurationBase {
        @Setting("sql-url") public String sqlURL = "jdbc:mysql://localhost:3306/database";
        @Setting("sql-user") public String sqlUser = "root";
        @Setting("sql-password") public String sqlPassword = "password";
    }

    public class Commands {
        private CommandSender sender;

        /**
         * The main command.
         */
        @Command(
            aliases = {"friends"},
            usage = "[create|remove] [list]",
            desc = "List the friends in the list.",
            min = 0,
            max = 4
        )
        @CommandPermissions({"efriends.use"})
        public void friends(CommandContext args, CommandSender sender) {
            this.sender = sender;

            switch (args.argsLength()) {
                case 3:
                    if (args.getString(1).equalsIgnoreCase("add"))
                        addPlayer(args.getString(0), args.getString(2));
                    else if (args.getString(1).equalsIgnoreCase("remove"))
                        removePlayer(args.getString(0), args.getString(2));
                    break;
                case 2:
                    if (args.getString(0).equalsIgnoreCase("create"))
                        createGroup(sender.getName(), args.getString(1));
                    else if (args.getString(0).equalsIgnoreCase("remove"))
                        removeGroup(sender.getName(), args.getString(1));
                    else if (args.getString(0).equalsIgnoreCase("list"))
                        listPlayers(args.getString(1));
                    else
                        sender.sendMessage(ChatColor.GOLD
                                + "Too few arguments. "
                                + "Try /friends [list] [add|remove] "
                                + "[player]");
                    break;
                case 1:
                    sender.sendMessage(ChatColor.GOLD + "Too few arguments. "
                            + "Try /friends [create|remove|list] [list]");
                    break;
                default:
                    listGroups(sender.getName());
                    break;
            }
        }

        /**
         * List the command sends groups.
         */
        private void listGroups(String player) {
            List<String> groups = new Groups(player).getGroups();

            // Check if the player has groups
            if (groups.size() != 0)
                sender.sendMessage(ChatColor.GOLD + "Your lists of friends:");
            else
                sender.sendMessage(ChatColor.GOLD
                    + "You don't have any lists of friends. "
                    + "Type '/friends create (name)' then "
                    + "'/friends (name) add (player)'");

            for (int i=0; i < groups.size(); i++)
                sender.sendMessage(ChatColor.GRAY + "  " + groups.get(i));
        }

        /**
         * Create a group and add it to the player.
         */
        public void createGroup(String player, String group) {
            Groups groups = new Groups(player);
            groups.add(group);
            sender.sendMessage(ChatColor.GOLD
                    + "Added group named '" + group + "'.");
        }

        /**
         * Remove a group from the player.
         */
        public void removeGroup(String player, String group) {
            Groups groups = new Groups(player);
            groups.remove(group);
            sender.sendMessage(ChatColor.GOLD
                    + "Removed group named '" + group + "'.");
        }

        /**
         * List the players in the group.
         */
        public void listPlayers(String group) {
            List<String> players = new Group(group).getMembers();

            // Check if the player has groups
            if (players.size() != 0)
                sender.sendMessage(ChatColor.GOLD
                        + "Your friends in '" + group + "':");
            else
                sender.sendMessage(ChatColor.GOLD
                    + "You don't have any friends in '" + group + "'. "
                    + "'/friends (list) add (player)'");

            for (int i=0; i < players.size(); i++)
                sender.sendMessage(ChatColor.GRAY + "  " + players.get(i));
            
        }

        /**
         * Add a player to a group.
         */
        public void addPlayer(String group, String player) {
            Group current = new Group(group);
            current.add(player);
            sender.sendMessage(ChatColor.GOLD
                    + "Added '" + player + "' to '" + group + "'.");
        }

        /**
         * Remove a player from a group.
         */
        public void removePlayer(String group, String player) {
            Group current = new Group(group);
            current.remove(player);
            sender.sendMessage(ChatColor.GOLD
                    + "Removed '" + player + "' from '" + group + "'.");
        }

    }

    public class Groups {
        private List<String> groups = new ArrayList<String>();
        private List<String> remove = new ArrayList<String>();
        private String player;
        private Connection connection = EFriends.inst().getConnection();

        /**
         * Gets the groups from that player.
         */
        Groups(String player) {
            this.player = player;
            // Gets the data from the database.
            try {
                Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery("SELECT `list` FROM `efriends` WHERE owner='" + player + "'");

                while (resultset.next())
                    add(resultset.getString(1));
            }
            catch (SQLException e) {
                logger.log(Level.WARNING, component + "  " + e);
            }
        }

        /**
         *
         */
        public String getPlayer() {
            return player;
        }

        /**
         * Gets all the groups owned by that player.
         */
        public List<String> getGroups() {
            return groups;
        }

        /**
         * Adds the group to the player.
         */
        public void add(String group) {
            if (!hasGroup(group))
                groups.add(group);
            sync();
        }

        /**
         * Removes the group from the player.
         */
        public void remove(String group) {
            if (hasGroup(group)) {
                groups.remove(group);
                remove.add(group);
            }
            sync();
        }

        /**
         * Check is the group exits in the database.
         *
         * @return true When the group exists.
         */
        public boolean isGroup(String group) {
            boolean results = false;
            try {
                Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery("SELECT `list` FROM `efriends` WHERE list='" + group + "'");
                results = resultset.first();
            }
            catch (SQLException e) {
                logger.log(Level.WARNING, component + "  " + e);
            }

            return results;
        }

        /**
         * Check if the player has the group.
         *
         * @return true When the player owns the group.
         */
        public boolean hasGroup(String group) {
            return groups.contains(group);
        }

        /**
         * Sync the data to the database.
         */
        private void sync() {
            List<String> data = getGroups();

            // Add the data the to database.
            for (int i=0; i < data.size(); i++) {
                if (!isGroup(data.get(i))) {
                    try {
                        Statement statement = connection.createStatement();
                        statement.execute("INSERT INTO efriends VALUES('" + getPlayer() + "', '" + data.get(i) + "', '" + getPlayer() + "')");
                    }
                    catch (SQLException e) {
                        logger.log(Level.WARNING, component + "  " + e);
                    }
                }
            }

            // Remove the data from the database.
            for (int i=0; i < remove.size(); i++) {
                if (isGroup(remove.get(i))) {
                    try {
                        Statement statement = connection.createStatement();
                        statement.execute("DELETE FROM efriends WHERE list='" + remove.get(i)  + "'");
                    }
                    catch (SQLException e) {
                        logger.log(Level.WARNING, component + "  " + e);
                    }
                }
            }
        }

    }

    public class Group {
        private List<String> members = new ArrayList<String>();
        private List<String> remove = new ArrayList<String>();
        private String owner;
        private String group;
        private Connection connection = EFriends.inst().getConnection();

        /**
         * Pull in the group data from the database.
         */
        public Group(String group) {
            this.group = group;
            // Gets the data from the database.
            try {
                Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery("SELECT `member` FROM `efriends` WHERE list='" + group + "'");

                while (resultset.next())
                    add(resultset.getString(1));
            }
            catch (SQLException e) {
                logger.log(Level.WARNING, component + " Can not connect to database or excute the statement.");
            }
        }

        /**
         * Gets the name of the group.
         *
         * @return The group name.
         */
        public String getGroup() {
            return group;
        }

        /**
         * Get the owner of the group.
         *
         * @return The owner of the group.
         */
        public String getOwner() {
            return members.get(0);
        }

        /**
         * Gets a list of the members of the group.
         *
         * @return The list of members.
         */
        public List<String> getMembers() {
            return members;
        }

        /**
         * Check if the player is a member.
         *
         * @return true When the player is a member.
         */
        public boolean hasMember(String player) {
            return members.contains(player);
        }

        /**
         * Check is the group exits in the database.
         *
         * @return true When the group exists.
         */
        public boolean isMember(String player) {
            boolean results = false;
            try {
                Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery("SELECT `member` FROM `efriends` WHERE list='" + getGroup() + "' AND member='" + player + "'");
                results = resultset.first();
            }
            catch (SQLException e) {
                logger.log(Level.WARNING, component + "  " + e);
            }

            return results;
        }

        /**
         * Add a player from the group if not all ready in.
         */
        public void add(String player) {
            if (!hasMember(player))
                members.add(player);
            sync();
        }

        /**
         * Removes a player from the group if it exists.
         */
        public void remove(String player) {
            if (hasMember(player) && !player.equalsIgnoreCase(getOwner())) {
                members.remove(player);
                remove.add(player);
            }
            sync();
        }

        /**
         * Sync the data to the database.
         */
        private void sync() {
            List<String> data = getMembers();

            // Add the data the to database.
            for (int i=0; i < data.size(); i++) {
                if (!isMember(data.get(i))) {
                    try {
                        Statement statement = connection.createStatement();
                        statement.execute("INSERT INTO `efriends` VALUES('" + getOwner() + "', '" + getGroup() + "', '" + data.get(i) + "')");
                    }
                    catch (SQLException e) {
                        logger.log(Level.WARNING, component + "  " + e);
                    }
                }
            }

            // Remove the data from the database.
            for (int i=0; i < remove.size(); i++) {
                if (isMember(remove.get(i))) {
                    try {
                        Statement statement = connection.createStatement();
                        statement.execute("DELETE FROM `efriends` WHERE member='" + remove.get(i) + "' AND list='" + getGroup() + "'");
                    }
                    catch (SQLException e) {
                        logger.log(Level.WARNING, component + "  " + e);
                    }
                }
            }
        }

    }

}
