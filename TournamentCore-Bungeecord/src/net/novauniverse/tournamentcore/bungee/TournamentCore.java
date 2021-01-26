package net.novauniverse.tournamentcore.bungee;

import java.sql.SQLException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.novauniverse.tournamentcore.bungee.listeners.ChatLog;
import net.novauniverse.tournamentcore.bungee.listeners.TCPluginMessageListener;
import net.novauniverse.tournamentcore.bungee.webapi.WebServer;
import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;
import net.zeeraa.novacore.bungeecord.novaplugin.NovaPlugin;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;

public class TournamentCore extends NovaPlugin implements Listener {
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
		saveDefaultConfiguration();

		DBCredentials dbCredentials = new DBCredentials(getConfig().getString("mysql.driver"), getConfig().getString("mysql.host"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"), getConfig().getString("mysql.database"));

		try {
			DBConnection dbConnection;
			dbConnection = new DBConnection();
			dbConnection.connect(dbCredentials);
			dbConnection.startKeepAliveTask();

			TournamentCoreCommons.setDBConnection(dbConnection);
		} catch (ClassNotFoundException | SQLException e) {
			Log.fatal("MCF2BungeecordPlugin", "Failed to connect to the database");
			e.printStackTrace();
			return;
		}

		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatLog());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new TCPluginMessageListener());

		try {
			webServer = new WebServer(8123);
		} catch (Exception e) {
			e.printStackTrace();
		}

		getProxy().registerChannel("TCData");
	}

	@Override
	public void onDisable() {
		if (webServer != null) {
			webServer.stop();
		}
	}
}