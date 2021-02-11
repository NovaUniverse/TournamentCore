package net.novauniverse.tournamentcore.spigot.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.novauniverse.tournamentcore.spigot.score.ScoreManager;
import net.novauniverse.tournamentcore.team.TournamentCoreTeam;
import net.novauniverse.tournamentcore.team.TournamentCoreTeamManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;

public class TCScoreboard extends NovaModule implements Listener {
	private int taskId;

	private boolean gameCountdownShown;

	public static final int COUNTDOWN_LINE = 6;

	@Override
	public String getName() {
		return "TCScoreboard";
	}

	@Override
	public void onLoad() {
		this.taskId = -1;
		this.gameCountdownShown = false;
	}

	@Override
	public void onEnable() {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentCore.getInstance(), new Runnable() {

				@Override
				public void run() {
					double[] recentTps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps();

					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						int playerScore = ScoreManager.getInstance().getPlayerScore(player);
						int teamScore = 0;

						TournamentCoreTeam team = (TournamentCoreTeam) TournamentCoreTeamManager.getInstance().getPlayerTeam(player);

						if (team != null) {
							teamScore = team.getScore();
						}

						NetherBoardScoreboard.getInstance().setPlayerLine(2, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentcore.scoreboard.score") + ChatColor.AQUA + playerScore);
						NetherBoardScoreboard.getInstance().setPlayerLine(3, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentcore.scoreboard.kills") + ChatColor.AQUA + PlayerKillCache.getInstance().getPlayerKills(player.getUniqueId()));
						NetherBoardScoreboard.getInstance().setPlayerLine(4, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentcore.scoreboard.team_score") + ChatColor.AQUA + teamScore);

						int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);

						NetherBoardScoreboard.getInstance().setPlayerLine(12, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentcore.scoreboard.your_ping") + formatPing(ping) + "ms " + (ping > 800 ? ChatColor.YELLOW + TextUtils.ICON_WARNING : ""));

						if (recentTps.length > 0) {
							double tps = recentTps[0];
							NetherBoardScoreboard.getInstance().setPlayerLine(13, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentcore.scoreboard.average_tps") + formatTps(tps) + (tps < 18 ? " " + ChatColor.RED + TextUtils.ICON_WARNING : ""));
						}
					}

					if (GameManager.getInstance().getCountdown().isCountdownRunning()) {
						gameCountdownShown = true;

						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
							NetherBoardScoreboard.getInstance().setPlayerLine(COUNTDOWN_LINE, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentcore.scoreboard.starting_in") + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(GameManager.getInstance().getCountdown().getTimeLeft()));
						}
					} else {
						if (gameCountdownShown) {
							gameCountdownShown = false;
							for (Player player : Bukkit.getServer().getOnlinePlayers()) {
								NetherBoardScoreboard.getInstance().clearPlayerLine(COUNTDOWN_LINE, player);
							}
						}
					}
				}
			}, 10L, 10L);
		}
	}

	@Override
	public void onDisable() {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
	}

	private String formatPing(int ping) {
		ChatColor color = ChatColor.DARK_RED;

		if (ping < 200) {
			color = ChatColor.GREEN;
		} else if (ping < 400) {
			color = ChatColor.DARK_GREEN;
		} else if (ping < 600) {
			color = ChatColor.YELLOW;
		} else if (ping < 800) {
			color = ChatColor.RED;
		}

		return color + "" + ping;
	}

	private String formatTps(double tps) {
		return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
	}
}