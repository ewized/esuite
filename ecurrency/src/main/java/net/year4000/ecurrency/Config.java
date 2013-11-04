package net.year4000.ecurrency;

import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

public class Config extends ConfigurationBase {

    // Default Configuration Default
    @Setting("start-balance") public int startBalance = 50;
    @Setting("currency-name-plural") public String moneyNamePlural = "Credits";
    @Setting("currency-name-singular") public String moneyNameSingular = "Credit";
    @Setting("atm.name") public String atmName = "ATM";
    @Setting("atm.exchangeamount") public int exchangeAmount = 1;
    @Setting("atm.exchangeitem") public int exchangeItem = 388;
}
