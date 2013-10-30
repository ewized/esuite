package net.year4000.echat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.sk89q.commandbook.CommandBook;

public class Sender {

    // Sets up the variables before sending it in a thread.
    public void sendChatMessage() {
        String playerName = EChat.inst().getMessage().getPlayerName();
        String playerMessage = EChat.inst().getMessage().getPlayerMessage();
        String chatFormat = EChat.inst().getMessage().getPlayerFormat();
        String formatMessage = formatChat(null, chatFormat);
        Bukkit.getScheduler().runTaskAsynchronously(CommandBook.inst(), new SendMessage(playerName, playerMessage, formatMessage));
    }

    // Sends the chat in its own thread.
    private class SendMessage implements Runnable {

        private String playerName;
        private String playerMessage;
        private String formatMessage;

        public SendMessage(String playerName,String playerMessage, String formatMessage) {
            this.playerName = playerName;
            this.playerMessage = playerMessage;
            this.formatMessage = formatMessage;
        }

        // Sends the message to each player on the server and the console.
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                // Check if the your trying to mention a player and notify that player.
                if (checkName(this.playerName, player.getName(), this.playerMessage)) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 0);
                    player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + ChatColor.stripColor(this.formatMessage));
                } else {
                    player.sendMessage(this.formatMessage);
                }
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(this.formatMessage));
        }
    }

    // Checks if the message is not saying him self.
    // Player is the player that we are checking against.
    // Sender is the player that sent the message.
    private boolean checkName(String player, String sender, String msg) {
        final int MINSIZE = 3;
        for (String word : msg.split(" ")) {
            if (word.length() > MINSIZE && word.length() <= sender.length()) {
                word = word.toLowerCase();
                if (word.startsWith(sender.substring(0, word.length()-1).toLowerCase())) {
                    if (!player.contains(word)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Formats the chat by replacing predefined and server defined variables.
    private String formatChat(Player player, String chatFormat) {
        // Predefined chat options.
        chatFormat = chatFormat.replace("%player%", EChat.inst().getMessage().getPlayerName());
        chatFormat = chatFormat.replace("%displayname%", EChat.inst().getMessage().getPlayerDisplayName());
        chatFormat = chatFormat.replace("%world%", EChat.inst().getMessage().getPlayerWorldName());
        chatFormat = chatFormat.replace("%server%", EChat.inst().getMessage().getPlayerServer());
        chatFormat = chatFormat.replace("%group%", EChat.inst().getMessage().getPlayerGroupName(0));

        // Check if their is a server defined option.
        String tempData = "%test%"+chatFormat;
        for (String word : tempData.split("%")) {
            String option = EChat.inst().getConfig().getOption(EChat.inst().getMessage().getPlayerGroupName(0), word);
            if (option != word) {
                chatFormat = chatFormat.replace("%" + word + "%", option);
            }
        }

        chatFormat = replaceColor(chatFormat);

        // Checks if the player has the permission to use colors in this chat.
        if (player != null) {
            if (CommandBook.inst().hasPermission(player, "echat.colors")) {
                chatFormat = replaceColor(chatFormat.replace("%message%", EChat.inst().getMessage().getPlayerMessage()));
            }
        } else {
            chatFormat = chatFormat.replace("%message%",  EChat.inst().getMessage().getPlayerMessage());
        }

        return chatFormat;
    }

    // Replace any color defined by Minecraft.
    private String replaceColor(String message) {
        for (ChatColor c : ChatColor.values()) {
            message = message.replaceAll("&" + c.getChar(), c.toString()); 
        }
        return message;
    }
}