package net.year4000.ettcapsule;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Sign;

public class ETTCapsuleBuild {

public void buildBoxInSide(World world, int rx, int ry, int rz, String owner) {

		rx = rx-1;
		rz = rz-1;
		
		world.getBlockAt(rx, ry, rz).getChunk().load();

		for (int x = 0;x<3;x++) {
			for (int z = 0;z<3;z++) {
				world.getBlockAt(rx+x, ry-1, rz+z).setTypeIdAndData(43, (byte) 0, false);
			}
		}
		
		for (int x = 0;x<3;x++) {
			for (int z = 0;z<3;z++) {
				for (int y = 0;y<3;y++) {
					world.getBlockAt(rx+x, ry+y, rz+z).setTypeIdAndData(35, (byte) 11, false);
				}
			}
		}
		
		for (int x = 0;x<2;x++) {
			for (int z = 0;z<3;z++) {
				for (int y = 0;y<3;y++) {
					world.getBlockAt(rx+1+x, ry+y, rz+z).setTypeIdAndData(35, (byte) 15, false);
				}
			}
		}
		
		for (int z = 0;z<3;z++) {
			if (world.getBlockAt(rx, ry+3, rz+z).isEmpty()) {
				world.getBlockAt(rx, ry+3, rz+z).setTypeIdAndData(171, (byte) 11, false);
			}
		}
		
		for (int x = 0;x<2;x++) {
			for (int z = 0;z<3;z++) {
				if (world.getBlockAt(rx+1+x, ry+3, rz+z).isEmpty()) {
					world.getBlockAt(rx+1+x, ry+3, rz+z).setTypeIdAndData(171, (byte) 15, false);
				}
			}
		}
		
		world.getBlockAt(rx, ry, rz+1).setTypeIdAndData(64, (byte) 2, false);
		world.getBlockAt(rx, ry+1, rz+1).setTypeIdAndData(64, (byte) 8, false);
		world.getBlockAt(rx+1, ry, rz+1).setTypeIdAndData(90, (byte) 0, false);
		world.getBlockAt(rx+1, ry+1, rz+1).setTypeIdAndData(90, (byte) 0, false);
		world.getBlockAt(rx+1, ry+2, rz+1).setTypeIdAndData(68, (byte) 5, false);
		
		Sign sign1 = (Sign) world.getBlockAt(rx+1, ry+2, rz+1).getState();
		sign1.setLine(0, "[TT Capsule]");
		sign1.setLine(1, "ID: " + world.getName().substring(14));
		sign1.setLine(2, owner);
		sign1.update();
    }
    
    public void buildBoxOutSide(World world, int rx, int ry, int rz, World oldworld, String owner) {

    	rx = rx-1;
    	rz = rz-1;
    	
    	world.getBlockAt(rx, ry, rz).getChunk().load();

		for (int x = 0;x<3;x++) {
			for (int z = 0;z<3;z++) {
				world.getBlockAt(rx+x, ry-1, rz+z).setTypeIdAndData(43, (byte) 0, false);
			}
		}
		
		for (int x = 0;x<3;x++) {
			for (int z = 0;z<3;z++) {
				for(int y = 0;y<4;y++){
					world.getBlockAt(rx+x, ry+y, rz+z).setTypeIdAndData(35, (byte) 11, false);
				}
			}
		}
		
		for (int x = 0;x<3;x++) {
			for (int z = 0;z<3;z++) {
				world.getBlockAt(rx+x, ry+3, rz+z).setTypeIdAndData(171, (byte) 11, false);
			}
		}
		
		world.getBlockAt(rx, ry, rz+1).setTypeIdAndData(64, (byte) 0, false);
		world.getBlockAt(rx+1, ry, rz+1).setTypeIdAndData(90, (byte) 0, false);
		world.getBlockAt(rx, ry+1, rz+1).setTypeIdAndData(64, (byte) 8, false);
		world.getBlockAt(rx+1, ry+1, rz+1).setTypeIdAndData(90, (byte) 0, false);
		world.getBlockAt(rx+1, ry+2, rz+1).setTypeIdAndData(68, (byte) 5, false);
		world.getBlockAt(rx+1, ry+3, rz+1).setTypeIdAndData(151, (byte) 0, false);
		world.getBlockAt(rx-1, ry+2, rz+1).setTypeIdAndData(68, (byte) 4, false);
		
		Sign sign1 = (Sign) world.getBlockAt(rx+1, ry+2, rz+1).getState();
		sign1.setLine(0, "[TT Capsule]");
		sign1.setLine(1, "ID: " + oldworld.getName().substring(14));
		sign1.setLine(2, owner);
		sign1.update();
		
		Sign sign2 = (Sign) world.getBlockAt(rx-1, ry+2, rz+1).getState();
		sign2.setLine(1, ChatColor.WHITE + "Police Box");
		sign2.setLine(2, ChatColor.WHITE + "Public Call");
		sign2.update();
    }
	
}
