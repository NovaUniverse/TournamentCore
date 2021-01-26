package net.novauniverse.tournamentcore.bungee.webapi.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.CallableStatement;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;

@SuppressWarnings("restriction")
public class ResetHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		try {
			String sql = "{ CALL reset_data() }";
			CallableStatement cs = TournamentCoreCommons.getDBConnection().getConnection().prepareCall(sql);

			cs.execute();
			cs.close();

			json.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();

			json.put("success", false);
			json.put("error", "failed");
			json.put("message", e.getClass().getName() + " " + e.getMessage());
		}

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}