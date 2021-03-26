package net.novauniverse.tournamentcore.spigot.modules.gamespecific;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import net.novauniverse.games.spleef.game.Spleef;
import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;

public class SpleefManager extends NovaModule implements Listener {
	public static final int SPLEEF_DECAY_LINE = 5;

	private int taskId;
	private boolean decayLineShown;

	@Override
	public String getName() {
		return "TCSpleefManager";
	}

	@Override
	public void onLoad() {
		this.taskId = -1;
		this.decayLineShown = false;
	}

	@Override
	public void onEnable() {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentCore.getInstance(), new Runnable() {
				@Override
				public void run() {
					Spleef spleef = (Spleef) GameManager.getInstance().getActiveGame();

					if (spleef.hasStarted()) {
						if (spleef.hasActiveMap()) {
							if (spleef.getDecayModule() != null) {
								if (spleef.getDecayModule().getStartTrigger().isRunning()) {
									decayLineShown = true;

									int timeLeft = (int) spleef.getDecayModule().getStartTrigger().getTicksLeft() / 20;

									ChatColor color;

									if (timeLeft < (spleef.getDecayModule().getBeginAfter() / 3)) {
										color = ChatColor.RED;
									} else if (timeLeft < (spleef.getDecayModule().getBeginAfter() / 2)) {
										color = ChatColor.YELLOW;
									} else {
										color = ChatColor.GREEN;
									}

									NetherBoardScoreboard.getInstance().setGlobalLine(SPLEEF_DECAY_LINE, ChatColor.GOLD + "Decay in: " + color + TextUtils.secondsToHoursMinutes(timeLeft));
								} else {
									if (decayLineShown) {
										NetherBoardScoreboard.getInstance().clearGlobalLine(SPLEEF_DECAY_LINE);
										decayLineShown = false;
									}
								}
							}
						}
					}
				}
			}, 5L, 5L);
		}
	}

	@Override
	public void onDisable() {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
	}
}