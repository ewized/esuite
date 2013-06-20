package net.year4000.ecurrency;

import java.text.DecimalFormat;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class ECurrencySession extends PersistentSession {
	
    @Setting("balance") private double balance = 0;
    
    protected ECurrencySession(){
    	super(-1);
    }
    public double getBalance(){
    	return balance;
    }
    public void setBalance(double balance){
    	DecimalFormat format = new DecimalFormat("#.");
    	this.balance = Double.valueOf(format.format(balance));
    }
}