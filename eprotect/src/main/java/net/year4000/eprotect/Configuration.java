package net.year4000.eprotect;

import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

public class Configuration extends ConfigurationBase {

    // Define default configuration options.
    @Setting("sign-header") public String sign = "[Protect]";

}
