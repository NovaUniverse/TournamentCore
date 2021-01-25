package net.novauniverse.tournamentcore.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.novauniverse.tournamentcore.team.TournamentCoreTeam;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTarget;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTrackerTarget;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;

public class TCCompassTracker implements CompassTrackerTarget {
	@Override
	public CompassTarget getCompassTarget(Player player) {
		if (GameManager.getInstance().hasGame()) {
			List<UUID> players = new ArrayList<UUID>(GameManager.getInstance().getActiveGame().getPlayers());

			players.remove(player.getUniqueId());

			double closestDistance = Double.MAX_VALUE;
			CompassTarget result = null;

			TournamentCoreTeam team = null;
			if (NovaCore.getInstance().hasTeamManager()) {
				team = (TournamentCoreTeam) TournamentCore.getInstance().getTeamManager().getPlayerTeam(player);
			}

			for (UUID uuid : players) {
				Player p = Bukkit.getServer().getPlayer(uuid);

				if (p != null) {
					if (p.isOnline()) {
						if (GameManager.getInstance().hasGame()) {
							if (!GameManager.getInstance().getActiveGame().getPlayers().contains(p.getUniqueId())) {
								//Log.trace(player.getName() + " Ignoring player not in game " + p.getName());
								continue;
							}
						}
						if (p.getLocation().getWorld() == player.getLocation().getWorld()) {
							if (team != null) {
								TournamentCoreTeam p2team = (TournamentCoreTeam) NovaCore.getInstance().getTeamManager().getPlayerTeam(p);

								if (p2team != null) {
									if (team.getTeamUuid().toString().equalsIgnoreCase(p2team.getTeamUuid().toString())) {
										//Log.trace(player.getName() + " Ignoring same team player " + p.getName());
										continue;
									}
								}
							}

							double dist = player.getLocation().distance(p.getLocation());

							if (dist < closestDistance) {
								closestDistance = dist;
								result = new CompassTarget(p.getLocation(), LanguageManager.getString(player, "tournamentcore.game.tracker.tracking_player", p.getName()));
							}
						}
					} else {
						//Log.trace(player.getName() + " Ignoring offline " + uuid.toString());
					}
				} else {
					//Log.trace(player.getName() + " Ignoring missing " + uuid.toString());
				}
			}

			return result;
		}
		return null;
	}
}