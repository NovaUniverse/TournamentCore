package net.novauniverse.tournamentcore.spigot.lobby.duels.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.novauniverse.tournamentcore.spigot.lobby.duels.DuelsManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class DuelCommand extends NovaCommand {
	public DuelCommand() {
		super("duel", TournamentCore.getInstance());
		this.addHelpSubCommand();
		this.setPermission("tournamentcore.command.duel");
		this.setPermissionDefaultValue(PermissionDefault.TRUE);
		this.setDescription("Send a duel request to a player");
		this.setAllowedSenders(AllowedSenders.PLAYERS);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Missing argument: Player");
		} else {
			Player targetPlayer = Bukkit.getPlayer(args[0]);

			if (targetPlayer != null) {
				if (targetPlayer.isOnline()) {
					if(targetPlayer.getUniqueId() != player.getUniqueId()) {
					if (DuelsManager.getInstance().createInvite(player, targetPlayer)) {
						player.sendMessage(ChatColor.GREEN + "Send a duel request to " + targetPlayer.getName());
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "Failed to invite player. Please try again later");
					}
					} else {
						player.sendMessage(ChatColor.RED + "You cant invite yourself to a duel");
					}
				} else {
					player.sendMessage(ChatColor.RED + "That player in not online");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Could not find a player with that name");
			}
		}

		return false;
	}
}