package net.novauniverse.tournamentcore.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zeeraa.novacore.commons.log.Log;

public class TCPluginMessageListener implements Listener {
	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase("TCData")) {
			e.setCancelled(true);
		}

		if (e.getSender() instanceof ProxiedPlayer) {
			Log.warn("TCPluginMessageListener", "Illegal sender for TCData plugin message: " + e.getSender().toString());
			return;
		}
	}
}