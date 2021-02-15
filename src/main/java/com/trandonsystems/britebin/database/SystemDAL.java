package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.trandonsystems.britebin.model.KeyValue;

import org.apache.log4j.Logger;


public class SystemDAL {

	static Logger log = Logger.getLogger(SystemDAL.class);

	public static String getSysConfigValue(String name) throws SQLException {
		
		log.info("SystemDAL.getSysConfigValue(" + name + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String configValue = "";

		String spCall = "{ call getSysConfigValue(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, name);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				configValue = rs.getString("value");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return configValue;
	}

	public static List<KeyValue> getSysConfigValues() throws SQLException {
		// I have this here for completeness - but it will be faster and easier if we do each of the calls as required from the
		// page to get each of the values required
		log.info("SystemDAL.getSysConfigValues()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<KeyValue> configValues = new ArrayList<KeyValue>();

		String spCall = "{ call getSysConfigValues() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				KeyValue kv = new KeyValue();
				kv.key = rs.getString("name");
				kv.value = rs.getString("value");

				configValues.add(kv);
			}
			
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return configValues;
	}


	public static void saveSysConfigValue(KeyValue kv, int actionUserId) throws SQLException {

		log.info("SystemDAL.saveSystemConfigValue(kv, userId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveSysConfig(?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, kv.key);
			spStmt.setString(2, kv.value);
			spStmt.setInt(3, actionUserId);
			
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("SystemDAL.saveSystemConfigValue(kv, userId) - ERROR: " + ex.getMessage());
			throw ex;
		}
		
		return;
	}
	
	
}
