package net.novauniverse.tournamentcore.bungee.webapi.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.tournamentcore.bungee.webapi.WebServer;
import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class SetTournamentNameHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		if (params.containsKey("name")) {
			String name = URLDecoder.decode(params.get("name"), StandardCharsets.UTF_8.name());
			Log.info("SetTournamentNameHandler", "Reanaming tournament to " + name);
			try {
				TournamentCoreCommons.setTournamentName(name);
				json.put("success", true);
			} catch (Exception e) {
				json.put("success", false);
				json.put("error", e.getClass().getName());
				json.put("message", e.getClass().getName() + " " + e.getMessage());
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: name");
		}

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}