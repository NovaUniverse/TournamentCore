package net.novauniverse.tournamentcore.bungee.listeners;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TCPluginMessageListener implements Listener {
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase("TCData")) {
			e.setCancelled(true);
		}
	}
}