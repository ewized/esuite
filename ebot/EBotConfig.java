package net.year4000.ebot;

import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

public class EBotConfig extends ConfigurationBase {
	@Setting("bot-name") public String botName = "Server";
	@Setting("bot-chat") public String botChat = "[%botname%] %message%";
	@Setting("bot-trigger") public String botTrigger = "%botname%";
	@Setting("bot") public String bot = "Cleverbot";
	@Setting("response-delay") public int responseDelay = 5;
}
