package net.novauniverse.tournamentcore.bungee.webapi.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class UpploadTeamHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject result = new JSONObject();

		JSONArray teamData = null;

		boolean failed = false;

		try {
			String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

			teamData = new JSONArray(body);
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			failed = true;
		}

		if (!failed) {
			try {
				String sql = "UPDATE players SET team_number = -1";
				PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.executeUpdate();
				ps.close();
			} catch (Exception e) {
				result.put("success", false);
				result.put("error", "failed");
				result.put("message", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
				failed = true;
			}
		}

		if (!failed) {
			try {
				for (int i = 0; i < teamData.length(); i++) {
					JSONObject player = teamData.getJSONObject(i);

					String sql;
					PreparedStatement ps;

					sql = "CALL `set_player_team`(?, ?, ?)";

					if (player.getInt("team_number") == 0) {
						Log.error("UpploadTeamHandler", "Invalid team number: 0");
						continue;
					}

					ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, player.getString("uuid"));
					ps.setString(2, player.getString("username"));
					ps.setInt(3, player.getInt("team_number"));

					//System.out.println("p1: " + player.getString("uuid") + " p2: " + player.getString("username") + " p3: " + player.getInt("team_number"));

					ps.executeUpdate();

					ps.close();
				}

				result.put("success", true);
			} catch (Exception e) {
				result.put("success", false);
				result.put("error", "failed");
				result.put("message", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			}
		}

		String response = result.toString(4);
		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}