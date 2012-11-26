package com.etriacraft.etriawallet;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class Commands {
	
	EtriaWallet plugin;
	
	public Commands(EtriaWallet instance) {
		this.plugin = instance;
		init();
	}
	
	private void init() {
		PluginCommand wallet = plugin.getCommand("wallet");
		CommandExecutor exe;
		
		exe = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
				if (args.length < 1) {
					s.sendMessage("§eEtriaWallet Commands");
					s.sendMessage("§3/wallet create§f - Creates a wallet for your account.");
					s.sendMessage("§3/wallet balance§f - Shows your balance.");
					return true;
				}
				else if (args[0].equalsIgnoreCase("balance") && s.hasPermission("etriawallet.balance")) {
					ResultSet rs2 = DBConnection.query("SELECT balance FROM wallet_players WHERE player = '" + s.getName() + "';", false);
					try {
						while (rs2.next()) {
							s.sendMessage("§eYou currently have a balance of §a$" + rs2.getDouble("balance") + ".");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					} else if (args[0].equalsIgnoreCase("create") && s.hasPermission("etriawallet.create")) {
						DBConnection.query("INSERT INTO wallet_players(player, balance) VALUES ('" + s.getName() + "', 0.00)", true);
					s.sendMessage("§eWallet Created!");
				} else { 
					s.sendMessage("§cYou don't have permission to do that!");
				} return true;
			}
		}; wallet.setExecutor(exe);
	}

}
