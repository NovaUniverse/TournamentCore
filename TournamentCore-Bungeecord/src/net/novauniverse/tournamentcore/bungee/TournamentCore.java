package net.novauniverse.tournamentcore.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.novauniverse.tournamentcore.bungee.webapi.WebServer;

public class TournamentCore extends Plugin {
	private static TournamentCore instance;
	
	private WebServer webServer;
	
	public static TournamentCore getInstance() {
		return instance;
	}
	
	@Override
	public void onLoad() {
		TournamentCore.instance = this;
		
		this.webServer = null;
	}
	
	@Override
	public void onEnable() {
		try {
		webServer = new WebServer(8123);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		if(webServer != null) {
			webServer.stop();
		}
	}
}