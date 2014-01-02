import java.util.logging.Level;
import java.util.logging.Logger;

import net.year4000.ecurrency.ECurrency;
import net.year4000.ecurrency.ECurrencySession;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

@ComponentInformation(friendlyName = "eMobDeath", desc = "Get stuff when a mob dies.")
@Depend(components = SessionComponent.class)
public class EMobDeath extends BukkitComponent implements Listener {

	private String component = "[eMobDeath]";
	public Logger logger = Logger.getLogger(component);
	private LocalConfiguration config;
	@InjectComponent private SessionComponent sessions;
	@InjectComponent private ECurrency ec;
	@InjectComponent private ECurrencySession ecs;
	//private ECurrencySession ecs;

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
    	
    	@Setting("mobs.BAT.price") public int batPrice = 1;
    	@Setting("mobs.BAT.name") public String batName = "Bat";
    	@Setting("mobs.BLAZE.price") public int blazePrice = 4;
    	@Setting("mobs.BLAZE.name") public String blazeName = "Blaze";
    	@Setting("mobs.CAVE_SPIDER.price") public int caveSpiderPrice = 2;
    	@Setting("mobs.CAVE_SPIDER.name") public String caveSpiderName = "Cave Spider";
    	@Setting("mobs.CHICKEN.price") public int chickenPrice = 1;
    	@Setting("mobs.CHICKEN.name") public String chickenName = "Chicken";
    	@Setting("mobs.COW.price") public int cowPrice = 1;
    	@Setting("mobs.COW.name") public String cowName = "Cow";
    	@Setting("mobs.CREEPER.price") public int creeperPrice = 3;
    	@Setting("mobs.CREEPER.name") public String creeperName = "Creeper";
    	@Setting("mobs.ENDERMAN.price") public int endermanPrice = 8;
    	@Setting("mobs.ENDERMAN.name") public String endermanName = "Enderman";
    	@Setting("mobs.ENDER_DRAGON.price") public int endermanDragonPrice = 100;
    	@Setting("mobs.ENDER_DRAGON.name") public String endermanDragonName = "Ender Dragon";
    	@Setting("mobs.GHAST.price") public int ghastPrice = 3;
    	@Setting("mobs.GHAST.name") public String ghastName = "Ghast";
    	@Setting("mobs.GIANT.price") public int giantPrice = 2;
    	@Setting("mobs.GIANT.name") public String giantName = "Giant";
    	@Setting("mobs.HORSE.price") public int horsePrice = 1;
    	@Setting("mobs.HORSE.name") public String horseName = "Horse";
    	@Setting("mobs.IRON_GOLEM.price") public int ironGolemPrice = 3;
    	@Setting("mobs.IRON_GOLEM.name") public String ironGolemName = "Iron Golem";
    	@Setting("mobs.MAGMA_CUBE.price") public int magmaCubePrice = 2;
    	@Setting("mobs.MAGMA_CUBE.name") public String magmaCubeName = "Magma Cube";
    	@Setting("mobs.MUSHROOM_COW.price") public int mushroomCowPrice = 1;
    	@Setting("mobs.MUSHROOM_COW.name") public String mushroomCowName = "Mooshroom";
    	@Setting("mobs.OCELOT.price") public int ocelotPrice = 1;
    	@Setting("mobs.OCELOT.name") public String ocelotName = "Ocelot";
    	@Setting("mobs.PIG.price") public int pigPrice = 1;
    	@Setting("mobs.PIG.name") public String pigName = "Pig";
    	@Setting("mobs.PIG_ZOMBIE.price") public int pigZombiePrice = 2;
    	@Setting("mobs.PIG_ZOMBIE.name") public String pigZombieName = "Zombie Pigman";
    	@Setting("mobs.PLAYER.price") public int playerPrice = 0;
    	@Setting("mobs.PLAYER.name") public String playerName = "Player";
    	@Setting("mobs.SHEEP.price") public int sheepPrice = 1;
    	@Setting("mobs.SHEEP.name") public String sheepName = "Sheep";
    	@Setting("mobs.SILVERFISH.price") public int sliverfishPrice = 2;
    	@Setting("mobs.SILVERFISH.name") public String sliverfishName = "Sliverfish";
    	@Setting("mobs.SKELETON.price") public int skeletonPrice = 2;
    	@Setting("mobs.SKELETON.name") public String skeletonName = "Skeleton";
    	@Setting("mobs.SLIME.price") public int slimePrice = 2;
    	@Setting("mobs.SLIME.name") public String slimeName = "Slime";
    	@Setting("mobs.SNOWMAN.price") public int snowmanPrice = 1;
    	@Setting("mobs.SNOWMAN.name") public String snowmanName = "Snowman";
    	@Setting("mobs.SPIDER.price") public int spiderPrice = 2;
    	@Setting("mobs.SPIDER.name") public String spiderName = "Spider";
    	@Setting("mobs.SQUID.price") public int squidPrice = 1;
    	@Setting("mobs.SQUID.name") public String squidName = "Squid";
    	@Setting("mobs.VILLAGER.price") public int villagerPrice = -1;
    	@Setting("mobs.VILLAGER.name") public String villagerName = "Villager";
    	@Setting("mobs.WITCH.price") public int witchPrice = 3;
    	@Setting("mobs.WITCH.name") public String witchName = "Witch";
    	@Setting("mobs.WITHER.price") public int witherPrice = 10;
    	@Setting("mobs.WITHER.name") public String witherName = "Wither";
    	@Setting("mobs.WOLF.price") public int wolfPrice = 1;
    	@Setting("mobs.WOLF.name") public String wolfName = "Wolf";
    	@Setting("mobs.ZOMBIE.price") public int zombiePrice = 2;
    	@Setting("mobs.ZOMBIE.name") public String zombieName = "Zombie";

    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event){
    	Player player = event.getEntity().getKiller();
    	String entityType = event.getEntityType().toString();

    	if (player != null && entityType != null) {
    		int price = (int) getVaule(entityType, "price");
    		String name = (String) getVaule(entityType, "name");
    		String currencyp = ec.getMoneyNamePlural();
    		String currencys = ec.getMoneyNameSingular();
    		ecs = sessions.getSession(ECurrencySession.class, player);
    		ecs.addBalance(price, ecs.getBalance());
    		if (price == 1) {
        		player.sendMessage(ChatColor.GREEN
        				+ "You have recived " + price + " " + currencys.toLowerCase()
        				+ " for killing a " + name.toLowerCase() + ".");
    		} else {
        		player.sendMessage(ChatColor.GREEN
        				+ "You have recived " + price + " " + currencyp.toLowerCase()
        				+ " for killing a " + name.toLowerCase() + ".");
    		}
    	}
    }

    public Object getVaule(String mob, String option){
    	Object object = this.getRawConfiguration().getProperty("mobs." + mob + "." + option);
    	if(object != null){
    		return object;
    	}
    	return option;
    }
}
