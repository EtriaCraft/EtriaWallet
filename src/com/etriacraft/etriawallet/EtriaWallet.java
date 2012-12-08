package com.etriacraft.etriawallet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EtriaWallet extends JavaPlugin {
	
	protected static Logger log;
	
	File configFile;
	FileConfiguration config;
	
	Commands cmd;
	
	@Override
	public void onEnable() {
		
		// Logger
		this.log = this.getLogger();
		
		//Initialize Config
		configFile = new File(getDataFolder(), "config.yml");
		
		// Use firstRun() method
		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Declare FileConfigurations, load.
		config = new YamlConfiguration();
		loadYamls();
		
		DBConnection.host = config.getString("MySQL.host", "localhost");
		DBConnection.db = config.getString("MySQL.database", "minecraft");
		DBConnection.user = config.getString("MySQL.username", "root");
		DBConnection.pass = config.getString("MySQL.password", "");
		DBConnection.port = config.getInt("MySQL.port", 3306);
		
		DBConnection.init();
		
		cmd = new Commands(this);
		
	}

	@Override
	public void onDisable() {
		DBConnection.disable();
	}
	
	// Methods
	private void firstRun() throws Exception {
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
	}
	
	private void loadYamls() {
		try {
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf))>0) {
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveYamls() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public String getExpiresDate(String packagename) {
		Integer days = getConfig().getInt("packages." + packagename + ".expires");
		if (!(days == 0)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			try {
				c.setTime(sdf.parse(getCurrentDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			c.add(Calendar.DATE, days);
			String exp = sdf.format(c.getTime());
			return exp;
		}
		return null;
	}
	
}