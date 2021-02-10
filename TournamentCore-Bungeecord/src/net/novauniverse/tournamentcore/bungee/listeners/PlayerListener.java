package net.novauniverse.tournamentcore.bungee.listeners;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;

public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PostLoginEvent e) {
		try {
			String sql = "SELECT id FROM players WHERE uuid = ? LIMIT 1";
			PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.setString(1, e.getPlayer().getUniqueId().toString());

			ResultSet rs = ps.executeQuery();

			if (!rs.next()) {
				if (e.getPlayer().hasPermission("tournamentcore.bypasswhitelist")) {
					addOrUpdatePlayer(e.getPlayer());
				} else {
					e.getPlayer().disconnect(new TextComponent(ChatColor.RED + "You are not in the list of players thats allowed to join the tournament right now.\n\nIf this is incorrect please contact an admin"));
				}
			} else {
				addOrUpdatePlayer(e.getPlayer());
			}

			rs.close();
			ps.close();

		} catch (Exception ex) {
			e.getPlayer().disconnect(new TextComponent(ChatColor.RED + "Failed to connect to the database.\n\n " + ex.getClass().getName() + "\n\nPlease report this to an admin"));
			ex.printStackTrace();
		}
	}
	
	private void addOrUpdatePlayer(ProxiedPlayer player) {
		boolean playerFound = false;

		try {
			String sql = "SELECT id, has_joined FROM players WHERE uuid = ?";
			PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.setString(1, player.getUniqueId().toString());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				playerFound = true;
			}

			rs.close();
			ps.close();
		} catch (Exception ee) {
			ee.printStackTrace();
			player.disconnect(new TextComponent(ChatColor.DARK_RED + ee.getClass().getName() + "\n\n" + ee.getMessage()));
			return;
		}

		if (!playerFound) {
			try {
				String sql = "INSERT INTO players (uuid, username, has_joined) VALUES (?, ?, 1)";
				PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setString(1, player.getUniqueId().toString());
				ps.setString(2, player.getName());

				ps.executeUpdate();

				ps.close();
			} catch (Exception ee) {
				ee.printStackTrace();
				player.disconnect(new TextComponent(ChatColor.DARK_RED + ee.getClass().getName() + "\n\n" + ee.getMessage()));
				return;
			}
		}

		try {
			String sql = "UPDATE players SET username = ?, has_joined = 1 WHERE uuid = ?";
			PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.setString(1, player.getName());
			ps.setString(2, player.getUniqueId().toString());

			ps.executeUpdate();

			ps.close();
		} catch (Exception ee) {
			ee.printStackTrace();
			player.disconnect(new TextComponent(ChatColor.DARK_RED + ee.getClass().getName() + "\n\n" + ee.getMessage()));
			return;
		}
	}
}