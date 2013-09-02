package net.year4000.echat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.sk89q.commandbook.CommandBook;

public class EChatBungeeCord implements PluginMessageListener {
	private EChatSender sender = EChat.inst().getEChatSender();

	// Send the needed data to every single server.
	public void sendChatBungeeCord() {
	    try {
	    	// Send out to eChat.
	        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
	        DataOutputStream msgout = new DataOutputStream(msgbytes);
	        msgout.writeUTF(EChat.inst().getEChatMessage().getPlayerName());
	        msgout.writeUTF(EChat.inst().getEChatMessage().getPlayerDisplayName());
	        msgout.writeUTF(EChat.inst().getEChatMessage().getPlayerServer());
	        msgout.writeUTF(EChat.inst().getEChatMessage().getPlayerWorldName());
	        msgout.writeUTF(EChat.inst().getEChatMessage().getPlayerGroupName(0));
	        msgout.writeUTF(EChat.inst().getEChatMessage().getPlayerMessage());
	        msgout.writeShort(EChat.inst().getEChatMessage().getPlayerMessage().length());

	        // Send out to BungeeCord.
	        ByteArrayOutputStream b = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(b);
	        out.writeUTF("Forward");
	        out.writeUTF("ALL");
	        out.writeUTF("eChat");
	        out.writeShort(msgbytes.toByteArray().length);
	        out.write(msgbytes.toByteArray());
	        Bukkit.getOnlinePlayers()[0].sendPluginMessage(CommandBook.inst(), "BungeeCord", b.toByteArray());
	      } catch (Exception ex) {
	        ex.printStackTrace();
	      }
	}

	// Receive the data from another server and processes that chat.
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] data) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			String subchannel = in.readUTF();
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			if (subchannel.equals("eChat")) {
				EChat.inst().getEChatMessage().setPlayerName(msgin.readUTF());
				EChat.inst().getEChatMessage().setPlayerDisplayName(msgin.readUTF());
				EChat.inst().getEChatMessage().setPlayerServer(msgin.readUTF());
				EChat.inst().getEChatMessage().setPlayerWorldName(msgin.readUTF());
				EChat.inst().getEChatMessage().setPlayerGroups(msgin.readUTF());
				EChat.inst().getEChatMessage().setPlayerMessage(msgin.readUTF());

				sender.sendChatMessage();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
