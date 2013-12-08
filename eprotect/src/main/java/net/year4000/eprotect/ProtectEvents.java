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
    private Protected protect = new Protected();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHanging(HangingBreakEvent event) {
        Block block = event.getEntity().getLocation().getBlock();
        RemoveCause cause = event.getCause();

        if (cause == RemoveCause.ENTITY) {
            if (protect.isProtected(block, null)) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (protect.isProtected(block, player)) {
            event.setCancelled(true);
        }

        if (player.isSneaking() && action == Action.RIGHT_CLICK_BLOCK) {
            player.sendMessage(ChatColor.GRAY + "This block is protected by: " + protect.getSign(block));
        }

        protect.result = false;
        protect.message = "no one";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (protect.isProtected(block, null)) {
                event.setCancelled(true);
                protect.result = false;
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();

        if (protect.isProtected(block, null)) {
            event.setCancelled(true);
            protect.result = false;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            if (event.getLine(0).equalsIgnoreCase("[Protect]")) {
                if (!protect.isProtected(block, null)) {
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
