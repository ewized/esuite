package net.year4000.echat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import com.sk89q.commandbook.CommandBook;

import net.year4000.echat.Message;

public class Sender implements Runnable {

    private Configuration configuration = EChat.inst().getConfiguration();
    private Message message;
    private String playerName;
    private String playerMessage;
    private String formatMessage;

    /**
     * Setup the environment so the message can be sent.
     *
     * @param message The class of the current message.
     */
    public Sender(Message message) {
        this.message = message;
        this.playerName = message.getPlayerName();
        this.playerMessage = message.getPlayerMessage();
        this.formatMessage = formatChat(message.getPlayerFormat());
    }

    /**
     * Sends the message to each player on the server and the console.
     */
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            // Check if the your trying to mention a player
            // and notify that player.
            if (checkName(playerName, name, playerMessage)) {
                Location playerLocation = player.getLocation();
                player.playSound(playerLocation, Sound.NOTE_PLING, 1, 0);
                // Change the mention to current mention
                String word = getWord(playerName, name, playerMessage);
                String at = ChatColor.AQUA + "@" + name + ChatColor.RESET;
                String msg = formatMessage.replaceAll(word, at);
                player.sendMessage(msg);
            } else {
                player.sendMessage(formatMessage);
            }
        }

        // Sends the non color message to the console.
        String stripedMessage = ChatColor.stripColor(formatMessage);
        Bukkit.getConsoleSender().sendMessage(stripedMessage);
    }

    /**
     * Checks if a word in the string matches the player.
     * 
     * @param player The player what we are searching for.
     * @param sender The sender of the message.
     * @param msg The message to search for the player.
     * @return the word that my be the player.
     */
    private String getWord(String player, String sender, String msg) {
        final int MINSIZE = 3;
        for (String word : msg.split(" ")) {
            if (word.length() > MINSIZE && word.length() <= sender.length()) {
                word = word.toLowerCase();
                String shortSender = sender.substring(0, word.length()-1);
                if (word.startsWith(shortSender.toLowerCase())) {
                    if (!player.contains(word)) {
                        return word;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a word in the string matches the player.
     * 
     * @param player The player what we are searching for.
     * @param sender The sender of the message.
     * @param msg The message to search for the player.
     * @return true If a word matches the player's name.
     */
    private boolean checkName(String player, String sender, String msg) {
        final int MINSIZE = 3;
        for (String word : msg.split(" ")) {
            if (word.length() > MINSIZE && word.length() <= sender.length()) {
                word = word.toLowerCase();
                String shortSender = sender.substring(0, word.length()-1);
                if (word.startsWith(shortSender.toLowerCase())) {
                    if (!player.contains(word)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Formats the chat by replacing predefined and server defined variables.
     *
     * @param player The player that sent the message.
     * @param chatFormat The format for the chat.
     * @return the chat after all vars has been replaced.
     */
    private String formatChat(String chatFormat) {
        // Predefined chat options.
        chatFormat = chatFormat.replace("%player%",
                this.message.getPlayerName());
        chatFormat = chatFormat.replace("%displayname%",
                this.message.getPlayerDisplayName());
        chatFormat = chatFormat.replace("%world%",
                this.message.getPlayerWorldName());
        chatFormat = chatFormat.replace("%server%",
                this.message.getPlayerServer());
        chatFormat = chatFormat.replace("%group%",
                this.message.getPlayerGroup());
        chatFormat = chatFormat.replace("%faction%",
                this.message.getPlayerFaction());
        chatFormat = chatFormat.replace("%title%",
                this.message.getPlayerTitle());

        // Check if their is a server defined option.
        String tempData = "%%"+chatFormat;
        for (String word : tempData.split("%")) {
            String group = this.message.getPlayerGroup();
            String option = configuration.getOption(group, word);
            if (option != word) {
                chatFormat = chatFormat.replace("%" + word + "%", option);
            }
        }

        chatFormat = replaceColor(chatFormat);

        // Checks if the player has the permission to use colors in this chat.
        if (this.message.getPlayerColor().equalsIgnoreCase("true")) {
            chatFormat = replaceColor(chatFormat.replace("%message%",
                    this.message.getPlayerMessage()));
        } else {
            chatFormat = chatFormat.replace("%message%",
                    this.message.getPlayerMessage());
        }

        return chatFormat;
    }

    /**
     * Replace any color defined by Minecraft.
     *
     * @param message The raw form of the message.
     * @return the message after colors has been added.
     */
    private String replaceColor(String message) {
        for (ChatColor c : ChatColor.values()) {
            message = message.replaceAll("&" + c.getChar(), c.toString()); 
        }
        return message;
    }
}
