package net.year4000.ehub;

import com.sk89q.commandbook.CommandBook;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ServerJump implements Listener {
    @EventHandler
    private void portalTeleport(PlayerMoveEvent event) {
        Block locationTo = event.getTo().getBlock();
        Block locationFrom = event.getFrom().getBlock();
        if ((locationTo.getType() == Material.PORTAL) && (locationFrom.getType() != Material.PORTAL)) {
            Location location = event.getTo();
            String server = getServer(getSign(location));

            if (server != null) {
                Location spawn = ((World)Bukkit.getWorlds().get(0)).getSpawnLocation();
                Player player = event.getPlayer();
                player.teleport(spawn);
                player.sendMessage(ChatColor.GOLD + "You are trying to connect to the " + server + " server.");


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeUTF("Connect");
                dos.writeUTF(server);
                player.sendPluginMessage(CommandBook.inst(), "BungeeCord", baos.toByteArray());
                baos.close();
                dos.close();
            }
        }
    }

    private Block getSign(Location location) {
        Block results;
        World world = location.getWorld();
        int rx = location.getBlockX();
        int rz = location.getBlockZ();
        int ry = location.getBlockY();
        for (int x = -3; x < 6; x++) {
            for (int z = -3; z < 6; z++) {
                for (int y = -3; y < 6; y++) {
                    results = world.getBlockAt(rx + x, ry + y, rz + z);
                    if (getServer(templocation) != null) {
                        break;
                    }
                }
            }
        }
        return results;
    }


    private String getServer(Block block) {
        String results = null;
        if (block != null && block.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign)block.getState();
            String[] lines = sign.getLines();
            if (lines[0].equalsIgnoreCase("[ServerJump]")) {
                results = lines[1];
            }
        }
        return results;
    }
}
