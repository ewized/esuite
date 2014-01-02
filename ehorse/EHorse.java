package net.year4000.ehorse;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eHorse", desc = "Horse stuff with awesome pony stuff too.")
public class EHorse extends BukkitComponent implements Listener {

	private String component = "[eHorse]";
	public Logger logger = Logger.getLogger(component);
	private LocalConfiguration config;   

    public void enable() {
        config = configure(new LocalConfiguration());
        CommandBook.registerEvents(this);
        if (config.saddleRecipe) {
        	Bukkit.addRecipe(saddleRecipe());
        }
        if (config.horseArmorRecipe) {
        	Bukkit.addRecipe(horseIronArmorRecipe());
        	Bukkit.addRecipe(horseGoldArmorRecipe());
        	Bukkit.addRecipe(horseDiamondArmorRecipe());
        }
        logger.log(Level.INFO, component + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    public static class LocalConfiguration extends ConfigurationBase {
    	@Setting("saddle-recipe") public boolean saddleRecipe = true;
    	@Setting("horse-armor-recipe") public boolean horseArmorRecipe = true;
    }

    public ShapedRecipe saddleRecipe() {
    	ShapedRecipe saddle = new ShapedRecipe(new ItemStack(Material.SADDLE));
    	saddle.shape("LLL", "LIL");
    	saddle.setIngredient('L', Material.LEATHER);
    	saddle.setIngredient('I', Material.IRON_INGOT);
    	return saddle;
    }

    public ShapedRecipe horseIronArmorRecipe() {
    	ShapedRecipe armor = new ShapedRecipe(new ItemStack(Material.IRON_BARDING));
    	armor.shape("##I", "ISI", "III");
    	armor.setIngredient('S', Material.SADDLE);
    	armor.setIngredient('I', Material.IRON_INGOT);
    	return armor;
    }

    public ShapedRecipe horseGoldArmorRecipe() {
    	ShapedRecipe armor = new ShapedRecipe(new ItemStack(Material.GOLD_BARDING));
    	armor.shape("##G", "GSG", "GGG");
    	armor.setIngredient('S', Material.SADDLE);
    	armor.setIngredient('G', Material.GOLD_INGOT);
    	return armor;
    }

    public ShapedRecipe horseDiamondArmorRecipe() {
    	ShapedRecipe armor = new ShapedRecipe(new ItemStack(Material.DIAMOND_BARDING));
    	armor.shape("##D", "DSD", "DDD");
    	armor.setIngredient('S', Material.SADDLE);
    	armor.setIngredient('D', Material.DIAMOND);
    	return armor;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void EntityDamage(EntityDamageByEntityEvent event) {
    	Entity damager = event.getDamager();
    	Entity entity = event.getEntity();

        if (entity instanceof Horse) {
            if (damager instanceof Player) {
                Player player = (Player)event.getDamager();
                event.setCancelled(checkHorse(player, entity));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEntityEvent event) {
    	Player player = event.getPlayer();
    	Entity horse = event.getRightClicked();

        if (horse instanceof Horse)
    	    event.setCancelled(checkHorse(player, horse));
    }

    public boolean checkHorse(Player player, Entity horse) {
    	boolean isOwner = false;

    	if (player != null && horse != null) {
        	String owner = getOwner(horse);
        	isOwner = isOwner(player.getName(), owner);
        	
        	if (horse.getType() == EntityType.HORSE) {
        		if (!isOwner && !player.isSneaking()) {
    	    		if (CommandBook.inst().hasPermission(player, "ehorse.override")) {
    	    			player.sendMessage(ChatColor.RED + "You are bypassing "
    	        				+ owner + " horse.");
    	    			return isOwner;
    	    		} else {
    	    			player.sendMessage(ChatColor.GOLD + "NOTICE: " + ChatColor.YELLOW
    	    					+ "This horse belongs to "
    	    					+ owner + ".");
    	    		}
        		}
    	    	if (player.isSneaking()) {
    	    		player.sendMessage(ChatColor.GRAY + "This horse belongs to "
    						+ owner + ".");
    	    		return true;
    	    	}
        	}
    	}
    	return !isOwner;
    }

    public boolean isOwner(String player, String owner) {
    	if (player != null && owner != null) {
        	if (owner.equals(player) || owner.equals("no one")) {
        		return true;
        	}
    	}
    	return false;
    }

    public String getOwner(Entity horse) {
    	EntityType entity = horse.getType();

    	if (horse != null && entity != null) {
        	if (entity == EntityType.HORSE) {
        		HorseInventory horseInventory = ((Horse) horse).getInventory();

        		if (horseInventory != null) {
        			ItemStack saddle = horseInventory.getSaddle();

        			if (saddle != null) {
        				if (!saddle.getItemMeta().hasDisplayName()) {
        					return "no one";
        				}
        				return saddle.getItemMeta().getDisplayName();
        			}
        		}
        	}
    	}

    	return "no one";
    }
}
