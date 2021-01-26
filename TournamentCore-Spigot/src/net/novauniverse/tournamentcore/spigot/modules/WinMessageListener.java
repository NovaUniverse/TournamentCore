package net.novauniverse.tournamentcore.spigot.modules;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerWinEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.teams.Team;

public class WinMessageListener extends NovaModule implements Listener{
	@Override
	public String getName() {
		return "TCWinMessageListener";
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerWin(PlayerWinEvent e) {
		ChatColor color = ChatColor.AQUA;

		Team team = TournamentCore.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());

		if (team != null) {
			color = team.getTeamColor();
		}

		// Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD +
		// "GAME OVER> " + ChatColor.GOLD + ChatColor.BOLD + "Winning player: " + color
		// + ChatColor.BOLD + e.getPlayer().getName());
		LanguageManager.broadcast("tournamentcore.game.gameover.winner.player", color.toString(), e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeamWin(TeamWinEvent e) {
		ChatColor color = e.getTeam().getTeamColor();

		// Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD +
		// "GAME OVER> " + ChatColor.GOLD + ChatColor.BOLD + "Winning team: " + color +
		// ChatColor.BOLD + "Team " + ((MCFTeam) e.getTeam()).getTeamNumber());
		LanguageManager.broadcast("tournamentcore.game.gameover.winner.team", color.toString(), e.getTeam().getDisplayName());
	}
}