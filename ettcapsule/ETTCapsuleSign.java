package net.year4000.ettcapsule;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class ETTCapsuleSign {
	
	private String lineOne;
	private String lineTwo;
	private String lineThree;
	private Block signBlock;
	
	public Block searchSign(Location location){
		World world = location.getWorld();
		int rx = location.getBlockX()-1;
		int rz = location.getBlockZ()-1;
		int ry = location.getBlockY();
		
		for(int x = 0;x<3;x++){
			for(int z = 0;z<3;z++){
				for(int y = 0;y<3;y++){
					Block templocation = world.getBlockAt(rx+x, ry+y, rz+z);
					if(checkSign(templocation)){
						return templocation;
					}
				}
			}
		}
		return null;
	}
	
	public boolean checkSign(Block block){
		if(block != null){
			if(block.getType() == Material.WALL_SIGN){
				Sign sign = (Sign) block.getState();
				String[] lines = sign.getLines();
				if(lines[0].equalsIgnoreCase("[TT Capsule]")){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean readSign(Block block){
		if(checkSign(block)){
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			this.lineOne = lines[1];
			this.lineTwo = lines[2];
			this.lineThree = lines[3];
			this.signBlock = block;
			return true;
		}
		return false;
	}
	
	public String getIDWorld(){
		return "TT_Capsule/TT-" + this.lineOne.substring(4);
	}
	
	public String getID(){
		return this.lineOne.substring(4);
	}
	
	public String getLock(){
		return this.lineThree;
	}
	
	public String getOwner(){
		return this.lineTwo;
	}
	
	public Block getBlock(){
		return this.signBlock;
	}
}
