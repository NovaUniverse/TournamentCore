package net.novauniverse.tournamentcore.bungee.webapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;

import net.novauniverse.tournamentcore.bungee.webapi.handlers.BroadcastHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.SendPlayerHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.SendPlayersHandler;
import net.novauniverse.tournamentcore.bungee.webapi.handlers.StatusHandler;

@SuppressWarnings("restriction")
public class WebServer {
	// https://stackoverflow.com/a/25945740

	private HttpServer httpServer;

	public WebServer(int port) throws IOException {
		httpServer = HttpServer.create(new InetSocketAddress(port), 0);

		httpServer.createContext("/api/status", new StatusHandler());

		httpServer.createContext("/api/broadcast", new BroadcastHandler());

		httpServer.createContext("/api/send_player", new SendPlayerHandler());
		httpServer.createContext("/api/send_players", new SendPlayersHandler());

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
	 * @return map
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