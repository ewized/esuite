package net.year4000.ebot;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class EBotSession extends PersistentSession {
	@Setting("ebot-mute") private boolean ebotMute = false;

    protected EBotSession() {
    	super(-1);
    }

    public void toggleMute() {
    	this.ebotMute = !this.ebotMute;
    }
    
    public boolean getMute(){
    	return this.ebotMute;
    }

}