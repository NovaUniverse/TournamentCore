package net.novauniverse.tournamentcore.bungee.webapi.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;

@SuppressWarnings("restriction")
public class UpploadTeamHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject result = new JSONObject();
		
		
		JSONArray teamData = null;
		
		try {
			String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
			
			teamData = new JSONArray(body);
		} catch(Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
		}
		
		try {
		if(teamData != null) {
			for(int i = 0; i < result.length(); i++) {
				JSONObject player = teamData.getJSONObject(i);
				
				String sql;
				PreparedStatement ps;
				ResultSet rs;
				
				//TODO: execute prepared statement to update or insert player
			}
		}
		}catch(Exception e) {
			result.put("success", false);
			result.put("error", "failed");
			result.put("message", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
		}
		
		
		String response = result.toString(4);
		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}