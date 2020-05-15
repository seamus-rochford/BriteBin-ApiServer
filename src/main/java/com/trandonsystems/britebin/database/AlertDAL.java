package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.BinLevel;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.ContentType;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.model.User;

public class AlertDAL {
	
	static Logger log = Logger.getLogger(AlertDAL.class);

	public static Alert setAlertValues(int customerId, ResultSet rs) throws SQLException {
		Alert alert = new Alert();
		
		alert.id = rs.getInt("alert_defn.id");
		
		alert.customerId = customerId;

		User user = new User();
		user.id = rs.getInt("users.id");
		user.name = rs.getString("users.name");
		user.email = rs.getString("users.email");
		alert.user = user;
		
		alert.binFull = (rs.getInt("alert_defn.binFull") == 1);

		alert.batteryUVLO = (rs.getInt("alert_defn.batteryUVLO") == 1);
		alert.binEmptiedLastPeriod = (rs.getInt("alert_defn.binEmptiedLastPeriod") == 1);
		alert.batteryOverTempLO = (rs.getInt("alert_defn.batteryOverTempLO") == 1);
		alert.binLocked = (rs.getInt("alert_defn.binLocked") == 1);

		alert.binTilted = (rs.getInt("alert_defn.binTilted") == 1);
		alert.serviceDoorOpen = (rs.getInt("alert_defn.serviceDoorOpen") == 1);
		alert.flapStuckOpen = (rs.getInt("alert_defn.flapStuckOpen") == 1);

		alert.damage = (rs.getInt("alert_defn.damage") == 1);

		alert.email = (rs.getInt("alert_defn.email") == 1);
		alert.sms = (rs.getInt("alert_defn.sms") == 1);
		alert.whatsApp = (rs.getInt("alert_defn.whatsapp") == 1);
		alert.pushNotification = (rs.getInt("alert_defn.push") == 1);
		
		// Convert database timestamp(UTC date) to local time instant
//		Timestamp insertDate = rs.getTimestamp("insertDate");
//		if (insertDate == null) {
//			alert.insertDate = null;
//		}
//		else {
//			java.time.Instant insertDateInstant = insertDate.toInstant();
//			alert.insertDate = insertDateInstant;
//		}
//		alert.insertBy = rs.getInt("insertBy");
//		
//		// Convert database timestamp(UTC date) to local time instant
//		Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
//		if (modifiedDate == null) {
//			alert.modifiedDate = null;
//		}
//		else {
//			java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
//			alert.modifiedDate = modifiedDateInstant;
//		}
//		alert.modifiedBy = rs.getInt("modifiedBy");

		
		return alert;
	}
	
	public static List<Alert> getAlertsAdmin(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsAdmin(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsAdmin(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}
		
	public static List<Alert> getAlertsDistributors(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsDistributors(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsDistributors(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}
		
	public static List<Alert> getAlertsAdminTechnicians(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsAdminTechnicians(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsAdminTechnicians(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}
		
	public static List<Alert> getAlertsDistributorTechnicians(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsDistributorTechnicians(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsDistributorTechnicians(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}
		
	public static List<Alert> getAlertsCorporate(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsCorporate(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call getAlertsCorporate(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}
		
	public static List<Alert> getAlertsCustomer(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsCustomer(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call getAlertsCustomer(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}
		
	public static List<Alert> getAlertsCorporateDrivers(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsCorporateDrivers(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call getAlertsCorporateDrivers(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}
		
	public static List<Alert> getAlertsCustomerDrivers(int customerId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("AlertDAL.getAlertsCustomerDrivers(customerId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call getAlertsCustomerDrivers(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, customerId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(customerId, rs);

				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alerts;
	}

	public static List<Alert> saveAlerts (List<Alert> alerts, int actionUserId) throws SQLException {
		log.info("AlertDAL.saveAlerts(alerts, inActionId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveAlert(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			for (int i = 0; i < alerts.size(); i++) {
				Alert alert = alerts.get(i);
				
				spStmt.setLong(1, alert.id);
				spStmt.setInt(2, alert.customerId);
				spStmt.setInt(3, alert.user.id);
				spStmt.setInt(4, alert.binFull ? 1 : 0);
				spStmt.setInt(5, alert.batteryUVLO ? 1 : 0);
				spStmt.setInt(6, alert.binEmptiedLastPeriod ? 1 : 0);
				spStmt.setInt(7, alert.batteryOverTempLO ? 1 : 0);
				spStmt.setInt(8, alert.binLocked ? 1 : 0);
				spStmt.setInt(9, alert.binTilted ? 1 : 0);
				spStmt.setInt(10, alert.serviceDoorOpen ? 1 : 0);
				spStmt.setInt(11, alert.flapStuckOpen ? 1 : 0);
				spStmt.setInt(12, alert.damage ? 1 : 0);
				spStmt.setInt(13, alert.email ? 1 : 0);
				spStmt.setInt(14, alert.sms ? 1 : 0);
				spStmt.setInt(15, alert.whatsApp ? 1 : 0);
				spStmt.setInt(16, alert.pushNotification ? 1 : 0);
				spStmt.setInt(17, actionUserId);
				
				spStmt.registerOutParameter(1, Types.BIGINT);
				
				spStmt.executeUpdate();
				
				alert.id = spStmt.getInt(1);
				
				alerts.set(i, alert);
				log.debug("alertId: " + alert.id);
			}

			
		} catch (SQLException ex) {
			log.error("AlertDAL.alerts: " + ex.getMessage());
			throw ex;
		}
		
		log.info("AlertDAL.saveAlerts(alerts, inActionId) - end");

		return alerts;
		
	}
	
}
