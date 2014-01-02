package net.year4000.ebot;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.session.SessionComponent;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

import org.bukkit.Bukkit;

@ComponentInformation(friendlyName = "eBot", desc = "Have you own personal server bot.")
@Depend(components = SessionComponent.class)
public class EBot extends BukkitComponent implements Listener {

	private String component = "[eBot]";
	private String version = this.getClass().getPackage().getImplementationVersion();
    private ChatterBotFactory chatterbotfactory;
    private ChatterBot chatterbot;
    private ChatterBotSession chatterbotsession;
    private LinkedList<String> messages;
    private long talkTick;
    private EBotConfig config = new EBotConfig();
    public Logger logger = Logger.getLogger(component);
    @InjectComponent private SessionComponent sessions;

    public void enable() {
        messages = new LinkedList<String>();
        config = configure(config);
        
        try {
        	chatterbotfactory = new ChatterBotFactory();
            if (config.bot.equalsIgnoreCase("pandorabots")) {
            	chatterbot = chatterbotfactory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            } else if (config.bot.equalsIgnoreCase("jabberwacky")) {
            	chatterbot = chatterbotfactory.create(ChatterBotType.JABBERWACKY);
            } else if (config.bot.equalsIgnoreCase("cleverbot")) {
            	chatterbot = chatterbotfactory.create(ChatterBotType.CLEVERBOT);
            } else {
            	chatterbot = chatterbotfactory.create(ChatterBotType.valueOf(config.bot));
            }
            chatterbotsession = chatterbot.createSession();
        } catch (Exception e) {
            this.disable();
        }
        
        CommandBook.registerEvents(this);
        registerCommands(Commands.class);
        logger.log(Level.INFO, component + " version " + version + " has been enabled.");
    }

    public void reload() {
        super.reload();
        configure(config);
        logger.log(Level.INFO, component + " has been reloaded.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
    	String message = event.getMessage();
    	Player player = event.getPlayer();
    	EBotSession session = sessions.getSession(EBotSession.class, player);
    	if (!session.getMute()) {
	        if (message.length() > config.botTrigger.length()) {
	            if (message.contains(replaceColorAndVaribles(config.botTrigger, null))) {
	                messages.push(message);
	                event.setMessage(message);
	                
	                if (messages.size() == 1 && Bukkit.getWorlds().get(0).getFullTime() > talkTick) {
	                    talkTick = Bukkit.getWorlds().get(0).getFullTime() + (config.responseDelay * 20);
	                    Bukkit.getScheduler().runTaskAsynchronously(CommandBook.inst(), new Think(this, messages.peek()));
	                }
	            }
	        }
    	}
    }

    public class Commands {

    	@Command(aliases = {"mutebot"}, usage = "", desc = "Toggle if the bot talks to you.")
	    public void command(CommandContext args, CommandSender sender) {
    		Player player = Bukkit.getPlayer(sender.getName());
    		EBotSession session = sessions.getSession(EBotSession.class, player);
    		if (!session.getMute()) {
    			player.sendMessage(ChatColor.YELLOW + "You have muted the bot, you wont get chat from the bot.");
    		} else {
    			player.sendMessage(ChatColor.YELLOW + "You have unmuted the bot, you will get chat from the bot.");
    		}
    		session.toggleMute();
		}

    }

    private class Think implements Runnable {

        private EBot ebot;
        private String msg;

        public Think(EBot ebot, String msg) {
            this.ebot = ebot;
            this.msg = msg;
        }

        @Override
        public void run() {
            String response = null;
            try {
                response = ebot.chatterbotsession.think(msg);
            } catch (Exception e) {
                System.out.println("Error querying the bot, no response will be given!");
            }
            long delay = ebot.talkTick - Bukkit.getWorlds().get(0).getFullTime();
            if (delay < 0) {
                delay = 0;
            }
            Bukkit.getScheduler().runTaskLater(CommandBook.inst(), new Talk(ebot, response), delay);
        }
    }

    private class Talk implements Runnable {

        private EBot ebot;
        private String msg;

        public Talk(EBot ebot, String msg) {
            this.ebot = ebot;
            this.msg = msg;
        }

        @Override
        public void run() {

            for (Player player : Bukkit.getOnlinePlayers()) {
            	EBotSession session = sessions.getSession(EBotSession.class, player);
            	if (!session.getMute()) {
            		player.sendMessage(replaceColorAndVaribles(config.botChat, msg));
            	}
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(replaceColorAndVaribles(config.botChat, msg)));
            ebot.messages.poll();
            if (ebot.messages.size() > 0) {
            	ebot.talkTick = Bukkit.getWorlds().get(0).getFullTime() + (config.responseDelay * 20);
                Bukkit.getScheduler().runTaskAsynchronously(CommandBook.inst(), new Think(ebot, ebot.messages.poll()));
            }
        }
    }

    
	public String replaceColorAndVaribles(String name, String message) {
		name = name.replace("%botname%", config.botName);
		if (message != null) name = name.replace("%message%", message);
		for (ChatColor c : ChatColor.values()) {
			name = name.replaceAll("&"+c.getChar(),c.toString()); 
    	}
		return name;
	}
}
