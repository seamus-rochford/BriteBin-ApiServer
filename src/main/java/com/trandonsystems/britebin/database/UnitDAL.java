package com.trandonsystems.britebin.database;

import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jasypt.util.password.*;

import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;

public class UnitDAL {

	static Logger log = Logger.getLogger(UnitDAL.class);
	static ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();

	public UnitDAL() {
		log.trace("Constructor");

	}

	public static Unit get(int parentId, int id) {

		log.info("UnitDAL.get(id)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUnit(?) }";
		log.info("SP Call: " + spCall);
		
		Unit unit = new Unit();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			ResultSet rs = spStmt.executeQuery();

			unit.id = id;
			if (rs.next()) {
				unit.ownerId = rs.getInt("ownerId");
				unit.serialNo = rs.getString("serialNo");
				unit.protocolType = rs.getInt("protocolType");
				unit.location = rs.getString("location");
				unit.latitude = rs.getDouble("latitude");
				unit.longitude = rs.getDouble("longitude");
				unit.tankTypeId = rs.getInt("tankTypeId");
				unit.useTankTypeLevel = rs.getInt("useTankTypeLevel");
				unit.minLevel = rs.getInt("minLevel");
				unit.maxLevel = rs.getInt("maxLevel");

				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("lastActivity");
				if (lastActivity == null) {
					unit.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					unit.lastActivity = lastActivityInstant;
				}

				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					unit.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					unit.insertDate = insertDateInstant;
				}
				unit.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					unit.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					unit.modifiedDate = modifiedDateInstant;
				}
				unit.modifiedBy = rs.getInt("modifiedBy");
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}

