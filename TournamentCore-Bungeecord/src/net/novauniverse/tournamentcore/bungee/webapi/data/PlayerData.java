package net.novauniverse.tournamentcore.bungee.webapi.data;

import java.util.UUID;

public class PlayerData {
	private UUID uuid;

	private int kills;
	private int score;
	private int teamScore;
	private int teamNumber;

	public PlayerData(UUID uuid, int kills, int score, int teamScore, int teamNumber) {
		this.uuid = uuid;
		this.kills = kills;
		this.score = score;
		this.teamScore = teamScore;
		this.teamNumber = teamNumber;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getKills() {
		return kills;
	}

	public int getScore() {
		return score;
	}

	public int getTeamScore() {
		return teamScore;
	}

	public int getTeamNumber() {
		return teamNumber;
	}
}
