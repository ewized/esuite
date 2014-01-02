package net.year4000.eminingworld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class EMiningWorldSession extends PersistentSession {
	
	@Setting("mining-portal-world") private String miningPortalWorld;
	@Setting("mining-portal-x") private int miningPortalX;
	@Setting("mining-portal-y") private int miningPortalY;
	@Setting("mining-portal-z") private int miningPortalZ;
    
    protected EMiningWorldSession(){
    	super(-1);
    }
    
    public void setLocation(String w, int x, int y, int z){
    	this.miningPortalWorld = w;
    	this.miningPortalX = x;
    	this.miningPortalY = y;
    	this.miningPortalZ = z;
    }
    
    public Location getLocation(){
    	World world = Bukkit.getWorld(Bukkit.getWorld(miningPortalWorld).getUID());
    	return new Location(world, miningPortalX, miningPortalY, miningPortalZ);
    }
    
}
