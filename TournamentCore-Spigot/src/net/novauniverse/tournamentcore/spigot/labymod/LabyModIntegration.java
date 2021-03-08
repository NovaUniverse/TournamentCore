package net.novauniverse.tournamentcore.spigot.labymod;

import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import net.labymod.serverapi.bukkit.LabyModPlugin;

public class LabyModIntegration {

	public static void sendWatermark(Player player, boolean visible) {
		JsonObject object = new JsonObject();

		// Visibility
		object.addProperty("visible", visible);

		// Send to LabyMod using the API
		LabyModPlugin.getInstance().sendServerMessage(player, "watermark", object);
	}
	
	public static void sendCineScope(Player player, int coveragePercent, long duration) {
		JsonObject object = new JsonObject();

		// Cinescope height (0% - 50%)
		object.addProperty("coverage", coveragePercent);

		// Duration
		object.addProperty("duration", duration);

		// Send to LabyMod using the API
		LabyModPlugin.getInstance().sendServerMessage(player, "cinescopes", object);
	}
}