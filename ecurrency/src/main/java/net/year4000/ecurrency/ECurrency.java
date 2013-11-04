package net.year4000.ecurrency;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

@ComponentInformation(friendlyName = "eCurrency", desc = "Players balance to buy and sell on the server.")
@Depend(components = SessionComponent.class)
public class ECurrency extends BukkitComponent implements Listener {

	private String component = "[eCurrency]";
	private String version = this.getClass().getPackage().getImplementationVersion();
	private Logger logger = Logger.getLogger(component);
	private Config config;
	@InjectComponent private SessionComponent sessions;

    public void enable() {
    	config = configure(new Config());
        CommandBook.registerEvents(this);
        registerCommands(Commands.class);
        logger.log(Level.INFO, component + " version " + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }
    
    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("start-balance") public int startBalance = 50;
    	@Setting("currency-name-plural") public String moneyNamePlural = "Credits";
    	@Setting("currency-name-singular") public String moneyNameSingular = "Credit";
    	@Setting("atm.name") public String atmName = "ATM";
    	@Setting("atm.exchangeamount") public int exchangeAmount = 1;
    	@Setting("atm.exchangeitem") public int exchangeItem = 388;
    }

    public String getMoneyNamePlural() {
    	return config.moneyNamePlural;
    }

    public String getMoneyNameSingular() {
    	return config.moneyNameSingular;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	Session session = sessions.getSession(Session.class, player);
    	
    	if (session.getBalance() == 0) {
    		session.setBalance(config.startBalance);
    	}
    }

	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            String message = onSign(block, player, action);

            if (message != null) {
            	player.sendMessage(ChatColor.YELLOW + message);
            }
        }
    }

	@SuppressWarnings("deprecation")
	public String onSign(Block block, Player player, Action action) {
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		Session session = sessions.getSession(Session.class, player);
		Material exchangeItem = Material.getMaterial(config.exchangeItem);
		ItemStack exchange = new ItemStack(Material.getMaterial(config.exchangeItem), config.exchangeAmount);

		if (lines[0].equalsIgnoreCase("[" + config.atmName + "]")) {
			if (action == Action.RIGHT_CLICK_BLOCK) {
				int amount = player.getInventory().getItemInHand().getAmount();

				if (player.getInventory().contains(exchange, amount)) {
					player.getInventory().removeItem(exchange);
					player.updateInventory();
					session.addBalance(session.getBalance(), config.exchangeAmount);
					return "You have exchanged " + config.exchangeAmount + " "
							+ exchangeItem.toString().toLowerCase() + ", you new balance is " 
							+ session.getBalance();
				}

				return "You do not have the item in your hand in order to exchange.";

			} else if (action == Action.LEFT_CLICK_BLOCK) {
				if (player.getInventory().firstEmpty() != -1 && session.getBalance() >= config.exchangeAmount) {
					player.getInventory().addItem(exchange);
					session.removeBalance(session.getBalance(), config.exchangeAmount);

					if (config.exchangeAmount == 1) {
						return "You have exchanged " + config.exchangeAmount  + " "
								+ config.moneyNameSingular.toLowerCase() + ", you new balance is " 
								+ session.getBalance();
					}

					return "You have exchanged " + config.exchangeAmount  + " "
							+ config.moneyNamePlural.toLowerCase() + ", you new balance is " 
							+ session.getBalance();
				} else if(session.getBalance() < config.exchangeAmount) {
					return "You do not have the correct amount of items.";
				}

				return "You do not have a place to put the items.";
			}

			return "Something happen and you did not exchange anything.";
		}

		return null;
	}

    public class Commands{
    	 @Command(aliases = {"balance", "bal", "money"}, usage = "[add|remove|set] [amount] [player]", desc = "All balance related command")
    	 @CommandPermissions({"ecurrency.balance", "ecurrency.balance.add", "ecurrency.balance.remove", "ecurrency.balance.set", "ecurrency.balance.other"})
         public void balance(CommandContext args, CommandSender player) throws CommandException {
    		 Session session = null;

    		 if (args.argsLength() == 3) {
    			 try {
    				 session = sessions.getSession(Session.class, Bukkit.getOfflinePlayer(args.getString(2)).getPlayer());
    			 } catch(Exception e) {
    				 player.sendMessage(ChatColor.YELLOW + "Can not grab that player's balance.");
    			 }
    		 } else if (args.argsLength() == 2) {
    			 session = sessions.getSession(Session.class, player);

	    		 if (args.getString(0).equalsIgnoreCase("add")) {
	    			CommandBook.inst().checkPermission(player, "ecurrency.balance.add");
	    			session.addBalance(session.getBalance(), args.getDouble(1));
	    		 } else if (args.getString(0).equalsIgnoreCase("remove")) {
	    			 CommandBook.inst().checkPermission(player, "ecurrency.balance.remove");
	    			 session.removeBalance(session.getBalance(), args.getDouble(1));
	    		 } else if (args.getString(0).equalsIgnoreCase("set")) {
	    			 CommandBook.inst().checkPermission(player, "ecurrency.balance.set");
	    			 double setTo = args.getDouble(1);
		 			 session.setBalance(setTo);
	    		 }

	    		 if (session.getBalance() == 1.0) {
	    			 player.sendMessage(ChatColor.YELLOW + session.getOwner().getName() 
	    					 + "'s balance is " + session.getBalance() + " " 
	    					 + config.moneyNameSingular.toLowerCase() + ".");
	    		 }else{
	    			 player.sendMessage(ChatColor.YELLOW + session.getOwner().getName() 
	    					 + "'s balance is " + session.getBalance() + " " 
	    					 + config.moneyNamePlural.toLowerCase() + ".");
	    		 }
	    		 
    		 } else if (args.argsLength() == 1) {
    			 CommandBook.inst().checkPermission(player, "ecurrency.balance.other");
    			 Player p = Bukkit.getPlayerExact(args.getString(0));
    			 session = sessions.getSession(Session.class, p);

    			 if (session.getBalance() == 1.0) {
    				 player.sendMessage(ChatColor.YELLOW + p.getName().substring(0,1).toUpperCase() 
    						 + p.getName().substring(1) + "'s balance is " + session.getBalance() 
    						 + " " + config.moneyNameSingular.toLowerCase() + ".");
    			 } else {
    				 player.sendMessage(ChatColor.YELLOW + p.getName().substring(0,1).toUpperCase() 
    						 + p.getName().substring(1) + "'s balance is " + session.getBalance() 
    						 + " " + config.moneyNamePlural.toLowerCase() + ".");
    			 }
    		 } else {
    			 session = sessions.getSession(Session.class, player);

    			 if (session.getBalance() == 1.0) {
    				 player.sendMessage(ChatColor.YELLOW + "Your balance is " 
    						 + session.getBalance() + " " + config.moneyNameSingular.toLowerCase() + ".");
    			 } else {
    				 player.sendMessage(ChatColor.YELLOW + "Your balance is " 
    						 + session.getBalance() + " " + config.moneyNamePlural.toLowerCase() + ".");
    			 }
    		 }
         }
    }
}
