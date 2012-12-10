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
				// This will only send the player the commands they have permission to use.
				if (args.length < 1) {
					s.sendMessage("§eEtriaWallet Commands");
					if (s.hasPermission ("etriawallet.create")) {
						s.sendMessage("§3/wallet create <player>§f - Creates a wallet for your account.");
					} if (s.hasPermission("etriawallet.balance")) {
						s.sendMessage("§3/wallet balance§f - Shows your balance.");
					} if (s.hasPermission("etriawallet.give")) {
						s.sendMessage("§3/wallet give <player> <amount>§f - Gives a player money.");
					} if (s.hasPermission("etriawallet.take")) {
						s.sendMessage("§3/wallet take <player> <amount>§f - Takes money from a player.");
					} if (s.hasPermission("etriawallet.reload")); {
						s.sendMessage("§3/wallet reload§f - Reloads Configs / Packages.");
					} if (s.hasPermission("etriawallet.packs")); {
						s.sendMessage("§3/packs§f - Lists the pack commands.");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("reload") && s.hasPermission("etriawallet.reload")) {
					// If the command is "/wallet reload" then it will reload the Configuration File.
					plugin.reloadConfig();
					s.sendMessage("§aConfig / Packages reloaded.");
				}
				else if (args[0].equalsIgnoreCase("balance") && s.hasPermission("etriawallet.balance")) {
					if (args.length == 1) {
						ResultSet rs2 = DBConnection.query("SELECT balance FROM wallet_players WHERE player = '" + s.getName() + "';", false);
						try {
							// If a balance can be returned, show it to the player.
							if (rs2.next()) {
								do {
									s.sendMessage("§eYou currently have a balance of §a$" + rs2.getDouble("balance") + "0§e credits.");
								} while(rs2.next());
							} else if (!rs2.next()) {
								// If we can't find the player's balance, tell them to create one.
								s.sendMessage("§cWas not able to find your wallet information!");
								s.sendMessage("§cTry creating one using the command: §3/wallet create");
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else if (args.length == 2 && s.hasPermission("etriawallet.balance.others")) {
						ResultSet rs2 = DBConnection.query("SELECT balance FROM wallet_players WHERE player = '" + args[1] + "';", false);
						try {
							if (rs2.next()) {
								do {
									// If the player's balance can be returned, display it.
									s.sendMessage("§e" + args[1] + " has §a" + rs2.getDouble("balance") + "0§e credits.");
								} while (rs2.next());
								// If no balance can be found, tell the player.
							} else if (!rs2.next()) {
								s.sendMessage("§cWas not able to find wallet information for §3" + args[1]);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				} else if (args[0].equalsIgnoreCase("create") && s.hasPermission("etriawallet.create")) {
					if (args.length == 1) {	
						ResultSet rs2 = DBConnection.query("SELECT player FROM wallet_players WHERE player = '" + s.getName() + "';", false);
						try {
							if (rs2.next()) {
								do {
									// If the query returns something, the player already has a wallet, don't attempt creating a new one!
									s.sendMessage("§cIt appears you already have a wallet, we can't let you create another one!");
								} while (rs2.next());
								// If the balance can't be found, try creating the wallet.
							}else if (!rs2.next()) {
								DBConnection.query("INSERT INTO wallet_players(player, balance) VALUES ('" + s.getName() + "', 0.00)", true);
								s.sendMessage("§aYour wallet has been created!");
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						//						DBConnection.query("INSERT INTO wallet_players(player, balance) VALUES ('" + s.getName() + "', 0.00)", true);
						//						s.sendMessage("§aYour wallet has been created!");
					} if (args.length == 2 && s.hasPermission("etriawallet.create.others")) {
						ResultSet rs2 = DBConnection.query("SELECT player FROM wallet_players WHERE player = '" + args[1] + "';", false);
						try {
							if (rs2.next()) {
								// If the player already has a wallet, skip the creation process.
								do {
									s.sendMessage("§3" + args[1] + " §calready has a wallet. Creating a new one would be silly.");
								} while (rs2.next());
							} else if (!rs2.next()) {
								DBConnection.query("INSERT INTO wallet_players(player, balance) VALUES ('" + args[1] + "', 0.00)", true);
								s.sendMessage("§eCreated a new wallet for §3" + args[1] + "§e.");
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					s.sendMessage("§eWallet created for " + args[1] + ".");

				} else if (args[0].equalsIgnoreCase("give") && args.length == 3 && s.hasPermission("etriawallet.give")) {
					ResultSet rs2 = DBConnection.query("UPDATE wallet_players SET balance = balance + " + args[2] + " WHERE player = '" + args[1] + "';", true);
					s.sendMessage("§eYou have given " + args[1] + " §a$" + args[2] + "§e.");
				} else if (args[0].equalsIgnoreCase("take") && args.length == 3 && s.hasPermission("etriawallet.take")) {
					DBConnection.query("UPDATE wallet_players SET balance = balance - " + args[2] + " WHERE player = '" + args[1] + "';", true);
					s.sendMessage("§eYou have taken §a$ " + args[2] + "§e from §a" + args[1] + "§e.");
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
					if (s.hasPermission("etriawallet.packs.info")) {
						s.sendMessage("§3/packs info <name>§f - Returns information on a pack.");
					}
					if (s.hasPermission("etriawallet.packs.buy")) {
						s.sendMessage("§3/packs buy <name>§f - Buys a package.");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("info") && s.hasPermission("etriawallet.packs.info")) {
					if (args.length == 1) {
						s.sendMessage("§cNot enough arguments, please provide a package name!");
					} else {
						String pack = args[1];
						Double price = plugin.getConfig().getDouble("packages." + pack + ".price");
						if (price == null) {
							s.sendMessage("§cWas not able to pull the required information for this package.");
							s.sendMessage("§cPerhaps it doesn't exist?");
						} else {
							String description = plugin.getConfig().getString("packages." + pack + ".description");
							s.sendMessage("-----§e" + pack + " Info§f----- ");
							s.sendMessage("§aPackage Name:§3 " + pack);
							s.sendMessage("§aPrice:§3 " + price + "0");
							s.sendMessage("§aDescription:§3 " + description);
							return true;
						}
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
									s.sendMessage("§aSubtracted §3" + price + "0§a from your account.");
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
