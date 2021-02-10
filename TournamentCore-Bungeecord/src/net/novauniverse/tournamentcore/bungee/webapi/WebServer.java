package net.novauniverse.tournamentcore.bungee.webapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;

import net.novauniverse.tournamentcore.bungee.TournamentCore;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.BroadcastHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.ClearPlayersHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.ExportTeamDataHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.FaviconHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.ResetHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.SendPlayerHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.SendPlayersHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.SetTournamentNameHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.StartGameHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.StaticFileHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.StatusHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.UpploadTeamHandler;

@SuppressWarnings("restriction")
public class WebServer {
	// https://stackoverflow.com/a/25945740

	private HttpServer httpServer;

	public WebServer(int port, String appRoot) throws IOException {
		httpServer = HttpServer.create(new InetSocketAddress(port), 0);
		
		// System related
		httpServer.createContext("/api/status", new StatusHandler());
		httpServer.createContext("/api/broadcast", new BroadcastHandler());
		httpServer.createContext("/api/reset", new ResetHandler());
		httpServer.createContext("/api/clear_players", new ClearPlayersHandler());
		
		// System options
		httpServer.createContext("/api/set_tournament_name", new SetTournamentNameHandler());
		
		// Player related
		httpServer.createContext("/api/send_player", new SendPlayerHandler());
		httpServer.createContext("/api/send_players", new SendPlayersHandler());

		// Game related
		httpServer.createContext("/api/start_game", new StartGameHandler());

		// Team related
		httpServer.createContext("/api/export_team_data", new ExportTeamDataHandler());
		httpServer.createContext("/api/uppload_team", new UpploadTeamHandler());

		// File indexes
		StaticFileHandler sfh = new StaticFileHandler("/app/", appRoot, "index.html");
		httpServer.createContext("/app", sfh);
		
		// Icon
		httpServer.createContext("/favicon.ico", new FaviconHandler(TournamentCore.getInstance().getDataFolder().getPath()));

		// Start the server
		httpServer.setExecutor(null);
		httpServer.start();
	}

	public void stop() {
		httpServer.stop(10);
	}

	/**
	 * returns the url parameters in a map
	 * 
	 * @param query The query
	 * @return map result
	 */
	public static Map<String, String> queryToMap(String query) {
		try {
			Map<String, String> result = new HashMap<String, String>();
			for (String param : query.split("&")) {
				String pair[] = param.split("=");
				if (pair.length > 1) {
					result.put(pair[0], pair[1]);
				} else {
					result.put(pair[0], "");
				}
			}
			return result;
		} catch (Exception e) {
			return new HashMap<String, String>();
		}
	}
}