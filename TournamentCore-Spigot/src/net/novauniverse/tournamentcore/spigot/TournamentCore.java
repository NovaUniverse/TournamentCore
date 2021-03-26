package net.novauniverse.tournamentcore.spigot;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.tournamentcore.commons.TournamentCoreCommons;
import net.novauniverse.tournamentcore.spigot.command.database.DatabaseCommand;
import net.novauniverse.tournamentcore.spigot.command.fly.FlyCommand;
import net.novauniverse.tournamentcore.spigot.command.halt.HaltCommand;
import net.novauniverse.tournamentcore.spigot.command.invsee.InvseeCommand;
import net.novauniverse.tournamentcore.spigot.command.top.TopCommand;
import net.novauniverse.tournamentcore.spigot.gamesetup.GameSetupProcess;
import net.novauniverse.tournamentcore.spigot.leaderboard.TCLeaderboard;
import net.novauniverse.tournamentcore.spigot.listeners.GameEventListeners;
import net.novauniverse.tournamentcore.spigot.lobby.TCLobby;
import net.novauniverse.tournamentcore.spigot.lobby.duels.DuelsManager;
import net.novauniverse.tournamentcore.spigot.lobby.duels.command.AcceptDuelCommand;
import net.novauniverse.tournamentcore.spigot.lobby.duels.command.DuelCommand;
import net.novauniverse.tournamentcore.spigot.modules.EdibleHeads;
import net.novauniverse.tournamentcore.spigot.modules.GameListeners;
import net.novauniverse.tournamentcore.spigot.modules.GoldenHead;
import net.novauniverse.tournamentcore.spigot.modules.NoEnderPearlDamage;
import net.novauniverse.tournamentcore.spigot.modules.PlayerHeadDrop;
import net.novauniverse.tournamentcore.spigot.modules.PlayerKillCache;
import net.novauniverse.tournamentcore.spigot.modules.PlayerNameCache;
import net.novauniverse.tournamentcore.spigot.modules.TCPlayerListener;
import net.novauniverse.tournamentcore.spigot.modules.TCScoreboard;
import net.novauniverse.tournamentcore.spigot.modules.WinMessageListener;
import net.novauniverse.tournamentcore.spigot.modules.YBorder;
import net.novauniverse.tournamentcore.spigot.modules.gamespecific.SpleefManager;
import net.novauniverse.tournamentcore.spigot.modules.gamespecific.UHCManager;
import net.novauniverse.tournamentcore.spigot.modules.gamespecific.DeathSwapManager;
import net.novauniverse.tournamentcore.spigot.pluginmessagelistener.TCPluginMessageListnener;
import net.novauniverse.tournamentcore.spigot.score.ScoreListener;
import net.novauniverse.tournamentcore.spigot.score.ScoreManager;
import net.novauniverse.tournamentcore.team.TournamentCoreTeamManager;
import net.novauniverse.tournamentcore.tracker.TCCompassTracker;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;

public class TournamentCore extends JavaPlugin implements Listener {
	private static TournamentCore instance;
	private static String serverName;
	private boolean topEnabled;
	private ScoreListener scoreListener;
	private TournamentCoreTeamManager teamManager;
	private String lobbyServer;
	private File sqlFixFile;
	private String tournamentName;

