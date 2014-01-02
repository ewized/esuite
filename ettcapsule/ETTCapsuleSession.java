package net.year4000.ettcapsule;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class ETTCapsuleSession extends PersistentSession {
	
	@Setting("tardis-portal-world") private String tardisPortalWorld;
	@Setting("tardis-portal-x") private int tardisPortalX;
	@Setting("tardis-portal-y") private int tardisPortalY;
	@Setting("tardis-portal-z") private int tardisPortalZ;
    
    protected ETTCapsuleSession(){
    	super(-1);
    }
    
    public void setLocation(String w, int x, int y, int z){
    	this.tardisPortalWorld = w;
    	this.tardisPortalX = x;
    	this.tardisPortalY = y;
    	this.tardisPortalZ = z;
    }
    
    public Location getLocation(){
    	World world = Bukkit.getWorld(Bukkit.getWorld(tardisPortalWorld).getUID());
    	return new Location(world, tardisPortalX, tardisPortalY, tardisPortalZ);
    }
    
}
