package net.year4000.ecurrency;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class ECurrencySession extends PersistentSession {
	
    @Setting("balance") private int balance = 0;
    
    protected ECurrencySession(){
    	super(-1);
    }
    public int getBalance(){
    	return balance;
    }
    public void setBalance(int balance){
    	this.balance = balance;
    }
}