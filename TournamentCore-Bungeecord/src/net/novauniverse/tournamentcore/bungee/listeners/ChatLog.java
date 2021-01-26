package net.novauniverse.tournamentcore.bungee.listeners;

import java.sql.PreparedStatement;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.async.ExecuteUpdateAsyncCallback;
import net.zeeraa.novacore.commons.log.Log;

public class ChatLog implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(ChatEvent e) {
		if (e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) e.getSender();

			String sql = "INSERT INTO `global_chat_log` (`id`, `uuid`, `username`, `timestamp`, `server_name`, `content`, `is_command`, `canceled`) VALUES (NULL, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";
			try {
				PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setString(1, player.getUniqueId().toString());
				ps.setString(2, player.getName());
				ps.setString(3, player.getServer().getInfo().getName());
				ps.setString(4, e.getMessage());

				ps.setBoolean(5, e.isCommand());
				ps.setBoolean(6, e.isCancelled());

				DBConnection.executeUpdateAsync(ps, new ExecuteUpdateAsyncCallback() {
					@Override
					public void onExecute(int result, Exception exception) {
						if (exception != null) {
							Log.error("Failed to log chat message. Cause: " + exception.getClass().getName() + " : " + exception.getMessage());
							exception.printStackTrace();
						}
					}
				});
			} catch (Exception ex) {
				Log.error("Failed to log chat message. Cause: " + ex.getClass().getName() + " : " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
}