package net.year4000.echat;

import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

public class Config extends ConfigurationBase {

    // Define default config options.
    @Setting("chat-format") public String chat = "<%player%> %message%";
    @Setting("server-format") public String server = "(%player%) %message%";
    @Setting("bungeecord") public boolean bungeecord = false;
    @Setting("groups.group.prefix") public String groupPrefix = "groupprefix";
    @Setting("groups.group.suffix") public String groupSuffix = "groupprefix";

    /**
     * Get a special option that is defined per player's group.
     *
     * @return Option value.
     */
    public String getOption(String group, String option) {
        Object config = EChat.inst().getRawConfiguration().getProperty("groups."
                + group + "." + option);
        if (config != null) {
            return config.toString();
        }
        return option;
    }
}

