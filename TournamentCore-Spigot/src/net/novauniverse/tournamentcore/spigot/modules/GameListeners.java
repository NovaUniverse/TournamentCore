package net.novauniverse.tournamentcore.spigot.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.novauniverse.tournamentcore.spigot.database.TournamentCoreDB;
import net.novauniverse.tournamentcore.spigot.gamespecific.UHCManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameLoadedEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class GameListeners extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TCGameListeners";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoaded(GameLoadedEvent e) {
		NetherBoardScoreboard.getInstance().setGlobalLine(0, ChatColor.YELLOW + "" + ChatColor.BOLD + e.getGame().getDisplayName());

		if (e.getGame().getName().equalsIgnoreCase("bingo")) {
			Log.info("TournamentCore", "Bingo manager enabled");
			/* oduleManager.enable(BingoManger.class); */
			TournamentCore.getInstance().setTopEnabled(true);
			Log.info("TournamentCore", "/top command enabled");
		}

		if (e.getGame().getName().equalsIgnoreCase("uhc")) {
			Log.info("TournamentCore", "UHC manager enabled");
			ModuleManager.enable(UHCManager.class);
			TournamentCore.getInstance().setTopEnabled(true);
			Log.info("TournamentCore", "/top command enabled");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		try {
			TournamentCoreDB.setActiveServer(TournamentCore.getServerName());
		} catch (Exception ex) {
			Log.error("Failed to set active server name");
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		try {
			TournamentCoreDB.setActiveServer(null);
		} catch (Exception ex) {
			Log.error("Failed to reset active server name");
			ex.printStackTrace();
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentCore.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(LanguageManager.getString(p, "tournamentcore.game.sending_you_to_lobby_10_seconds"));
				}

				Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentCore.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
							Bukkit.getScheduler().runTaskLater(TournamentCore.getInstance(), new Runnable() {
								@Override
								public void run() {
									BungeecordUtils.sendToServer(player, TournamentCore.getInstance().getLobbyServer());
								}
							}, 4L);
						}

						Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentCore.getInstance(), new Runnable() {
							@Override
							public void run() {
								for (Player p : Bukkit.getServer().getOnlinePlayers()) {
									p.kickPlayer(LanguageManager.getString(p, "tournamentcore.game.server.restarting", e.getGame()));
								}
								Bukkit.getServer().shutdown();
							}
						}, 40L);
					}
				}, 200L);
			}
		}, 100L);
	}
}