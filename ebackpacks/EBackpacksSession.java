package net.year4000.ebackpacks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class EBackpacksSession extends PersistentSession {
	
	@Setting("backpack-inventory") private Inventory backpackInventory = Bukkit.createInventory(null, 9, "Backpack");;
	@Setting("backpack-inventory") private boolean backpackCreated = true;
    
    protected EBackpacksSession(){
    	super(-1);
    }
    
    public void setInventory(Inventory inventory){
    	this.backpackInventory = inventory;
    }
    
    public Inventory getInventory(){
    	return this.backpackInventory;
    }
    
    public boolean getNewInventory(){
    	return this.backpackCreated;
    }
    
    public void setNewInventory(){
    	this.backpackCreated = false;
    }
    
}
