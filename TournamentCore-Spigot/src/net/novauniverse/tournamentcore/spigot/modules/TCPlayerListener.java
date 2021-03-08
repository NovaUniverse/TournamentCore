package net.novauniverse.tournamentcore.spigot.modules;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class TCPlayerListener extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TCPlayerListener";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerLoginEvent(PlayerLoginEvent e) {
		try {
			String sql = "SELECT id FROM players WHERE uuid = ? LIMIT 1";
			PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.setString(1, e.getPlayer().getUniqueId().toString());

			ResultSet rs = ps.executeQuery();

			if (!rs.next()) {
				if (!e.getPlayer().hasPermission("tournamentcore.bypasswhitelist")) {
					e.setResult(Result.KICK_OTHER);
					e.setKickMessage(ChatColor.RED + "You are not in the list of players thats allowed to join the tournament right now.\n\nIf you are a staff member try reconnecting");
				}
			}

			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		//LabyModIntegration.sendWatermark(e.getPlayer(), true);
	}
}