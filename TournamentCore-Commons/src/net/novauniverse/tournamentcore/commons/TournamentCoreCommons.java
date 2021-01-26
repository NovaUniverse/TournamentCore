package net.novauniverse.tournamentcore.commons;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.zeeraa.novacore.commons.database.DBConnection;

public class TournamentCoreCommons {
	private static DBConnection dbConnection;

	public static void setDBConnection(DBConnection dbConnection) {
		TournamentCoreCommons.dbConnection = dbConnection;
	}

	public static DBConnection getDBConnection() {
		return TournamentCoreCommons.dbConnection;
	}

	public static String getTournamentName() {
		String result = null;
		try {
			String sql = "SELECT data_value FROM tc_data WHERE data_key = \"tournament_name\"";
			PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("data_value");
			}

			rs.close();
			ps.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}

		return result;
	}

	public static void setTournamentName(String name) throws SQLException {
		String sql = "UPDATE tc_data SET data_value = ? WHERE data_key = \"tournament_name\"";
		PreparedStatement ps = TournamentCoreCommons.getDBConnection().getConnection().prepareStatement(sql);

		ps.setString(1, name);

		ps.executeUpdate();

		ps.close();
	}
}