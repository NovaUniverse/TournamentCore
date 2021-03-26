package net.novauniverse.tournamentcore.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.tournamentcore.spigot.modules.gamespecific.DeathSwapManager;
import net.novauniverse.tournamentcore.spigot.modules.gamespecific.SpleefManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameLoadedEvent;

public class GameEventListeners implements Listener {
	// Events
	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoaded(GameLoadedEvent e) {
		if (e.getGame().getName().equalsIgnoreCase("deathswap")) {
			Log.info("TournamentCore", "Enabling deathswap module");
			ModuleManager.enable(DeathSwapManager.class);
		}

		if (e.getGame().getName().equalsIgnoreCase("spleef")) {
			Log.info("TournamentCore", "Enabling spleef module");
			ModuleManager.enable(SpleefManager.class);
		}
	}
}