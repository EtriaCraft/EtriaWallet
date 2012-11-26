package com.etriacraft.etriawallet;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	
	private EtriaWallet plugin;
	
	public Config(EtriaWallet instance) {
		plugin = instance;
	}
	
	public void initialize() {
		YamlConfiguration config = (YamlConfiguration)plugin.getConfig();
		
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			config.options().copyDefaults(true);
			plugin.saveConfig();
		}
		
		DBConnection.host = config.getString("MySQL.host", "localhost");
		DBConnection.db = config.getString("MySQL.database", "minecraft");
		DBConnection.user = config.getString("MySQL.username", "root");
		DBConnection.pass = config.getString("MySQL.password", "");
		DBConnection.port = config.getInt("MySQL.port", 3306);
	}

}
