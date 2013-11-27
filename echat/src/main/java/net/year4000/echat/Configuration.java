package net.year4000.echat;

import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

public class Configuration extends ConfigurationBase {

    // Define default config options.
    @Setting("chat-format") public String chat = "<%player%> %message%";
    @Setting("server-format") public String server = "(%player%) %message%";
    @Setting("bungeecord") public boolean bungeecord = false;
    @Setting("groups.group.prefix") public String groupPrefix = "groupprefix";
    @Setting("groups.group.suffix") public String groupSuffix = "groupprefix";

    /**
     * Get a special option that is defined per player's group.
     *
     * @param group The group of the player.
     * @param option The option in the config for the group.
     * @return Option value.
     */
    public String getOption(String group, String option) {
        Object configuration = EChat.inst().getRawConfiguration().getProperty("groups."
                + group + "." + option);
        if (configuration != null) {
            return configuration.toString();
        }
        return option;
    }
}

