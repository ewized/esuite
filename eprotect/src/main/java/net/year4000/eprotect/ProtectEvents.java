package net.year4000.eprotect;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;

public class ProtectEvents implements Listener {
    private CommandBook cmdbook = CommandBook.inst();

    /**
     * If the entity is protected, block it from breaking.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHanging(HangingBreakEvent event) {
        Block block = event.getEntity().getLocation().getBlock();
        RemoveCause cause = event.getCause();
        Protected protect = new Protected(block);

        if (cause == RemoveCause.ENTITY) {
            if (protect.isProtected()) {
                event.setCancelled(true);
            }
        }

    }

    /**
     * If the block is being interacted by other than the members, block it.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        Action action = event.getAction();
        Protected protect = new Protected(block);
        boolean sneeking = player.isSneaking() && action == Action.RIGHT_CLICK_BLOCK;

        if (protect.isProtected()) {
            if (cmdbook.hasPermission(player, "eprotect.override")) {
                if (sneeking)
                    player.sendMessage(protect.getMessage(2));
                else
                    player.sendMessage(protect.getMessage(1));
                event.setCancelled(false);
            }
            else if (!protect.isMember(player.getName())) {
                if (sneeking)
                    player.sendMessage(protect.getMessage(2));
                else
                    player.sendMessage(protect.getMessage(0));
                event.setCancelled(true);
            }
        }
        else if (sneeking) {
            player.sendMessage(protect.getMessage(2));
        }
    }

    /**
     * If any of the blocks are protected, block the explosion.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            Protected protect = new Protected(block);

            if (protect.isProtected()) {
                event.setCancelled(true);
                break;
            }
        }
    }

    /**
     * If the block is protected, block it from burring.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        Protected protect = new Protected(block);

        if (protect.isProtected()) {
            event.setCancelled(true);
        }
    }

    /**
     * Gives users access to place protect signs.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Protected protect = new Protected(block);

        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            if (event.getLine(0).equalsIgnoreCase("[Protect]")) {
                if (!protect.isProtected()) {
                    try {
                        CommandBook.inst().checkPermission(player, "eprotect.create.other");
                        event.setLine(0, "[Protect]");

                        if (event.getLine(1).equals("")) {
                            event.setLine(1, player.getName());
                        }
                    }
                    catch (CommandPermissionsException e) {
                        event.setLine(0, "[Protect]");
                        event.setLine(1, player.getName());
                    }
                }
                else {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GOLD + "This block is all ready protected.");
                }
            }
        }
    }
}
