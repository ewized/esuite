package net.year4000.echat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import com.sk89q.commandbook.CommandBook;

public class Sender {

    /**
     * Sets up the variables before sending it in a thread.
     */
    public void sendChatMessage() {
        String playerName = EChat.inst().getMessage().getPlayerName();
        String playerMessage = EChat.inst().getMessage().getPlayerMessage();
        String chatFormat = EChat.inst().getMessage().getPlayerFormat();
        String formatMessage = formatChat(null, chatFormat);
        Bukkit.getScheduler().runTaskAsynchronously(CommandBook.inst(),
                new SendMessage(playerName, playerMessage, formatMessage));
    }

    /**
     * Sends the chat in its own thread.
     */
    private class SendMessage implements Runnable {

        private String playerName;
        private String playerMessage;
        private String formatMessage;

        public SendMessage(String name,String message, String format) {
            playerName = name;
            playerMessage = message;
            formatMessage = format;
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
            String stripedMessage = ChatColor.stripColor(formatMessage);
            Bukkit.getConsoleSender().sendMessage(stripedMessage);
        }
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
    private String formatChat(Player player, String chatFormat) {
        // Predefined chat options.
        chatFormat = chatFormat.replace("%player%",
                EChat.inst().getMessage().getPlayerName());
        chatFormat = chatFormat.replace("%displayname%",
                EChat.inst().getMessage().getPlayerDisplayName());
        chatFormat = chatFormat.replace("%world%",
                EChat.inst().getMessage().getPlayerWorldName());
        chatFormat = chatFormat.replace("%server%",
                EChat.inst().getMessage().getPlayerServer());
        chatFormat = chatFormat.replace("%group%",
                EChat.inst().getMessage().getPlayerGroup());

        // Check if their is a server defined option.
        String tempData = "%%"+chatFormat;
        for (String word : tempData.split("%")) {
            String group = EChat.inst().getMessage().getPlayerGroup();
            String option = EChat.inst().getConfiguration().getOption(group, word);
            if (option != word) {
                chatFormat = chatFormat.replace("%" + word + "%", option);
            }
        }

        chatFormat = replaceColor(chatFormat);

        // Checks if the player has the permission to use colors in this chat.
        if (player != null) {
            if (CommandBook.inst().hasPermission(player, "echat.colors")) {
                chatFormat = replaceColor(chatFormat.replace("%message%",
                        EChat.inst().getMessage().getPlayerMessage()));
            }
        } else {
            chatFormat = chatFormat.replace("%message%",
                    EChat.inst().getMessage().getPlayerMessage());
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
