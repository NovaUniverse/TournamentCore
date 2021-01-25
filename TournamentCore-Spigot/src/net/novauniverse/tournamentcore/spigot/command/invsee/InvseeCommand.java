package net.novauniverse.tournamentcore.spigot.command.invsee;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.tournamentcore.spigot.TournamentCore;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class InvseeCommand extends NovaCommand {
	public InvseeCommand() {
		super("invsee", TournamentCore.getInstance());
		setDescription("Access the inventory of a player");
		setPermission("tournamentcore.command.invsee");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);
		
		addHelpSubCommand();
		
		this.setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if (args.length == 1) {
			Player target = Bukkit.getServer().getPlayer(args[0]);

			if (target != null) {
				if (target.isOnline()) {
					player.openInventory(target.getInventory());
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "That player is not online");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Could not find player named " + args[0]);
			}
		} else {
			player.sendMessage(ChatColor.RED + "Use /invsee <Player>");
		}
		return false;
	}
}