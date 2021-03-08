package net.novauniverse.tournamentcore.bungee.webapi.data;

public class TeamData {
	private int teamNumber;
	private int score;

	public TeamData(int teamNumber, int score) {
		this.teamNumber = teamNumber;
		this.score = score;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public int getScore() {
		return score;
	}
}