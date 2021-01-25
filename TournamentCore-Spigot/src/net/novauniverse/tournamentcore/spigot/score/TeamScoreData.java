package net.novauniverse.tournamentcore.spigot.score;

import org.bukkit.ChatColor;

import net.novauniverse.tournamentcore.team.TournamentCoreTeam;

public class TeamScoreData extends ScoreData {
	private TournamentCoreTeam team;

	public TeamScoreData(TournamentCoreTeam team) {
		super(team.getScore());
		this.team = team;
	}

	public TeamScoreData(TournamentCoreTeam team, int score) {
		super(score);
		this.team = team;
	}

	public Integer getTeamNumber() {
		return team.getTeamNumber();
	}

	public TournamentCoreTeam getTeam() {
		return team;
	}

	@Override
	public String toString() {
		String teamName;

		if (team.getMembers().size() > 0) {
			teamName = team.getTeamColor() + team.getMemberString();
		} else {
			teamName = team.getTeamColor() + "Team " + team.getTeamNumber();
		}

		return teamName + ChatColor.GOLD + " : " + ChatColor.AQUA + this.getScore();
	}
}