package net.year4000.eprotect;

import java.util.EnumSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.material.Attachable;

import com.sk89q.commandbook.CommandBook;

import net.year4000.efriends.*;

public class Protected {

    Set<BlockFace> blockFaces = EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    Set<BlockFace> blockUpDown = EnumSet.of(BlockFace.UP, BlockFace.DOWN);
    private Configuration configuration = EProtect.inst().getConfiguration();
    private boolean protect;
    private String type;
    private List<String> members = new ArrayList<String>();
    private int x;
    private int y;
    private int z;

   /**
     * Create a new instance if the entity is protected.
     *
     * @param entity The entity to check against.
     */
    Protected(Entity entity) {
        this.type = "entity";
        Block block = entity.getLocation().getBlock();

        // Special blocks
        switch (block.getType()) {
            case WALL_SIGN:
                checkSign(block);
                break;
            case CHEST:
                checkChest(block);
                break;
            default:
                checkBlock(block);
                break;
        }

        checkChunk(block);
    }

    /**
     * Create a new instance if the block is protected.
     *
     * @param block The block to check against.
     */
    Protected(Block block) {
        this.type = "block";

        // Special blocks
        switch (block.getType()) {
            case WALL_SIGN:
                checkSign(block);
                break;
            case CHEST:
                checkChest(block);
                break;
            default:
                checkBlock(block);
                break;
        }

        checkChunk(block);
    }

    /**
     * Get the owner of the protection.
     *
     * @return The owner of the protection.
     */
    public String getOwner() {
        String results;
        try {
            results = members.get(0);
        }
        catch (Exception e) {
            results = "no one";
        }
        return results;
    }

    /**
     * Checks if the player's name is on the sign.
     *
     * @return true if the name is a member for the sign.
     */
    public boolean isMember(String player) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equalsIgnoreCase("everyone")) {
                return true;
            }
            if (members.get(i).equalsIgnoreCase(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the block is protected.
     *
     * @return true When the block is protected by some player.
     */
    public boolean isProtected() {
        return this.protect;
    }

    /**
     * Get the message to display to the player.
     *
     * @param type The type of message to display.
     * @return The correct message to display.
     */
    public String getMessage(int type) {
        String message;
        switch (type) {
            case 0: // Normal notice message.
                message = ChatColor.GOLD + "NOTICE: " + ChatColor.YELLOW
                        + "This " + this.type
                        + " is protected by " + getOwner() + ".";
                break;
            case 1: // Bypass message.
                message = ChatColor.RED + "You are bypassing "
                        + getOwner() + "'s protection.";
                break;
            case 2: // Info message
                if (isProtected()) {
                    message = ChatColor.GRAY + "This " + this.type
                            + " is protected by: "
                            + getOwner()
                            + " (x:" + this.x + " "
                            + "y:" + this.y + " "
                            + "z:" + this.z + ")";
                }
                else {
                    message = ChatColor.GRAY + "This " + this.type
                            + " is protected by: no one";
                }
                break;
            default:
                message = "";
                break;
        }
        return message;
    }

    /**
     * Check if the sign is a protect sign.
     *
     * @param block The block to check against.
     * @return true When a valid sign has been found.
     */
    private boolean checkSign(Block block) {
        boolean results = false;
        // If the block is already protected don't check again.
        if (isProtected()) {
            return isProtected();
        }

        // Check if the block is a sign.
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();

            if (lines[0].equalsIgnoreCase(configuration.sign)) {
                for (int i = 1; i < lines.length; i++) {
                    if (lines[i].startsWith("#")) {
                        String list = lines[i].substring(1);
                        EFriends.Group group = EFriends.inst().getGroup(list);
                        List<String> players = group.getMembers();
                        for (int j = 0; j < players.size(); j++)
                            members.add(players.get(j));
                    }
                    else
                        members.add(lines[i]);
                }
                // Get the sign location
                x = block.getX();
                y = block.getY();
                z = block.getZ();
                results = true;
            }
        }
        protect = results;
        return results;
    }

    /**
     * Check if the block has a protect sign attached to it.
     *
     * @param block The block to check against.
     */
    private void checkBlock(Block block) {
        for (BlockFace blockface : blockFaces) {
            Block face = block.getRelative(blockface);

            if (face.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) face.getState();
                Attachable direction = (Attachable) sign.getData();
                BlockFace blockfacesign = direction.getAttachedFace().getOppositeFace();

                if (blockface == blockfacesign) {
                    checkSign(face);
                }
            }
        }
    }

    /**
     * Check if the chest is protected.
     *
     * Also check if the chest is a multi block.
     *
     * @param block The block to check against.
     */
    private void checkChest(Block block) {
        for (BlockFace blockface : blockFaces) {
            Block adjacent = block.getRelative(blockface);

            if (adjacent.getState() instanceof Chest) {
                checkBlock(adjacent);
            }
            else if (adjacent.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) adjacent.getState();
                Attachable direction = (Attachable) sign.getData();
                BlockFace blockfacesign = direction.getAttachedFace().getOppositeFace();

                if (blockface == blockfacesign) {
                    checkSign(adjacent);
                }
            }
        }
    }

    /**
     * Check if the chunk is protected.
     *
     * @param block The block to check against.
     */
    private void checkChunk(Block block) {
        Chunk chunk = block.getChunk();
        int blockX = chunk.getX();
        int blockZ = chunk.getZ();

        int minX = blockX * 16;
        int minZ = blockZ * 16;
        int maxX = (blockX * 16) + 16;
        int maxZ = (blockZ * 16) + 16;
        int minY = 0;
        int maxY = block.getWorld().getMaxHeight();

        for (int a = minY; a < maxY; a++) {
            for (int b = minX; b < maxX; b++) {
                for (int c = minZ; c < maxZ; c++) {
                    Block currentBlock = chunk.getBlock(b, a, c);

                    if (currentBlock.getType() == Material.SIGN_POST) {
                        if (checkSign(currentBlock))
                            break;
                    }
                }
            }
        }
    }
}
