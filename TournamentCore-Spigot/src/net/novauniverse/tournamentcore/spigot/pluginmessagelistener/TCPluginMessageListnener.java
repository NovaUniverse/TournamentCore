package net.novauniverse.tournamentcore.spigot.pluginmessagelistener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;

public class TCPluginMessageListnener implements PluginMessageListener {
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase("TCData")) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();

			switch (subchannel.toLowerCase()) {
			case "start_game":
				if (GameManager.getInstance().isEnabled()) {
					if (GameManager.getInstance().hasGame()) {
						if (!GameManager.getInstance().getCountdown().hasCountdownStarted() && !GameManager.getInstance().getCountdown().hasCountdownFinished()) {
							Log.info("TCPluginMessageListnener", "Starting countdown");
							GameManager.getInstance().getCountdown().startCountdown();
						}
					}
				}
				break;

			default:
				Log.warn("TCPluginMessageListnener", "Reveived invalid sub channel: " + subchannel);
				break;
			}
		}
	}
}