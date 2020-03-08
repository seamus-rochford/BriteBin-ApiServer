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

	public static Unit get(int id) {

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

	public static Unit get(String serialNo) {

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

	public static List<Unit> getUnits() {

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
				Timestamp insertDate = rs.getTimestamp("insert_date");
				if (insertDate == null) {
					unit.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					unit.insertDate = insertDateInstant;
				}
				unit.insertBy = rs.getInt("insert_by");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modified_date");
				if (modifiedDate == null) {
					unit.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					unit.modifiedDate = modifiedDateInstant;
				}
				unit.modifiedBy = rs.getInt("modified_by");

				units.add(unit);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return units;
	}

	public static List<UnitReading> getUnitReadings(int unitId) {

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
				unitReading.noFlapOpening = rs.getInt("binLevelBC");
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

	public static List<UnitReading> getUnitReadings(String serialNo) {

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
				unitReading.serialNo = rs.getString("serialNo");
				unitReading.msgType = rs.getInt("msgType");
				unitReading.binLevel = rs.getInt("binLevel");
				unitReading.binLevelBC = rs.getInt("BinLevelBC");
				unitReading.noFlapOpening = rs.getInt("binLevelBC");
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

	// TODO - Body not yet done
	public static Unit save(Unit unit, int currentUserId) throws Exception {
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
