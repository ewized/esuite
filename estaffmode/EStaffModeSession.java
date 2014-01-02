package net.year4000.estaffmode;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class EStaffModeSession extends PersistentSession {
	
	@Setting("staff-group") private String staffGroup = "";
	@Setting("staff-mode") private Boolean staffMode = false;
    
    protected EStaffModeSession(){
    	super(-1);
    }
    public String getStaffGroup(){
    	return staffGroup;
    }
    public void setStaffGroup(String staffGroup){
    	this.staffGroup = staffGroup;
    }
    public Boolean getStaffMode(){
    	return staffMode;
    }
    public void setStaffMode(Boolean staffMode){
    	this.staffMode = staffMode;
    }
}