		return unit;
	}

	public static Unit get(int parentId, String serialNo) {

		log.info("UnitDAL.get(serialNo)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUnitBySerialNo(?) }";
		log.info("SP Call: " + spCall);
		
		Unit unit = new Unit();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, serialNo);
			ResultSet rs = spStmt.executeQuery();

			unit.serialNo = serialNo;
			if (rs.next()) {
				unit.id = rs.getInt("id");
				unit.ownerId = rs.getInt("ownerId");
				unit.serialNo = rs.getString("serialNo");
				unit.protocolType = rs.getInt("protocolType");
				unit.location = rs.getString("location");
				unit.latitude = rs.getDouble("latitude");
				unit.longitude = rs.getDouble("longitude");
				unit.tankTypeId = rs.getInt("tankTypeId");
				unit.useTankTypeLevel = rs.getInt("useTankTypeLevel");
				unit.minLevel = rs.getInt("minLevel");
				unit.maxLevel = rs.getInt("maxLevel");

				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("lastActivity");
				if (lastActivity == null) {
					unit.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					unit.lastActivity = lastActivityInstant;
				}

				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					unit.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					unit.insertDate = insertDateInstant;
				}
				unit.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					unit.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					unit.modifiedDate = modifiedDateInstant;
				}
				unit.modifiedBy = rs.getInt("modifiedBy");
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}

		return unit;
	}

	public static Unit getUnitBySerialNo(String serialNo) {
		log.info("UnitDAL.getUnitBySerialNo(serialNo)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUnitBySerialNo(?) }";
		log.info("SP Call: " + spCall);
		
		Unit unit = new Unit();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, serialNo);
			ResultSet rs = spStmt.executeQuery();

			unit.serialNo = serialNo;
			if (rs.next()) {
				unit.id = rs.getInt("id");
				unit.location = rs.getString("location");
			}

			log.debug("UnitDAL.getUnitBySerialNo(serialNo) - end");
		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}

		return unit;
	}

	
	public static List<Unit> getUnits(int parentId) {

		log.info("UnitDAL.getUnits");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Unit> units = new ArrayList<Unit>();

		String spCall = "{ call GetUnits() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Unit unit = new Unit();

				unit.id = rs.getInt("id");
				unit.ownerId = rs.getInt("ownerId");
				unit.serialNo = rs.getString("serialNo");
				unit.protocolType = rs.getInt("protocolType");
				unit.location = rs.getString("location");
				unit.latitude = rs.getDouble("latitude");
				unit.longitude = rs.getDouble("longitude");
				unit.tankTypeId = rs.getInt("tankTypeId");
				unit.useTankTypeLevel = rs.getInt("useTankTypeLevel");
				unit.minLevel = rs.getInt("minLevel");
				unit.maxLevel = rs.getInt("maxLevel");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("lastActivity");
				if (lastActivity == null) {
					unit.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					unit.lastActivity = lastActivityInstant;
				}

				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					unit.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					unit.insertDate = insertDateInstant;
				}
				unit.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					unit.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					unit.modifiedDate = modifiedDateInstant;
				}
				unit.modifiedBy = rs.getInt("modifiedBy");

				units.add(unit);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return units;
	}

	public static List<UnitReading> getUnitReadings(int parentId, int unitId) {

		log.info("UnitDAL.getUnitReadings(unitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsByUnitId(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, unitId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = new UnitReading();

				unitReading.unitId = unitId;
				unitReading.serialNo = rs.getString("serialNo");
				unitReading.msgType = rs.getInt("msgType");
				unitReading.binLevel = rs.getInt("binLevel");
				unitReading.binLevelBC = rs.getInt("BinLevelBC");
				unitReading.noFlapOpening = rs.getInt("noFlapOpenings");
				unitReading.batteryVoltage = rs.getInt("batteryVoltage");
				unitReading.temperature = rs.getInt("temperature");
				unitReading.noCompactions = rs.getInt("noCompactions");
				unitReading.nbIoTSignalStrength = rs.getInt("nbIoTSignalStrength");
				
				unitReading.batteryUVLO = (rs.getInt("batteryUVLO") == 1);
				unitReading.binEmptiedLastPeriod = (rs.getInt("binEmptiedLastPeriod") == 1);
				unitReading.overUnderTempLO = (rs.getInt("overUnderTempLO") == 1);
				unitReading.binLocked = (rs.getInt("binLocked") == 1);
				unitReading.binFull = (rs.getInt("binFull") == 1);
				unitReading.binTilted = (rs.getInt("binTilted") == 1);
				unitReading.serviceDoorOpen = (rs.getInt("serviceDoorOpen") == 1);
				unitReading.flapStuckOpen = (rs.getInt("flapStuckOpen") == 1);

				unitReading.rssi = rs.getInt("rssi");
				unitReading.src = rs.getInt("src");
				unitReading.snr = rs.getInt("snr");
				unitReading.ber = rs.getInt("ber");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp readingDateTime = rs.getTimestamp("readingDateTime");
				if (readingDateTime == null) {
					unitReading.readingDateTime = null;
				}
				else {
					java.time.Instant readingDateTimenInstant = readingDateTime.toInstant();
					unitReading.readingDateTime = readingDateTimenInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDateTime = rs.getTimestamp("insertDateTime");
				if (insertDateTime == null) {
					unitReading.insertDateTime = null;
				}
				else {
					java.time.Instant insertDateTimeInstant = insertDateTime.toInstant();
					unitReading.insertDateTime = insertDateTimeInstant;
				}
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return unitReadings;
	}

	public static List<UnitReading> getUnitReadings(int parentId, String serialNo) {

		log.info("UnitDAL.getUnitReadings(serialNo)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadings(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, serialNo);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = new UnitReading();

				unitReading.serialNo = serialNo;
				unitReading.unitId = rs.getInt("unitId");
				unitReading.msgType = rs.getInt("msgType");
				unitReading.binLevel = rs.getInt("binLevel");
				unitReading.binLevelBC = rs.getInt("BinLevelBC");
				unitReading.noFlapOpening = rs.getInt("noFlapOpenings");
				unitReading.batteryVoltage = rs.getInt("batteryVoltage");
				unitReading.temperature = rs.getInt("temperature");
				unitReading.noCompactions = rs.getInt("noCompactions");
				unitReading.nbIoTSignalStrength = rs.getInt("nbIoTSignalStrength");
				
				unitReading.batteryUVLO = (rs.getInt("batteryUVLO") == 1);
				unitReading.binEmptiedLastPeriod = (rs.getInt("binEmptiedLastPeriod") == 1);
				unitReading.overUnderTempLO = (rs.getInt("overUnderTempLO") == 1);
				unitReading.binLocked = (rs.getInt("binLocked") == 1);
				unitReading.binFull = (rs.getInt("binFull") == 1);
				unitReading.binTilted = (rs.getInt("binTilted") == 1);
				unitReading.serviceDoorOpen = (rs.getInt("serviceDoorOpen") == 1);
				unitReading.flapStuckOpen = (rs.getInt("flapStuckOpen") == 1);

				unitReading.rssi = rs.getInt("rssi");
				unitReading.src = rs.getInt("src");
				unitReading.snr = rs.getInt("snr");
				unitReading.ber = rs.getInt("ber");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp readingDateTime = rs.getTimestamp("readingDateTime");
				if (readingDateTime == null) {
					unitReading.readingDateTime = null;
				}
				else {
					java.time.Instant readingDateTimenInstant = readingDateTime.toInstant();
					unitReading.readingDateTime = readingDateTimenInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDateTime = rs.getTimestamp("insertDateTime");
				if (insertDateTime == null) {
					unitReading.insertDateTime = null;
				}
				else {
					java.time.Instant insertDateTimeInstant = insertDateTime.toInstant();
					unitReading.insertDateTime = insertDateTimeInstant;
				}

				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return unitReadings;
	}

	public static List<UnitReading> getUnitReadings(int parentId, String serialNo, int limit) {

		log.info("UnitDAL.getUnitReadings(serialNo)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsLimit(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, serialNo);
			spStmt.setInt(2, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = new UnitReading();

				unitReading.serialNo = serialNo;
				unitReading.unitId = rs.getInt("unitId");
				unitReading.msgType = rs.getInt("msgType");
				unitReading.binLevel = rs.getInt("binLevel");
				unitReading.binLevelBC = rs.getInt("BinLevelBC");
				unitReading.noFlapOpening = rs.getInt("noFlapOpenings");
				unitReading.batteryVoltage = rs.getInt("batteryVoltage");
				unitReading.temperature = rs.getInt("temperature");
				unitReading.noCompactions = rs.getInt("noCompactions");
				unitReading.nbIoTSignalStrength = rs.getInt("nbIoTSignalStrength");
				
				unitReading.batteryUVLO = (rs.getInt("batteryUVLO") == 1);
				unitReading.binEmptiedLastPeriod = (rs.getInt("binEmptiedLastPeriod") == 1);
				unitReading.overUnderTempLO = (rs.getInt("overUnderTempLO") == 1);
				unitReading.binLocked = (rs.getInt("binLocked") == 1);
				unitReading.binFull = (rs.getInt("binFull") == 1);
				unitReading.binTilted = (rs.getInt("binTilted") == 1);
				unitReading.serviceDoorOpen = (rs.getInt("serviceDoorOpen") == 1);
				unitReading.flapStuckOpen = (rs.getInt("flapStuckOpen") == 1);

				// Convert database timestamp(UTC date) to local time instant
				Timestamp readingDateTime = rs.getTimestamp("readingDateTime");
				if (readingDateTime == null) {
					unitReading.readingDateTime = null;
				}
				else {
					java.time.Instant readingDateTimenInstant = readingDateTime.toInstant();
					unitReading.readingDateTime = readingDateTimenInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDateTime = rs.getTimestamp("insertDateTime");
				if (insertDateTime == null) {
					unitReading.insertDateTime = null;
				}
				else {
					java.time.Instant insertDateTimeInstant = insertDateTime.toInstant();
					unitReading.insertDateTime = insertDateTimeInstant;
				}

				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return unitReadings;
	}

	public static long saveRawData(byte[] data) throws SQLException{

		log.info("UnitDAL.saveRawData(data) - start");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
			throw new SQLException("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveRawData(?) }";
		log.info("SP Call: " + spCall);

		long id = 0;
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setBytes(1, data);
			ResultSet rs = spStmt.executeQuery();
			
			if (rs.next()) {
				id = rs.getInt("id");
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
		
		log.info("UnitDAL.saveRawData(data) - end");
		return id;
	}	

	public static void saveReading(long rawDataId, long unitId, UnitReading reading) throws SQLException {

		log.info("UnitDAL.saveReading(rawDataId, unitId, reading)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveReading(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setLong(1, unitId);
			spStmt.setString(2, reading.serialNo);
			spStmt.setLong(3, rawDataId);
			spStmt.setInt(4, reading.msgType);
			spStmt.setInt(5, reading.binLevelBC);
			spStmt.setInt(6, reading.binLevel);
			spStmt.setInt(7, reading.noFlapOpening);
			spStmt.setInt(8, reading.batteryVoltage);
			spStmt.setInt(9, reading.temperature);
			spStmt.setInt(10, reading.noCompactions);
			spStmt.setInt(11, reading.batteryUVLO ? 1 : 0);
			spStmt.setInt(12, reading.binEmptiedLastPeriod ? 1 : 0);
			spStmt.setInt(13, reading.overUnderTempLO ? 1 : 0);
			spStmt.setInt(14, reading.binLocked ? 1 : 0);
			spStmt.setInt(15, reading.binFull ? 1 : 0);
			spStmt.setInt(16, reading.binTilted ? 1 : 0);
			spStmt.setInt(17, reading.serviceDoorOpen ? 1 : 0);
			spStmt.setInt(18, reading.flapStuckOpen ? 1 : 0);
			spStmt.setInt(19, reading.nbIoTSignalStrength);
			spStmt.setInt(20, reading.rssi);
			spStmt.setInt(21, reading.src);
			spStmt.setInt(22, reading.snr);
			spStmt.setInt(23, reading.ber);

			// Convert java.time.Instant to java.sql.timestamp
			Timestamp ts = Timestamp.from(reading.readingDateTime);
		    spStmt.setTimestamp(24, ts);
		    
		    spStmt.executeQuery();

		} catch (SQLException ex) {
			log.error("UnitDAL.saveReading: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UnitDAL.saveReading(rawDataId, unitId, reading) - end");

		return;
	}
	


	// TODO - Body not yet done
 	public static Unit save(int parentId, Unit unit, int currentUserId) throws Exception {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		log.info("UnitDAL.save");
		int unitId = unit.id;

		String sqlStmt = "UPDATE unit SET fname = ?, lname = ?, tel = ?, email = ?, role_id = ?, status = ?, operator_id = ?, bank_id = ?, modified_date = ?, modified_by = ?"
					+ " WHERE id = ?";

		log.debug("SQL: " + sqlStmt);
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password)) {

			try (PreparedStatement prepStmt = conn.prepareStatement(sqlStmt, Statement.RETURN_GENERATED_KEYS)) {
				conn.setAutoCommit(false);

				log.debug("Set name: " + unit.location);
				prepStmt.setString(1, unit.location);
								
				Instant currentInstant = Instant.now().truncatedTo(ChronoUnit.SECONDS); //gives UTC datetime
				unit.modifiedDate = currentInstant;
				prepStmt.setTimestamp(9, Timestamp.from(currentInstant));
				
				// Modified By
				unit.modifiedBy = currentUserId ;
				prepStmt.setInt(10, currentUserId);
				
				prepStmt.setInt(11,  unit.id);
				
				log.debug("Execute SQL");
				log.debug("SQL: " + prepStmt.toString());
				
				int affectedRows = prepStmt.executeUpdate();

				log.debug("SQL executed - affectedRows: " + affectedRows + "        UnitId: " + unitId);
				
				// If successful insert
				if (affectedRows > 0 && unitId == 0) {
					ResultSet generatedKeys = prepStmt.getGeneratedKeys();
					if (generatedKeys.next()) {
						unitId = generatedKeys.getInt(1);
						unit.id = unitId;
					}
				}
				
				conn.commit();

				log.info("Unit update complete ... unit id: " + unit.id);

				return unit;
			} catch (Exception e) {
				log.error(e.getMessage());

				conn.rollback();
				throw e;
			}
		} catch (Exception e) {
			log.error(e.getMessage());

			throw e;
		}		
	}

}
