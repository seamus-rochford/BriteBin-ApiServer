package com.trandonsystems.britebin.database;

import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.Damage;
import com.trandonsystems.britebin.model.DamageHistory;
import com.trandonsystems.britebin.model.DamageStatus;
import com.trandonsystems.britebin.model.DamageType;


public class DamageDAL {

	static Logger log = Logger.getLogger(DamageDAL.class);


	public DamageDAL() {
		log.trace("Constructor");
	}
	
	public static Damage getDamage(int damageId, int userFilterId) throws SQLException {

		log.info("DamageDAL.getDamage(id)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetDamage(?) }";
		log.info("SP Call: " + spCall);
		
		Damage damage = new Damage();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageId);
			ResultSet rs = spStmt.executeQuery();

			damage.id = damageId;
			if (rs.next()) {
				
				damage.damageType = new DamageType();
				damage.damageType.id = rs.getInt("ref_damage_type.id");
				damage.damageType.name = rs.getString("ref_damage_type.name");
				
				damage.damageStatus = new DamageStatus();
				damage.damageStatus.id = rs.getInt("ref_damage_status.id");
				damage.damageStatus.name = rs.getString("ref_damage_status.name");
				
				int unitId =  rs.getInt("damage.unitId");
				
				damage.unit = UnitDAL.getUnit(userFilterId, unitId);
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					damage.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					damage.insertDate = insertDateInstant;
				}
				
				// Get damage history
				damage.damageHistory = getDamageHistory(damageId, userFilterId);

			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return damage;
	}

	public static byte[] getDamageHistoryImage(int damageHistoryId) throws SQLException {

		log.info("DamageDAL.getDamageHistoryImage(id)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call getDamageHistoryImage(?) }";
		log.info("SP Call: " + spCall);
		
		byte[] image = new byte[0];
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageHistoryId);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				image = rs.getBytes("damage_history.image"); 
				
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return image;
	}

