package com.etriacraft.etriawallet;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class EtriaWallet extends JavaPlugin {
	
	protected static Logger log;
	private Config config;
	
	Commands cmd;
	
	@Override
	public void onEnable() {
		// Logger
		this.log = this.getLogger();
		config = new Config(this);
		
		config.initialize();
		
		DBConnection.init();
		
		cmd = new Commands(this);
		
	}
	
	@Override
	public void onDisable() {
		DBConnection.disable();
	}
	
}