	// Initialize variables
	@Override
	public void onLoad() {
		TournamentCore.instance = this;
		this.topEnabled = false;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();

		// Initialize file variables
		sqlFixFile = new File(this.getDataFolder().getPath() + File.separator + "sql_fix.sql");

		File gameLobbyFolder = new File(this.getDataFolder().getPath() + File.separator + "GameLobby");
		File worldFolder = new File(this.getDataFolder().getPath() + File.separator + "Worlds");

		// Server name and lobby name
		TournamentCore.serverName = getConfig().getString("server_name");
		this.lobbyServer = getConfig().getString("lobby_server");

		// Try to create the files and folders and load the worlds
		try {
			FileUtils.forceMkdir(gameLobbyFolder);
			FileUtils.forceMkdir(worldFolder);

			FileUtils.touch(sqlFixFile);

			if (NovaCore.isNovaGameEngineEnabled()) {
				Log.info("TournamentCore", "Reading lobby files from " + gameLobbyFolder.getPath());
				GameSetupProcess.readMapFiles(gameLobbyFolder, worldFolder);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.fatal("TournamentCore", "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Language files
		Log.info("TournamentCore", "Loading language files...");
		try {
			LanguageReader.readFromJar(this.getClass(), "/lang/en-us.json");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Setup win score
		int[] winScore;
		String winScoreString = "Win score: ";

		@SuppressWarnings("unchecked")
		List<Integer> winScoreList = (List<Integer>) getConfig().getList("win_score");

		winScore = new int[winScoreList.size()];
		for (int i = 0; i < winScoreList.size(); i++) {
			winScore[i] = winScoreList.get(i);
			winScoreString += winScore[i] + (i < (winScore.length - 1) ? ", " : " ");
		}

		Log.info("TournamentCore", winScoreString);

		// Require NetherBoardScoreboard
		if (ModuleManager.isDisabled(NetherBoardScoreboard.class)) {
			ModuleManager.enable(NetherBoardScoreboard.class);
		}

		// Connect to the database
		DBCredentials dbCredentials = new DBCredentials(getConfig().getString("mysql.driver"), getConfig().getString("mysql.host"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"), getConfig().getString("mysql.database"));

		try {
			DBConnection connection = new DBConnection();
			connection.connect(dbCredentials);

			TournamentCoreCommons.setDBConnection(connection);

			connection.startKeepAliveTask();
		} catch (ClassNotFoundException | SQLException e) {
			Log.fatal("TournamentCore", "Failed to connect to the database");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Get tournament name
		tournamentName = TournamentCoreCommons.getTournamentName();

		// Setup teams
		teamManager = new TournamentCoreTeamManager();

		NovaCore.getInstance().setTeamManager(teamManager);

		if (NovaCore.isNovaGameEngineEnabled()) {
			GameSetupProcess.init();
		}

		// Scoreboard
		ModuleManager.require(NetherBoardScoreboard.class);

		NetherBoardScoreboard.getInstance().setDefaultTitle(ChatColor.translateAlternateColorCodes('§', tournamentName));
		NetherBoardScoreboard.getInstance().setLineCount(15);

		// Register modules and enable them
		ModuleManager.loadModule(PlayerNameCache.class, true);
		ModuleManager.loadModule(PlayerKillCache.class, true);
		ModuleManager.loadModule(NoEnderPearlDamage.class, true);
		
		ModuleManager.loadModule(TCLeaderboard.class, true);
		ModuleManager.loadModule(ScoreManager.class, true);
		ModuleManager.loadModule(GoldenHead.class, true);
		ModuleManager.loadModule(TCScoreboard.class, true);
		ModuleManager.loadModule(TCPlayerListener.class, true);

		// Register modules
		ModuleManager.loadModule(PlayerHeadDrop.class);
		ModuleManager.loadModule(EdibleHeads.class);
		ModuleManager.loadModule(YBorder.class);
		ModuleManager.loadModule(TCLobby.class);

		// Commands
		CommandRegistry.registerCommand(new DatabaseCommand());
		CommandRegistry.registerCommand(new FlyCommand());
		CommandRegistry.registerCommand(new HaltCommand());
		CommandRegistry.registerCommand(new InvseeCommand());
		CommandRegistry.registerCommand(new TopCommand());
		
		if (NovaCore.isNovaGameEngineEnabled()) {
			Bukkit.getServer().getPluginManager().registerEvents(new GameEventListeners(), this);
			
			ModuleManager.loadModule(UHCManager.class);
			ModuleManager.loadModule(DeathSwapManager.class);
			ModuleManager.loadModule(SpleefManager.class);
			ModuleManager.loadModule(WinMessageListener.class, true);
			ModuleManager.loadModule(GameListeners.class, true);
		}

		// Configuration values for heads
		if (getConfig().getBoolean("enable_head_drops")) {
			ModuleManager.enable(PlayerHeadDrop.class);
		}

		if (getConfig().getBoolean("enable_edible_heads")) {
			ModuleManager.enable(EdibleHeads.class);
		}

		// Register events
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(teamManager, this);

		// Check if lobby is enabled
		if (getConfig().getBoolean("lobby_enabled")) {
			ModuleManager.enable(TCLobby.class);

			Location lobbyLocation = new Location(Bukkit.getServer().getWorlds().get(0), getConfig().getDouble("spawn_x"), getConfig().getDouble("spawn_y"), getConfig().getDouble("spawn_z"), (float) getConfig().getDouble("spawn_yaw"), (float) getConfig().getDouble("spawn_pitch"));
			TCLobby.getInstance().setLobbyLocation(lobbyLocation);

			TCLobby.getInstance().setKOTLLocation(getConfig().getDouble("kotl_x"), getConfig().getDouble("kotl_z"), getConfig().getDouble("kotl_radius"));

			ConfigurationSection playerLeaderboard = getConfig().getConfigurationSection("lobby_player_leaderboard");
			ConfigurationSection teamLeaderboard = getConfig().getConfigurationSection("lobby_team_leaderboard");

			TCLeaderboard.getInstance().setLines(8);

			TCLeaderboard.getInstance().setPlayerHologramLocation(new Location(TCLobby.getInstance().getWorld(), playerLeaderboard.getDouble("x"), playerLeaderboard.getDouble("y"), playerLeaderboard.getDouble("z")));
			TCLeaderboard.getInstance().setTeamHologramLocation(new Location(TCLobby.getInstance().getWorld(), teamLeaderboard.getDouble("x"), teamLeaderboard.getDouble("y"), teamLeaderboard.getDouble("z")));

			ModuleManager.require(GUIManager.class);

			ModuleManager.loadModule(DuelsManager.class, true);
			CommandRegistry.registerCommand(new AcceptDuelCommand());
			CommandRegistry.registerCommand(new DuelCommand());
		}

		if (NovaCore.isNovaGameEngineEnabled()) {
			// Combat tag message
			

			// Check if game is enabled
			if (getConfig().getBoolean("game_enabled")) {
				scoreListener = new ScoreListener(getConfig().getBoolean("kill_score_enabled"), getConfig().getInt("kill_score"), getConfig().getBoolean("win_score_enabled"), winScore, getConfig().getBoolean("participation_score_enabled"), getConfig().getInt("participation_score"));
				Bukkit.getServer().getPluginManager().registerEvents(scoreListener, this);

				CompassTracker.getInstance().setCompassTrackerTarget(new TCCompassTracker());
				CompassTracker.getInstance().setStrictMode(true);
			}
		}

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "TCData");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "TCData", new TCPluginMessageListnener());
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Log.info("TournamentCore", "NovaCore#isNovaGameEngineEnabled(): " + NovaCore.isNovaGameEngineEnabled());
			}
		}.runTask(this);
	}

	@Override
	public void onDisable() {
		// Cancel tasks and listeners
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);

		// Close database connection
		try {
			if (TournamentCoreCommons.getDBConnection() != null) {
				if (TournamentCoreCommons.getDBConnection().isConnected()) {
					TournamentCoreCommons.getDBConnection().close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Getters and setters
	public static TournamentCore getInstance() {
		return instance;
	}

	public File getSqlFixFile() {
		return sqlFixFile;
	}

	public static String getServerName() {
		return serverName;
	}

	public TournamentCoreTeamManager getTeamManager() {
		return teamManager;
	}

	public boolean isTopEnabled() {
		return topEnabled;
	}

	public void setTopEnabled(boolean topEnabled) {
		this.topEnabled = topEnabled;
	}

	public String getLobbyServer() {
		return lobbyServer;
	}
}