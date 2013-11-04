package net.year4000.ecurrency;

import java.text.DecimalFormat;

import com.sk89q.commandbook.session.PersistentSession;
import com.zachsthings.libcomponents.config.Setting;

public class Session extends PersistentSession {
    @Setting("balance") private double balance = 0;

    protected Session() {
    	super(-1);
    }

    public double getBalance() {
    	return balance;
    }

    public void setBalance(double balance) {
    	DecimalFormat format = new DecimalFormat("#.");

    	if (this.balance != 0 && balance > 0) {
    	    this.balance = Double.valueOf(format.format(balance));
    	}
    }

    public void addBalance(double balanceStart, double balanceChange) {
		double addTo = balanceStart + balanceChange;

		if (addTo < 0) {
			setBalance(0);
		}
		setBalance(addTo);
    }

	public void removeBalance(double balanceStart, double balanceChange) {
		double removeTo = balanceStart - balanceChange;

		if (removeTo < 0) {
			setBalance(0);
		}
		setBalance(removeTo);
	}
}