	public static List<DamageHistory> getDamageHistory(int damageId, int userFilterId) throws SQLException {
		
		log.info("UnitDAL.getUnits(userFilterId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<DamageHistory> damageHistoryList = new ArrayList<DamageHistory>();

		String spCall = "{ call GetDamageHistory(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				DamageHistory damageHistory = new DamageHistory();

				damageHistory.id = rs.getInt("id");
				damageHistory.damageId = damageId;
				damageHistory.damageStatus = new DamageStatus();
				damageHistory.damageStatus.id = rs.getInt("ref_damage_status.id");
				damageHistory.damageStatus.name = rs.getString("ref_damage_status.name");
				damageHistory.comment = rs.getString("damage_history.comment");
				damageHistory.actionUserId = rs.getInt("damage_history.actionUserId");
				damageHistory.image = rs.getBytes("damage_history.image"); 
				if (damageHistory.image != null) {
					damageHistory.base64 = new String(Base64.getEncoder().encode(damageHistory.image));
				} 

				// Convert database timestamp(UTC date) to local time instant
				Timestamp actionDate = rs.getTimestamp("actionDate");
				if (actionDate == null) {
					damageHistory.actionDate = null;
				}
				else {
					java.time.Instant actionDateInstant = actionDate.toInstant();
					damageHistory.actionDate = actionDateInstant;
				}

				damageHistory.assignedToUserId = rs.getInt("damage_history.assignedTo");

				damageHistoryList.add(damageHistory);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return damageHistoryList;
	}
		
	public static List<Damage> getDamages(int filterDamageStatusId, Instant filterFromDate, Instant filterToDate, int userFilterId) throws SQLException {
		// Return all units based on "parentId" hierarchy
		
		log.info("UnitDAL.getDamages(filterDamageStatusId, filterFromDate, filterToDate, userFilterId, locale)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Damage> damages = new ArrayList<Damage>();

		String spCall = "{ call GetDamages(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, filterDamageStatusId);
			
			// Convert java.time.Instant to java.sql.timestamp
			Timestamp tsFrom = Timestamp.from(filterFromDate);
		    spStmt.setTimestamp(3, tsFrom);

			// Convert java.time.Instant to java.sql.timestamp
			Timestamp tsTo = Timestamp.from(filterToDate);
		    spStmt.setTimestamp(4, tsTo);

		    ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Damage damage = new Damage();

				damage.id = rs.getInt("damage.id");
				
				damage.damageType = new DamageType();
				damage.damageType.id = rs.getInt("ref_damage_type.id");
				damage.damageType.name = rs.getString("ref_damage_type.name");
				
				damage.damageStatus = new DamageStatus();
				damage.damageStatus.id = rs.getInt("ref_damage_status.id");
				damage.damageStatus.name = rs.getString("ref_damage_status.name");
				
				int unitId =  rs.getInt("damage.unitId");
				
				damage.unit = UnitDAL.getUnit(userFilterId, unitId);
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					damage.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					damage.insertDate = insertDateInstant;
				}
				
				// Get damage history
				damage.damageHistory = getDamageHistory(damage.id, userFilterId);


				damages.add(damage);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return damages;
	}
		
	public static List<Damage> getAssignedDamages(int assignedUserId, Instant filterFromDate, Instant filterToDate, int userFilterId) throws SQLException {
		// Return all units based on "parentId" hierarchy
		
		log.info("UnitDAL.getDamages(filterDamageStatusId, filterFromDate, filterToDate, userFilterId, locale)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Damage> damages = new ArrayList<Damage>();

		String spCall = "{ call GetDamagesAssigned(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, assignedUserId);
			
			// Convert java.time.Instant to java.sql.timestamp
			Timestamp tsFrom = Timestamp.from(filterFromDate);
		    spStmt.setTimestamp(3, tsFrom);

			// Convert java.time.Instant to java.sql.timestamp
			Timestamp tsTo = Timestamp.from(filterToDate);
		    spStmt.setTimestamp(4, tsTo);

		    ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Damage damage = new Damage();

				damage.id = rs.getInt("damage.id");
				
				damage.damageType = new DamageType();
				damage.damageType.id = rs.getInt("ref_damage_type.id");
				damage.damageType.name = rs.getString("ref_damage_type.name");
				
				damage.damageStatus = new DamageStatus();
				damage.damageStatus.id = rs.getInt("ref_damage_status.id");
				damage.damageStatus.name = rs.getString("ref_damage_status.name");
				
				int unitId =  rs.getInt("damage.unitId");
				
				damage.unit = UnitDAL.getUnit(userFilterId, unitId);
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					damage.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					damage.insertDate = insertDateInstant;
				}
				
				// Get damage history
				damage.damageHistory = getDamageHistory(damage.id, userFilterId);


				damages.add(damage);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return damages;
	}
		
 	public static Damage report(int damageType, int unitId, String comment, int actionUserId) throws SQLException {

		log.info("DamageDAL.report(damageType, unitId, comment, actionUserId)");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call DamageReport(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		int damageId = 0;
		Damage damage = new Damage();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageType);
			spStmt.setInt(2, unitId);
			spStmt.setString(3, comment);
			spStmt.setInt(4, actionUserId);
			ResultSet rs = spStmt.executeQuery();
			
			if (rs.next()) {
				damageId = rs.getInt("id");
				
				damage = getDamage(damageId, actionUserId);
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return damage;
	}

 	public static Damage report(int damageType, int unitId, String comment, int actionUserId, String fileName) throws Exception {

		log.info("DamageDAL.report(damageType, unitId, comment, actionUserId)");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		File imageFile = new File(fileName);
		FileInputStream image = new FileInputStream(imageFile);
		
		String spCall = "{ call DamageReportWithImage(?, ?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		int damageId = 0;
		Damage damage = new Damage();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageType);
			spStmt.setInt(2, unitId);
			spStmt.setString(3, comment);
			spStmt.setInt(4, actionUserId);
			spStmt.setBinaryStream(5, image);
			ResultSet rs = spStmt.executeQuery();
			
			if (rs.next()) {
				damageId = rs.getInt("id");
				
				damage = getDamage(damageId, actionUserId);
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return damage;
	}

 	public static Damage assign(int damageId, int assignedUserId, String comment, int actionUserId) throws SQLException {

		log.info("DamageDAL.assign(damageId, assignedUserId, comment, actionUserId, locale)");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call DamageAssign(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		Damage damage;
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageId);
			spStmt.setInt(2, assignedUserId);
			spStmt.setString(3, comment);
			spStmt.setInt(4, actionUserId);
			spStmt.executeUpdate();
			
			damage = getDamage(damageId, actionUserId);

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return damage;
	}	

 	public static Damage close(int damageId, String comment, int actionUserId) throws SQLException {

		log.info("DamageDAL.close(damageId, comment, actionUserId, locale)");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call DamageClose(?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		Damage damage;
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageId);
			spStmt.setString(2, comment);
			spStmt.setInt(3, actionUserId);
			spStmt.executeUpdate();
			
			damage = getDamage(damageId, actionUserId);

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return damage;
	}	 	
}
