package net.novauniverse.tournamentcore.spigot.modules;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.connorlinfoot.titleapi.TitleAPI;

import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;
import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class TCTabTitle extends NovaModule implements Listener{
	@Override
	public String getName() {
		return "TCTabTitle";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		TitleAPI.sendTabTitle(e.getPlayer(), ChatColor.translateAlternateColorCodes('&', TournamentCoreCommons.getTournamentName() + "\n" + ChatColor.YELLOW + ChatColor.BOLD + TournamentCore.getServerName()), ChatColor.YELLOW + "Powered by novauniverse.net\n"+org.bukkit.ChatColor.YELLOW + "https://discord.gg/4gZSVJ7");
	}
}