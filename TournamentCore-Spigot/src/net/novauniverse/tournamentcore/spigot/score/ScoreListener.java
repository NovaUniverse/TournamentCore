package net.novauniverse.tournamentcore.spigot.score;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.tournamentcore.spigot.modules.PlayerKillCache;
import net.novauniverse.tournamentcore.team.TournamentCoreTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerWinEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.TeamEliminatedEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.ProjectileUtils;

public class ScoreListener implements Listener {
	private boolean killScoreEnabled;
	private int killScore;

	private boolean winScoreEnabled;
	private int[] winScore;

	private boolean participationScoreEnabled;
	private int participationScore;

	public ScoreListener(boolean killScoreEnabled, int killScore, boolean winScoreEnabled, int[] winScore, boolean participationScoreEnabled, int participationScore) {
		this.killScoreEnabled = killScoreEnabled;
		this.killScore = killScore;

		this.winScoreEnabled = winScoreEnabled;
		this.winScore = winScore;

		this.participationScoreEnabled = participationScoreEnabled;
		this.participationScore = participationScore;

		Log.info("Kill score: " + this.killScoreEnabled + " Win score: " + this.winScoreEnabled + " Participation score: " + participationScoreEnabled);
	}

	public boolean isKillScoreEnabled() {
		return killScoreEnabled;
	}

	public boolean isWinScoreEnabled() {
		return winScoreEnabled;
	}

	public boolean isParticipationScoreEnabled() {
		return participationScoreEnabled;
	}

	public int getKillScore() {
		return killScore;
	}

	public int[] getWinScore() {
		return winScore;
	}

	public int getParticipationScore() {
		return participationScore;
	}

	public void setKillScore(int killScore) {
		this.killScore = killScore;
	}

	public void setWinScore(int[] winScore) {
		this.winScore = winScore;
	}

	public void setParticipationScore(int participationScore) {
		this.participationScore = participationScore;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		Log.trace("PlayerEliminatedEvent. " + e.getPlayer().getUniqueId());
		if (participationScoreEnabled) {
			if (GameManager.getInstance().hasGame()) {
				if (GameManager.getInstance().getActiveGame().hasStarted()) {
					for (UUID uuid : GameManager.getInstance().getActiveGame().getPlayers()) {
						Player player = Bukkit.getServer().getPlayer(uuid);
						if (player != null) {
							if (player.isOnline()) {
								ScoreManager.getInstance().addPlayerScore(player, participationScore, true);
								player.sendMessage(ChatColor.GRAY + "+" + participationScore + " Participation score");
							}
						}
					}
				}
			}
		}

		if (killScoreEnabled) {
			if (e.getPlayer().isOnline()) {
				Entity killer = e.getKiller();

				Player killerPlayer = null;

				if (ProjectileUtils.isProjectile(killer)) {
					Entity shooter = ProjectileUtils.getProjectileShooterEntity(killer);

					if (shooter != null) {
						if (shooter instanceof Player) {
							killerPlayer = (Player) shooter;
						}
					}
				} else if (killer instanceof Player) {
					killerPlayer = (Player) killer;
				}

				if (killerPlayer != null) {
					ScoreManager.getInstance().addPlayerScore(killerPlayer, killScore, true);
					PlayerKillCache.getInstance().invalidate(killerPlayer);
				}
			}
		}

		if (winScoreEnabled) {
			if (e.getPlacement() > 1) {
				addPlayerPlacementScore(e.getPlayer(), e.getPlacement());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeamEliminated(TeamEliminatedEvent e) {
		Log.trace("TeamEliminatedEvent. " + e.getTeam().getTeamUuid());
		if (winScoreEnabled) {
			if (e.getPlacement() > 1) {
				addTeamPlacementScore(e.getTeam(), e.getPlacement());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeamWin(TeamWinEvent e) {
		Log.trace("TeamWinEvent called");
		addTeamPlacementScore(e.getTeam(), 1);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWin(PlayerWinEvent e) {
		Log.trace("PlayerWinEvent called");
		addPlayerPlacementScore(e.getPlayer(), 1);
	}

	private void addPlayerPlacementScore(OfflinePlayer player, int placement) {
		Log.trace("ScoreListener.addPlayerPlacementScore()");
		if (!GameManager.getInstance().isUseTeams()) {
			if (placement <= winScore.length) {
				int score = winScore[placement - 1];

				ScoreManager.getInstance().addPlayerScore(player, score, false);
				if(player.isOnline()) {
					((Player)player).sendMessage(ChatColor.GRAY + "+" + score + " score");
				}
			}
		}
	}

	private void addTeamPlacementScore(Team team, int placement) {
		Log.trace("ScoreListener.addTeamPlacementScore()");
		if (placement <= winScore.length) {
			int score = winScore[placement - 1];

			double individualScore = Math.ceil(((double) score) / ((double) team.getMembers().size()));

			for (UUID uuid : team.getMembers()) {
				ScoreManager.getInstance().addPlayerScore(uuid, (int) individualScore, false);
			}

			ScoreManager.getInstance().addTeamScore((TournamentCoreTeam) team, score);
			
			team.sendMessage(ChatColor.GRAY + "+" + score + " score (Shared with team members)");
		}
	}
}