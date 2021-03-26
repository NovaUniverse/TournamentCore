package net.novauniverse.tournamentcore.spigot.gamesetup;

import java.io.File;

import net.novauniverse.tournamentcore.spigot.messages.TCActionBarCombatTagMessage;
import net.novauniverse.tournamentcore.spigot.messages.TCTeamEliminationMessage;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.gamelobby.GameLobby;

public class GameSetupProcess {
	public static final void init() {
		GameManager.getInstance().addCombatTagMessage(new TCActionBarCombatTagMessage());
		
		GameManager.getInstance().setUseTeams(true);
		GameManager.getInstance().addCombatTagMessage(new TCActionBarCombatTagMessage());
		GameManager.getInstance().setTeamEliminationMessage(new TCTeamEliminationMessage());
	}
	
	public static final void readMapFiles(File gameLobbyFolder, File worldFolder) {
		GameLobby.getInstance().getMapReader().loadAll(gameLobbyFolder, worldFolder);
	}
}