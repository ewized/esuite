package net.year4000.eprotect;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.Attachable;

public class Protected {
	
	Set<BlockFace> blockFaces = EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    Set<BlockFace> blockUpDown = EnumSet.of(BlockFace.UP, BlockFace.DOWN);
	
	public boolean isProtected(Block block, Player player){
		switch(block.getType()){
			case WALL_SIGN:
				return checkSign(block, player);
			case IRON_DOOR:
				return false;
			case WOODEN_DOOR:
				return false;
			default:
				return checkBlock(block, player);
		}
	}
	
	public boolean checkSign(Block block, Player player){
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();

		if(lines[0].equalsIgnoreCase("[Protect]")){
			if(player != null){
				Boolean locked = true;
				for(String line : lines){
					if(line.equalsIgnoreCase(player.getName())){
						locked = false;
					}
				}
				if(locked){
					player.sendMessage(ChatColor.GOLD+"NOTICE: " + ChatColor.YELLOW + "This block is protected by " + lines[1] + ".");
					return true;
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean checkBlock(Block block, Player player){
		for(BlockFace blockface : blockFaces){
			Block face = block.getRelative(blockface);
			if(face.getType()==Material.WALL_SIGN){
				Sign sign = (Sign) face.getState();
				Attachable direction = (Attachable) sign.getData();
				BlockFace blockfacesign = direction.getAttachedFace().getOppositeFace();
				if(blockface.equals(blockfacesign)){
					return checkSign(face, player);
				}
			}
		}
		return false;
	}
	
}