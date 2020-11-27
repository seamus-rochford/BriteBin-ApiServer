package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.model.GuestUnit;


public class GuestDAL {

	static Logger log = Logger.getLogger(GuestDAL.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();


	public GuestDAL() {
		log.trace("Constructor");
	}

	
	private static GuestUnit setGuestUnitValues(ResultSet rs) throws SQLException {
		GuestUnit guestUnit = new GuestUnit();
		
		guestUnit.guestId = rs.getInt("guestId");
		guestUnit.guestUnitId = rs.getInt("guestUnitId");
		guestUnit.dupUnitId = rs.getInt("dupUnitId");
		
		return guestUnit;
	}
	
	
	public static GuestUnit getGuestUnit(int guestUnitId) throws SQLException {

		log.info("GuestDAL.getGuestUnit(guestUnitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetGuestUnit(?) }";
		log.info("SP Call: " + spCall);
		
		GuestUnit guestUnit = new GuestUnit();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, guestUnitId);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				guestUnit = setGuestUnitValues(rs);
			} else {
				throw new SQLException("No guest unit exists with id = " + guestUnitId);
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return guestUnit;
	}

	
	public static int deleteGuestUnit(int guestUnitId) throws SQLException {

		log.info("GuestDAL.deleteGuestUnit(guestUnitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		int noRecordsDeleted = 0;
		
		String spCall = "{ call DeleteGuestUnit(?) }";
		log.info("SP Call: " + spCall);
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, guestUnitId);
			noRecordsDeleted = spStmt.executeUpdate();


		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return noRecordsDeleted;
	}

	
	public static int deleteGuestUnits(int guestId) throws SQLException {

		log.info("GuestDAL.deleteGuestUnits(guestId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		int noRecordsDeleted = 0;
		
		String spCall = "{ call DeleteGuestUnits(?) }";
		log.info("SP Call: " + spCall);
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, guestId);
			noRecordsDeleted = spStmt.executeUpdate();


		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return noRecordsDeleted;
	}

	
	public static List<GuestUnit> getGuestUnits(int guestId) throws SQLException {
		// Return all units based on "parentId" hierarchy
		
		log.info("GuestDAL.getGuestUnits(guestId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<GuestUnit> guestUnits = new ArrayList<GuestUnit>();

		String spCall = "{ call GetGuestUnits(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, guestId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				GuestUnit guestUnit = setGuestUnitValues(rs);

				guestUnits.add(guestUnit);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return guestUnits;
	}


 	public static void save(GuestUnit guestUnit) throws SQLException {
		log.info("GuestDAL.save(guestUnit, actionUserId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		String spCall = "{ call SaveGuestUnit(?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, guestUnit.guestId);
			spStmt.setInt(2, guestUnit.guestUnitId);
			spStmt.setInt(3, guestUnit.dupUnitId);
		    
			spStmt.executeUpdate();
			
		} catch (SQLException ex) {
			log.error("GuestDAL.save: " + ex.getMessage());
			throw ex;
		}
		
		log.info("GuestDAL.save(unit, actionUserId) - end");

		return;
		
	}

}
