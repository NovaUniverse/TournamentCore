package net.novauniverse.tournamentcore.bungee;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;

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

	private String tounamentName;

	public static TournamentCore getInstance() {
		return instance;
	}

	public String getTounamentName() {
		return tounamentName;
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

		tounamentName = TournamentCoreCommons.getTournamentName();

		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatLog());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new TCPluginMessageListener());

		File wwwAppFile = new File(getDataFolder().getPath() + File.separator + "www_app");

		try {
			FileUtils.forceMkdir(wwwAppFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		File teamEditorFile = new File(getDataFolder().getPath() + File.separator + "www_app" + File.separator + "team_editor");

		try {
			if (teamEditorFile.exists()) {
				FileUtils.forceDelete(teamEditorFile);
			}

			FileUtils.forceMkdir(teamEditorFile);

			JarFile jf = null;
			try {
				String s = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();

				System.out.println(s);

				jf = new JarFile(s);

				Enumeration<JarEntry> entries = jf.entries();
				while (entries.hasMoreElements()) {
					JarEntry je = entries.nextElement();
					System.out.println(je.getName());

					if (je.getName().startsWith("TournamentCore-Team-Editor/")) {

						if (je.isDirectory()) {
							// System.out.println("Ignore directory");
							continue;
						}

						File targetFile = new File(teamEditorFile.getPath() + File.separator + je.getName().replace("TournamentCore-Team-Editor/", ""));

						// System.out.println("Target: " + targetFile.getPath());

						FileUtils.forceMkdirParent(targetFile);

						URL inputUrl = getClass().getResource("/" + je.getName());
						FileUtils.copyURLToFile(inputUrl, targetFile);
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					jf.close();
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			int port = getConfig().getInt("web-server-port");
			Log.info("Starting web server on port ");
			webServer = new WebServer(port, wwwAppFile.getPath());
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