package net.novauniverse.tournamentcore.bungee.webapi.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.tournamentcore.bungee.TournamentCore;
import net.novauniverse.tournamentcore.bungee.webapi.data.PlayerData;
import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class StatusHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		JSONArray servers = new JSONArray();

		for (String key : ProxyServer.getInstance().getServers().keySet()) {
			ServerInfo serverInfo = ProxyServer.getInstance().getServers().get(key);

			JSONObject server = new JSONObject();

			server.put("name", serverInfo.getName());
			server.put("player_count", serverInfo.getPlayers().size());

			servers.put(server);
		}

		List<PlayerData> playerDataList = new ArrayList<PlayerData>();

		try {
			String sql = "SELECT p.uuid AS uuid, p.score AS player_score, p.kills AS kills, t.team_number AS team_number, t.score AS team_score FROM players AS p LEFT JOIN teams AS t ON t.team_number = p.team_number";
			PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				PlayerData playerData = new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getInt("kills"), rs.getInt("player_score"), rs.getInt("team_score"), (teamNumber == 0 ? -1 : teamNumber));

				playerDataList.add(playerData);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONArray players = new JSONArray();

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			JSONObject p = new JSONObject();

			int kills = 0;
			int score = 0;
			int teamScore = 0;
			int teamNumber = -1;

			for (PlayerData pd : playerDataList) {
				if (pd.getUuid().toString().equalsIgnoreCase(player.getUniqueId().toString())) {

					kills = pd.getKills();
					score = pd.getScore();
					teamScore = pd.getTeamScore();
					teamNumber = pd.getTeamNumber();

					break;
				}
			}

			p.put("uuid", player.getUniqueId().toString());
			p.put("name", player.getName());
			p.put("server", player.getServer().getInfo().getName());
			p.put("ping", player.getPing());
			p.put("kills", kills);
			p.put("score", score);
			p.put("team_score", teamScore);
			p.put("team_number", teamNumber);

			players.put(p);
		}

		json.put("tournament_name", TournamentCore.getInstance().getTounamentName());
		
		json.put("online_players", ProxyServer.getInstance().getPlayers().size());
		json.put("proxy_software", ProxyServer.getInstance().getName());
		json.put("proxy_software_version", ProxyServer.getInstance().getVersion());
		json.put("total_memory", Runtime.getRuntime().totalMemory());
		json.put("free_memory", Runtime.getRuntime().freeMemory());
		
		json.put("servers", servers);
		json.put("players", players);

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}