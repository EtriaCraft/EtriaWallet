package com.etriacraft.etriawallet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class Commands {

	EtriaWallet plugin;

	public Commands(EtriaWallet instance) {
		this.plugin = instance;
		init();
	}

	private void init() {
		PluginCommand wallet = plugin.getCommand("wallet");
		PluginCommand packs = plugin.getCommand("packs");
		CommandExecutor exe;

		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (args.length < 1) {
					s.sendMessage("§eEtriaWallet Commands");
					s.sendMessage("§3/wallet create <player>§f - Creates a wallet for your account.");
					s.sendMessage("§3/wallet balance§f - Shows your balance.");
					s.sendMessage("§3/wallet give <player> <amount>§f - Gives a player money.");
					return true;
				}
				else if (args[0].equalsIgnoreCase("balance") && s.hasPermission("etriawallet.balance")) {
					if (args.length == 1) {
						ResultSet rs2 = DBConnection.query("SELECT balance FROM wallet_players WHERE player = '" + s.getName() + "';", false);
						try {
							while (rs2.next()) {
								s.sendMessage("§eYou currently have a balance of §a$" + rs2.getDouble("balance") + ".");
							}
						} catch (SQLException e) {
							s.sendMessage("§cWas unable to check the balance. Perhaps you don't have a wallet yet.");
							s.sendMessage("§eUse §3/wallet create §eto create a wallet for yourself.");
							e.printStackTrace();
						}
					} else if (args.length == 2 && s.hasPermission("etriawallet.balance.others")) {
						ResultSet rs2 = DBConnection.query("SELECT balance FROM wallet_players WHERE player = '" + args[1] + "';", false);
						try {
							while (rs2.next()) {
								s.sendMessage("§e" + args[1] + " has a balance of §a" + rs2.getDouble("balance") + "§e.");
							}
						} catch (SQLException e) {
							s.sendMessage("§cWas unable to find the balance for §e" + args[1] + " §cperhaps that player doesn't have a wallet?");
							e.printStackTrace();
						}
					}
				} else if (args[0].equalsIgnoreCase("create") && s.hasPermission("etriawallet.create")) {
					if (args.length == 1) {
						DBConnection.query("INSERT INTO wallet_players(player, balance) VALUES ('" + s.getName() + "', 0.00)", true);
						s.sendMessage("§eWallet Created!");
					} else if (args.length == 2 && s.hasPermission("etriawallet.create.others")) {
						DBConnection.query("INSERT INTO wallet_players(player, balance) VALUES ('" + args[1] + "', 0.00)", true);
						s.sendMessage("§eWallet created for " + args[1] + ".");
					}
				} else if (args[0].equalsIgnoreCase("give") && args.length == 3 && s.hasPermission("etriawallet.give")) {
					DBConnection.query("UPDATE wallet_players SET balance = balance + " + args[2] + " WHERE player = '" + args[1] + "';", true);
					s.sendMessage("§eYou have given " + args[1] + " §a$" + args[2] + "§e.");
				} else if (args[0].equalsIgnoreCase("take") && args.length == 3 && s.hasPermission("etriawallet.take")) {
					DBConnection.query("UPDATE wallet_players SET balance = balance - " + args[2] + " WHERE player = '" + args[1] + "';", true);
					s.sendMessage("§eYou have taken " + args[1] + "§a$" + args[2] + "§e.");
				} else { 
					s.sendMessage("§cYou don't have permission to do that!");
				} return true;
			}
		}; wallet.setExecutor(exe);
		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (args.length < 1) {
					s.sendMessage("§eApplicable Pack Commands");
					s.sendMessage("§3/packs info <name>§f - Returns information on a pack.");
					return true;
				} else if (args[0].equalsIgnoreCase("info") && s.hasPermission("etriawallet.packs.info")) {
					String pack = args[1];
					Double price = plugin.getConfig().getDouble("packages." + pack + ".price");
					if (price == null) {
						s.sendMessage("§cWas not able to pull the required information for this package.");
						s.sendMessage("§cPerhaps it doesn't exist?");
					} else {
						String description = plugin.getConfig().getString("packages." + pack + ".description");
						s.sendMessage("-----§e" + pack + " Info§f----- ");
						s.sendMessage("§aPackage Name:§3 " + pack);
						s.sendMessage("§aPrice:§3 " + price);
						s.sendMessage("§aDescription:§3 " + description);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("buy") && s.hasPermission("etriawallet.packs.buy")) {
					String purchasedPack = args[1];
					Double price = plugin.getConfig().getDouble("packages." + purchasedPack + ".price");
					String player = s.getName();
					ResultSet playerBalance1 = DBConnection.query("SELECT balance FROM wallet_players WHERE player = '" + s.getName() + "';", false);
					if (price == null) {
						s.sendMessage("§cThere is an error with that package.");
						s.sendMessage("§cIt either doesn't exist or the price is not valid.");
					} else {
						try {

							while (playerBalance1.next()) {

								Double newbalance = playerBalance1.getDouble("balance");

								if (!(newbalance >= price)) {
									s.sendMessage("§cYou don't have enough for this package");
								} else if (newbalance >= price) {
									DBConnection.query("UPDATE wallet_players SET balance = balance - " + price + " WHERE player = '" + player + "';", true);
									List<String> commands = plugin.getConfig().getStringList("packages." + purchasedPack + ".commands");
									for (String cmd : commands) {
										plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd.replace("%player", player));
									}
									s.sendMessage("§aWe are adding §3" + purchasedPack + "§a to your account.");
									s.sendMessage("§aSubtacted §3" + price + "§a from your account.");
									s.sendMessage("§aPurchase successfully executed!");
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();

						} 
					}

				} else {
					s.sendMessage("Something went wrong");
				} return true;
			}
		}; packs.setExecutor(exe);
	} 

}
