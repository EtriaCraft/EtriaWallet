// Credit goes to Darq from http://darkcraft.org for the SQL Code //

package com.etriacraft.etriawallet;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.etriacraft.etriawallet.SQL.MySQLConnection;

public final class DBConnection {
    
	public static MySQLConnection con;
	
	public static EtriaWallet plugin;
	
	public DBConnection(EtriaWallet instance) {
		plugin = instance;
	}
    
    public static void init() {
    	EtriaWallet.log.info("[EtriaWallet] Etablishing Database Connection...");
    	
    	try {
    		con = new MySQLConnection(plugin.getConfig().getString("MySQL.host"), plugin.getConfig().getInt("MySQL.port"), plugin.getConfig().getString("MySQL.database"), plugin.getConfig().getString("MySQL.username"), plugin.getConfig().getString("MySQL.password"));
    	} catch (InstantiationException e) {
    		e.printStackTrace();
    	} catch (IllegalAccessException e) {
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
    	}
    	
    	if (con.connect(true)) {
    		EtriaWallet.log.info("[EtriaWallet] Connection Established!");
    	} else {
    		EtriaWallet.log.warning("[EtriaWallet] MySQL Connection Failed!");
    	}
    }
    
    public static void disable() {
    	con.disconnect();
    }
    
    public static ResultSet query (String query, boolean modifies) {
    	try {
    		return con.executeQuery(query, modifies);
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
}