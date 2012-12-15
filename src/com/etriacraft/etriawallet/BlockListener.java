package com.etriacraft.etriawallet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {

	public static EtriaWallet plugin;

	public BlockListener(EtriaWallet instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.isCancelled()) return;
		if (e.getPlayer() == null) return;
		Player p = e.getPlayer();
		String line1 = e.getLine(0);
		
		// Handles Permissions
		if (line1.equalsIgnoreCase("[Premium]")) {
			if (!p.hasPermission("etriawallet.sign.create")) {
				e.setCancelled(true);
				p.sendMessage("§cYou can't create EtriaWallet signs!");
			}
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();
		BlockState state = block.getState();
		if (state instanceof Sign) {
			Sign sign = (Sign) state;
			String signline1 = sign.getLine(0);
			if (signline1.equalsIgnoreCase("[Premium]")
					&& e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
					&& block.getType() == Material.WALL_SIGN
					&& player.hasPermission("etriawallet.sign.use")) {
				String purchasedPack = sign.getLine(1);
				Double price = plugin.getConfig().getDouble("packages." + purchasedPack + ".price");
				String username = player.getName();
				ResultSet playerBalance1 = DBConnection.query("SELECT balance FROM wallet_players WHERE player = '" + username + "';", false);
				try {
					while (playerBalance1.next()) {
						Double newbalance = playerBalance1.getDouble("balance");

						if (!(newbalance >= price)) {
							player.sendMessage("§cYou don't have enough for this package.");
						} else if (newbalance >= price) {
							DBConnection.query("UPDATE wallet_players SET balance = balance = " + price + " WHERE player = '" + username + "';", true);
							List<String> commands = plugin.getConfig().getStringList("packages." + purchasedPack + ".commands");
							for (String cmd : commands) {
								plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd.replace("%player", username));
							}
							player.sendMessage("§aWe are adding §3" + purchasedPack + "§a to your account.");
							player.sendMessage("§aSubtracted §3" + price + "0§a from your account.");
							player.sendMessage("§aPurchase successfully executed!");
						}
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
					player.sendMessage("Something went wrong with your transaction. Contact an Admin");
				}
			}
			else if (block.getType() == Material.SIGN_POST) {
				return;
			}
		}
	}
}
