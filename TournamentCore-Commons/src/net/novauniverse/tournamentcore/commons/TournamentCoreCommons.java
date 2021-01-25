package net.novauniverse.tournamentcore.commons;

import net.zeeraa.novacore.commons.database.DBConnection;

public class TournamentCoreCommons {
	private static DBConnection dbConnection;

	public static void setDBConnection(DBConnection dbConnection) {
		TournamentCoreCommons.dbConnection = dbConnection;
	}

	public static DBConnection getDBConnection() {
		return TournamentCoreCommons.dbConnection;
	}
}
