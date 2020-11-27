package com.trandonsystems.britebin.database;

import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.ContentType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.model.BinLevel;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.RawData;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitMessage;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.model.UnitStatus;
import com.trandonsystems.britebin.model.User;

public class UnitDAL {

	static Logger log = Logger.getLogger(UnitDAL.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	static final String SOURCE_SIGFOX = "Sigfox";   // Saving Readings
	
	
	public UnitDAL() {
		log.trace("Constructor");
	}

	public static Unit setUnitValues(ResultSet rs) throws SQLException {
		Unit unit = new Unit();
				
		unit.id  = rs.getInt("units.id");

		User owner = new User();
		owner.id = rs.getInt("users.id");
		owner.name = rs.getString("users.name");
		unit.owner = owner;
		
		unit.serialNo = rs.getString("serialNo");
		
		DeviceType deviceType = new DeviceType();
		deviceType.id = rs.getInt("ref_device_type.id");
		deviceType.name = rs.getString("ref_device_type.name");
		unit.deviceType = deviceType;
		
		unit.location = rs.getString("location");
		unit.latitude = rs.getDouble("latitude");
		unit.longitude = rs.getDouble("longitude");
		
		BinType binType = new BinType();
		binType.id = rs.getInt("ref_bin_type.id");
		binType.name = rs.getString("ref_bin_type.name");
		binType.emptyLevel = rs.getInt("ref_bin_type.emptyLevel");
		binType.fullLevel = rs.getInt("ref_bin_type.fullLevel");
		unit.binType = binType;
		
		ContentType contentType = new ContentType();
		contentType.id = rs.getInt("ref_content_type.id");
		contentType.name = rs.getString("ref_content_type.name");	
		unit.contentType = contentType;
		
		unit.useBinTypeLevel = (rs.getInt("useBinTypeLevel") == 1);
		unit.emptyLevel = rs.getInt("emptyLevel");
		unit.fullLevel = rs.getInt("fullLevel");

		UnitStatus unitStatus = new UnitStatus();
		unitStatus.id = rs.getInt("ref_unit_status.id");
		unitStatus.name = rs.getString("ref_unit_status.name");
		unit.status = unitStatus;
		
		// Convert database timestamp(UTC date) to local time instant
		Timestamp lastActivity = rs.getTimestamp("lastActivity");
		if (lastActivity == null) {
			unit.lastActivity = null;
		}
		else {
			java.time.Instant lastActivityInstant = lastActivity.toInstant();
			unit.lastActivity = lastActivityInstant;
		}
		
		// firmware values
		unit.firmware = rs.getString("units.firmware");
		unit.timeDiff = rs.getLong("units.timeDiff");
		unit.binJustOn = (rs.getInt("units.binJustOn") == 1);
		unit.regularPeriodicReporting = (rs.getInt("units.regularPeriodicReporting") == 1);
		unit.nbiotSimIssue = (rs.getInt("units.nbiotIssue") == 1);

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
		
		
		return unit;
	}
	
		
	public static Unit getUnit(int userFilterId, int id) throws SQLException {

		log.info("UnitDAL.getUnit(userFilterId, id)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUnit(?, ?) }";
		log.info("SP Call: " + spCall);
		
		Unit unit = new Unit();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, id);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				unit = setUnitValues(rs);
			} else {
				throw new SQLException("No unit exists with id = " + id + " for customer with id = " + userFilterId);
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return unit;
	}

	
	public static Unit getUnit(int userFilterId, String serialNo) throws SQLException {

		log.info("UnitDAL.getUnit(userFilterId, serialNo)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUnitBySerialNo(?, ?) }";
		log.info("SP Call: " + spCall);
		
		Unit unit = new Unit();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setString(2, serialNo.toUpperCase());
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				unit = setUnitValues(rs);
			} else {
				// No Unit exist for serialNo for this customer - return an empty unit definition
				unit = new Unit();
				unit.id = 0;
				// Do not throw an error because this will cause a raw data reading not to be saved to the data_readings table
//				throw new SQLException("No unit exists with serialNo = " + serialNo + " for customer with id = " + userFilterId);
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return unit;
	}

	
	public static List<Unit> getUnits(int userFilterId, boolean includeDeactive) throws SQLException {
		// Return all units based on "parentId" hierarchy
		
		log.info("UnitDAL.getUnits(userFilterId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Unit> units = new ArrayList<Unit>();

		String spCall = "{ call GetUnits(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, includeDeactive ? 1 : 0);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Unit unit = setUnitValues(rs);

				units.add(unit);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return units;
	}
	
	
	public static List<String> getUnitsUnregistered() throws SQLException {
		// Return all unit serialNos where no unit is defined
		
		log.info("UnitDAL.getUnitsUnregistered()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<String> serailNos = new ArrayList<String>();

		String spCall = "{ call getUnitsUnregistered(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				String serailNo = rs.getString("serialNo");

				serailNos.add(serailNo);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return serailNos;
	}
	
	
	public static int computePercentageTekelek(int binType, int value) {
		int result = 0;
		
		double reading40percent = 0;
		double reading100percent = 0;
		
		if (binType == 1 || binType == 5) {
			// Model 120 (binType = 1) or Model 170 (binType = 5)
			reading40percent = 49.4;
			reading100percent = 18.0;
		} else {
			// Model 240, Model 360, Model 600
			reading40percent = 73.9;
			reading100percent = 18.0;
		}
		
		double dominator = reading40percent - reading100percent;
		result = (int)Math.round(100 - (value - reading100percent) / dominator * 60);

		log.debug("computePercentageTekelek - Value: " + value + "    Percentage: " + result + " %");
		
		// Negative readings are wrong so for readability, we will display them as 0 %
		if (result < 0) {
			result = 0;
		} else if (result > 100) {
			result = 100;
		}
		
		return result;
	}
	
	
	public static int computePercentagePelBin(int binType, int value) {
		int result = 0;
		
		double readingZeropercent = 0;
		double reading40percent = 0;
		double reading50percent = 0;
		double reading100percent = 0;
		
		if (binType == 1 || binType == 5) {
			// Model 120 (binType = 1) or Model 170 (binType = 5)
			// uses reading at 50% to interpolate readings above and below 50%
			readingZeropercent = 156;
			reading50percent = 53;
			reading100percent = 29;
			
			if (value >= reading50percent) {
				result = (int)Math.round(50 * (value - reading50percent) / (readingZeropercent - reading50percent)) + 50;
			} else {
				result = (int)Math.round(50 * (value - reading100percent) / (reading50percent - reading100percent));
			}
		} else {
			// Model 240, Model 360  - uses reading at 40% to interpolate readings above and below 40%
			readingZeropercent = 140;
			reading40percent = 42;
			reading100percent = 21;

			if (value >= reading40percent) {
				result = (int)Math.round(60 * (value - reading40percent) / (readingZeropercent - reading40percent)) + 40;
			} else {
				result = (int)Math.round(40 * (value - reading100percent) / (reading40percent - reading100percent));
			}
		}

		log.debug("computePercentagePelBin - Value: " + value + "    Percentage: " + result + " %");

		// Negative readings are wrong so for readability, we will display them as 0 %
		if (result < 0) {
			result = 0;
		} else if (result > 100) {
			result = 100;
		}
		
		return result;
	}
	
	
	public static UnitReading setUnitReadingValues(ResultSet rs) throws SQLException {
		UnitReading unitReading = new UnitReading();
		
		unitReading.id = rs.getInt("unit_readings.id");
		
		Unit unit = new Unit();
		unit.id  = rs.getInt("units.id");
		unit.serialNo = rs.getString("serialNo");
		unit.location = rs.getString("location");
		unit.latitude = rs.getDouble("latitude");
		unit.longitude = rs.getDouble("longitude");
		
		User owner = new User();
		owner.id = rs.getInt("users.id");
		owner.name = rs.getString("users.name");
		owner.email = rs.getString("users.email");
		unit.owner = owner;
		
		DeviceType deviceType = new DeviceType();
		deviceType.id = rs.getInt("ref_device_type.id");
		deviceType.name = rs.getString("ref_device_type.name");
		unit.deviceType = deviceType;
		
		BinType binType = new BinType();
		binType.id = rs.getInt("ref_bin_type.id");
		binType.name = rs.getString("ref_bin_type.name");
		binType.emptyLevel = rs.getInt("ref_bin_type.emptyLevel");
		binType.fullLevel = rs.getInt("ref_bin_type.fullLevel");
		unit.binType = binType;
		
		ContentType contentType = new ContentType();
		contentType.id = rs.getInt("ref_content_type.id");
		contentType.name = rs.getString("ref_content_type.name");	
		unit.contentType = contentType;
		
		unit.useBinTypeLevel = (rs.getInt("useBinTypeLevel") == 1);
		unit.emptyLevel = rs.getInt("emptyLevel");
		unit.fullLevel = rs.getInt("fullLevel");
		
		UnitStatus unitStatus = new UnitStatus();
		unitStatus.id = rs.getInt("ref_unit_status.id");
		unitStatus.name = rs.getString("ref_unit_status.name");
		unit.status = unitStatus;
		
		// Convert database timestamp(UTC date) to local time instant
		Timestamp lastActivity = rs.getTimestamp("lastActivity");
		if (lastActivity == null) {
			unit.lastActivity = null;
		}
		else {
			java.time.Instant lastActivityInstant = lastActivity.toInstant();
			unit.lastActivity = lastActivityInstant;
		}				
		unitReading.unit = unit;
		
		unitReading.serialNo = rs.getString("serialNo");
		unitReading.msgType = rs.getInt("msgType");
		unitReading.binLevel = rs.getInt("binLevel");
		unitReading.binLevelBC = rs.getInt("BinLevelBC");
		if (unitReading.binLevel == 0) {
			// No compaction done
			unitReading.compactionDone = false;
			unitReading.binLevel = unitReading.binLevelBC;
		} else {
			unitReading.compactionDone = true;
		}
		
		unitReading.noFlapOpenings = rs.getInt("noFlapOpenings");
		unitReading.batteryVoltageReading = rs.getInt("batteryVoltage");
		unitReading.temperature = rs.getInt("temperature");
		unitReading.noCompactions = rs.getInt("noCompactions");
		unitReading.nbIoTSignalStrength = 0;
		
		unitReading.batteryUVLO = (rs.getInt("batteryUVLO") == 1);
		unitReading.binEmptiedLastPeriod = (rs.getInt("binEmptiedLastPeriod") == 1);
		unitReading.batteryOverTempLO = (rs.getInt("batteryOverTempLO") == 1);
		unitReading.binLocked = (rs.getInt("binLocked") == 1);
		unitReading.binFull = (rs.getInt("binFull") == 1);
		unitReading.binTilted = (rs.getInt("binTilted") == 1);
		unitReading.serviceDoorOpen = (rs.getInt("serviceDoorOpen") == 1);
		unitReading.flapStuckOpen = (rs.getInt("flapStuckOpen") == 1);

		unitReading.serviceDoorClosed = (rs.getInt("serviceDoorClosed") == 1);

		unitReading.rssi = rs.getDouble("rssi");
		unitReading.src = rs.getInt("src");
		unitReading.snr = rs.getDouble("snr");
		unitReading.ber = rs.getInt("ber");
		unitReading.rsrq = rs.getDouble("rsrq");
		unitReading.rsrp = rs.getInt("rsrp");
		
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
		Timestamp insertDateTime = rs.getTimestamp("unit_readings.insertDateTime");
		if (insertDateTime == null) {
			unitReading.insertDateTime = null;
		}
		else {
			java.time.Instant insertDateTimeInstant = insertDateTime.toInstant();
			unitReading.insertDateTime = insertDateTimeInstant;
		}
		unitReading.source = rs.getString("unit_readings.source");
		
		// Convert battery voltage reading to voltage voltage = reading * 0.05 + 7
		unitReading.batteryVoltage = unitReading.batteryVoltageReading * 0.05 + 7;
		
		// Compute Percentages
		if (unit.deviceType.id == 1) {
			// Tekelek Sensor
			unitReading.binLevelPercent = computePercentageTekelek(unit.binType.id, unitReading.binLevel);
			unitReading.binLevelBCPercent = computePercentageTekelek(unit.binType.id, unitReading.binLevelBC);
		} else {
			// Pel Bin Sensor
			unitReading.binLevelPercent = computePercentagePelBin(unit.binType.id, unitReading.binLevel);
			unitReading.binLevelBCPercent = computePercentagePelBin(unit.binType.id, unitReading.binLevelBC);
		}
		
		// Decide on the BinLevel
		int binLevelStatus = BinLevel.BIN_EMPTY;
		log.debug("Unit Id: " + unit.id);
		log.debug("useBinTypeLevel: " + unit.useBinTypeLevel);
		if (unit.useBinTypeLevel) {
			if (unitReading.binLevelPercent >= binType.fullLevel) {
				binLevelStatus = BinLevel.BIN_FULL;
			} else if (unitReading.binLevelPercent <= binType.emptyLevel) {
				binLevelStatus = BinLevel.BIN_EMPTY;
			} else {
				binLevelStatus = BinLevel.BIN_BETWEEN;
			}
			log.debug("Status set from BinType: " + binLevelStatus);
		} else { // Use unit bin levels
			if (unitReading.binLevelPercent >= unit.fullLevel) {
				binLevelStatus = BinLevel.BIN_FULL;
			} else if (unitReading.binLevelPercent <= unit.emptyLevel) {
				binLevelStatus = BinLevel.BIN_EMPTY;
			} else {
				binLevelStatus = BinLevel.BIN_BETWEEN;
			}
			log.debug("Status set from Unit: " + binLevelStatus);
		}
		unitReading.binLevelStatus = binLevelStatus;
		
		// firmware values
		unitReading.firmware = rs.getString("unit_readings.firmware");
		unitReading.timeDiff = rs.getLong("unit_readings.timeDiff");
		unitReading.binJustOn = (rs.getInt("unit_readings.binJustOn") == 1);
		unitReading.regularPeriodicReporting = (rs.getInt("unit_readings.regularPeriodicReporting") == 1);
		unitReading.nbiotSimIssue = (rs.getInt("unit_readings.nbiotIssue") == 1);
		
		return unitReading;
	}
	
	
	public static List<UnitReading> getUnitReadings(int userFilterId, int unitId, int limit) throws SQLException {

		log.info("UnitDAL.getUnitReadings(unitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsByUnitId(?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, unitId);
			spStmt.setInt(3, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}

	
	public static List<UnitReading> getUnitReadings(int userFilterId, String serialNo, int limit) throws SQLException {

		log.info("UnitDAL.getUnitReadings(userFilterId, serialNo, limit)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadings(?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setString(2, serialNo.toUpperCase());
			spStmt.setInt(3, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}


	// Pagination routines
	public static List<UnitReading> getUnitReadingsFirst(int userFilterId, int unitId, int limit) throws SQLException {

		log.info("UnitDAL.getUnitReadingsFirst(userFilterId, unitId, noRecords)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsFirst(?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, unitId);
			spStmt.setInt(3, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}

	
	public static List<UnitReading> getUnitReadingsNext(int userFilterId, int unitId, int lastId, int limit) throws SQLException {

		log.info("UnitDAL.getUnitReadings(unitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsNext(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, unitId);
			spStmt.setInt(3, lastId);
			spStmt.setInt(4, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}

	
	public static List<UnitReading> getUnitReadingsPrev(int userFilterId, int unitId, int lastId, int limit) throws SQLException {

		log.info("UnitDAL.getUnitReadings(unitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsPrev(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, unitId);
			spStmt.setInt(3, lastId);
			spStmt.setInt(4, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}

	
	public static List<UnitReading> getUnitReadingsLast(int userFilterId, int unitId, int limit) throws SQLException {

		log.info("UnitDAL.getUnitReadings(unitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsLast(?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, unitId);
			spStmt.setInt(3, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}

	
	
	// This one is for engineering testing only
	public static List<UnitReading> getUnitReadingsTest(String serialNo, int limit) throws SQLException {

		log.info("UnitDAL.getUnitReadings(userFilterId, serialNo, limit)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetUnitReadingsTest(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, serialNo.toUpperCase());
			spStmt.setInt(2, limit);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);
				
				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}

	
	private static void RecoverReadings(String serialNo, int unitId) throws SQLException {
		log.info("UnitDAL.RecoverReadings(serialNo, unitId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call RecoverReadings(?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, serialNo.toUpperCase());
			spStmt.setInt(2, unitId);

			spStmt.executeUpdate();
			
		} catch (SQLException ex) {
			log.error("UserDAL.RecoverReadings: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UserDAL.RecoverReadings - end");


	}
	
	
	public static List<UnitReading> pullReadings(int userFilterId, int unitId, String serialNo) throws SQLException {

		log.info("UnitDAL.pullReadings");
		RecoverReadings(serialNo, unitId);
		
		List<UnitReading> unitReadings = getUnitReadings(userFilterId, unitId, -1);

		return unitReadings;
	}

	
	public static List<UnitReading> getLatestReadings(int userFilterId, boolean includeDeactive) throws SQLException {

		log.info("UnitDAL.getLatestReadings(userFilterId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetLatestReadings(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, includeDeactive ? 1 : 0);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);

				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}
	

	public static long saveRawData(byte[] data) throws SQLException {

		log.info("UnitDAL.saveRawData(data) - start");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
			throw new SQLException("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveRawReadings(?, ?) }";
		log.info("SP Call: " + spCall);

		long id = 0;
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setBytes(1, data);
			spStmt.setString(2, SOURCE_SIGFOX);
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

	
	// This is generic to all reading types Sigfox/NB-IoT briteBin/NB-IoT Tekelek
	public static List<RawData> getUnprocessedRawData(String source) throws SQLException {
		log.info("UnitDAL.getUnprocessData(" + source + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}
 
		log.debug("Source: " + source);
		String spCall = "{ call GetUnprocessRawData(?) }";
		log.debug("SP Call: " + spCall);

		List<RawData> readings = new ArrayList<RawData>();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {
			spStmt.setString(1, source);
			ResultSet rs = spStmt.executeQuery();	
			
			while (rs.next()) {
				RawData rawData = new RawData();

				rawData.id = rs.getInt("id");
				rawData.source = rs.getString("source");
				rawData.rawData = rs.getBytes("rawData");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertAt = rs.getTimestamp("insertAt");
				if (insertAt == null) {
					rawData.insertAt = null;
				}
				else {
					java.time.Instant insertAtInstant = insertAt.toInstant();
					rawData.insertAt = insertAtInstant;
				}	
				
				readings.add(rawData);
			}
			
		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
		
		return readings;
	}
	
	
	public static UnitMessage getUnitMsg(Connection conn, String serialNo) throws SQLException {
		log.info("UnitDAL.getUnit(conn, serialNo)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}
 
		log.debug("SerialNo: " + serialNo);
		String spCall = "{ call GetUnitMessage(?) }";
		log.debug("SP Call: " + spCall);

		UnitMessage unitMsg = new UnitMessage();
		
		try (CallableStatement spStmt = conn.prepareCall(spCall)) {
			spStmt.setString(1, serialNo);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				unitMsg.unitId = rs.getInt("unitId");
				unitMsg.replyMessage = rs.getBoolean("replyMessage");
				unitMsg.messageId = rs.getInt("messageId");
				unitMsg.message = rs.getBytes("message");
			}
		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
		
		if (unitMsg.message != null) {
			int msgType = unitMsg.message[0] & 0xff;
			if (msgType == 4) {
				// Get the current UTC Date/Time to set for the unit
				
				// Get UTC Date/Time
				LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
				
				byte[] msg = new byte[8];
				msg[0] = (byte)msgType;
				msg[1] = (byte)(now.getYear() % 100);  // Get 2 digit year part
				msg[2] = (byte)now.getMonthValue();
				msg[3] = (byte)now.getDayOfMonth();
				msg[4] = (byte)now.getHour();
				msg[5] = (byte)now.getMinute();
				msg[6] = (byte)now.getSecond();
				msg[7] = unitMsg.message[7];
				
				unitMsg.message = msg;
			}
		}

		return unitMsg;
	}	

	
	// This is specific to Sigfox - NB-IoT save readings are done in their own specific Listener
	public static UnitMessage saveReading(long rawDataId, long unitId, UnitReading reading) throws SQLException {

		log.info("UnitDAL.saveReading(rawDataId, unitId, reading)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		UnitMessage unitMsg = new UnitMessage();
		
		String spCall = "{ call SaveReading(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			unitMsg = getUnitMsg(conn, reading.serialNo);
			log.debug("unitMsg: " + gson.toJson(unitMsg));		

			spStmt.setLong(1, unitId);
			spStmt.setString(2, reading.serialNo.toUpperCase());
			spStmt.setLong(3, rawDataId);
			spStmt.setInt(4, reading.msgType);
			spStmt.setInt(5, reading.binLevelBC);
			spStmt.setInt(6, reading.binLevel);
			spStmt.setInt(7, reading.noFlapOpenings);
			spStmt.setInt(8, reading.batteryVoltageReading);
			spStmt.setInt(9, reading.temperature);
			spStmt.setInt(10, reading.noCompactions);
			spStmt.setInt(11, reading.batteryUVLO ? 1 : 0);
			spStmt.setInt(12, reading.binEmptiedLastPeriod ? 1 : 0);
			spStmt.setInt(13, reading.batteryOverTempLO ? 1 : 0);
			spStmt.setInt(14, reading.binLocked ? 1 : 0);
			spStmt.setInt(15, reading.binFull ? 1 : 0);
			spStmt.setInt(16, reading.binTilted ? 1 : 0);
			spStmt.setInt(17, reading.serviceDoorOpen ? 1 : 0);
			spStmt.setInt(18, reading.flapStuckOpen ? 1 : 0);
			spStmt.setInt(19, reading.serviceDoorClosed ? 1 : 0);
			spStmt.setDouble(20, reading.rssi);
			spStmt.setInt(21, reading.src);
			spStmt.setDouble(22, reading.snr);
			spStmt.setInt(23, reading.ber);

			// Convert java.time.Instant to java.sql.timestamp
			Timestamp ts = Timestamp.from(reading.readingDateTime);
		    spStmt.setTimestamp(24, ts);
		    
			spStmt.setString(25, SOURCE_SIGFOX);

			spStmt.setString(26, reading.firmware);
			spStmt.setLong(27, reading.timeDiff);
			spStmt.setInt(28, reading.binJustOn ? 1 : 0);
			spStmt.setInt(29, reading.regularPeriodicReporting ? 1 : 0);
			spStmt.setInt(30, reading.nbiotSimIssue ? 1 : 0);
			
			spStmt.executeQuery();

		} catch (SQLException ex) {
			log.error("UnitDAL.saveReading: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UnitDAL.saveReading(rawDataId, unitId, reading) - end");

		return unitMsg;
	}
	
	
	// This is generic to all reading types Sigfox/NB-IoT briteBin/NB-IoT Tekelek
	public static void saveReadingOnly(String source, long rawDataId, long unitId, UnitReading reading) throws SQLException {
		// This is similar to saveReadings but it does NOT return a msg to be sent back to the the device
		// This routine is used to process rawData that was NOT processed already

		log.info("UnitDAL.saveReading(rawDataId, unitId, reading)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveReading(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {	

			spStmt.setLong(1, unitId);
			spStmt.setString(2, reading.serialNo.toUpperCase());
			spStmt.setLong(3, rawDataId);
			spStmt.setInt(4, reading.msgType);
			spStmt.setInt(5, reading.binLevelBC);
			spStmt.setInt(6, reading.binLevel);
			spStmt.setInt(7, reading.noFlapOpenings);
			spStmt.setInt(8, reading.batteryVoltageReading);
			spStmt.setInt(9, reading.temperature);
			spStmt.setInt(10, reading.noCompactions);
			spStmt.setInt(11, reading.batteryUVLO ? 1 : 0);
			spStmt.setInt(12, reading.binEmptiedLastPeriod ? 1 : 0);
			spStmt.setInt(13, reading.batteryOverTempLO ? 1 : 0);
			spStmt.setInt(14, reading.binLocked ? 1 : 0);
			spStmt.setInt(15, reading.binFull ? 1 : 0);
			spStmt.setInt(16, reading.binTilted ? 1 : 0);
			spStmt.setInt(17, reading.serviceDoorOpen ? 1 : 0);
			spStmt.setInt(18, reading.flapStuckOpen ? 1 : 0);
			spStmt.setInt(19, reading.nbIoTSignalStrength);
			spStmt.setDouble(20, reading.rssi);
			spStmt.setInt(21, reading.src);
			spStmt.setDouble(22, reading.snr);
			spStmt.setInt(23, reading.ber);

			// Convert java.time.Instant to java.sql.timestamp
			Timestamp ts = Timestamp.from(reading.readingDateTime);
		    spStmt.setTimestamp(24, ts);
		    
			spStmt.setString(25, source);

			spStmt.setString(26, reading.firmware);
			spStmt.setLong(27, reading.timeDiff);
			spStmt.setInt(28, reading.binJustOn ? 1 : 0);
			spStmt.setInt(29, reading.regularPeriodicReporting ? 1 : 0);
			spStmt.setInt(30, reading.nbiotSimIssue ? 1 : 0);
			
			spStmt.executeQuery();

		} catch (SQLException ex) {
			log.error("UnitDAL.saveReading: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UnitDAL.saveReading(rawDataId, unitId, reading) - end");

		return;
	}
	
	
	// This is specific to Sigfox
	public static UnitMessage saveReadingFirmware(long rawDataId, long unitId, UnitReading reading) throws SQLException {

		log.info("UnitDAL.saveReadingFirmware(rawDataId, unitId, reading)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		UnitMessage unitMsg = new UnitMessage();
		
		String spCall = "{ call saveReadingFirmware(?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			unitMsg = getUnitMsg(conn, reading.serialNo);
			log.debug("unitMsg: " + gson.toJson(unitMsg));		

			spStmt.setLong(1, unitId);
			spStmt.setLong(2, rawDataId);
			spStmt.setString(3, reading.firmware);
			spStmt.setLong(4, reading.timeDiff);
			spStmt.setInt(5, reading.binJustOn ? 1 : 0);
			spStmt.setInt(6, reading.regularPeriodicReporting ? 1 : 0);
			spStmt.setInt(7, reading.nbiotSimIssue ? 1 : 0);
			spStmt.setString(8, SOURCE_SIGFOX);

			spStmt.executeQuery();

		} catch (SQLException ex) {
			log.error("UnitDAL.saveReadingFirmware: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UnitDAL.saveReadingFirmware(rawDataId, unitId, reading) - end");

		return unitMsg;
	}
	
	
	// This is specific to Sigfox
	public static void saveReadingFirmwareOnly(long rawDataId, long unitId, UnitReading reading) throws SQLException {
		// This is similar to saveReadingFirmware but it does NOT return a msg to be sent back to the the device
		// This routine is used to process rawData that was NOT processed already

		log.info("UnitDAL.saveReadingFirmwareOnly(rawDataId, unitId, reading)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call saveReadingFirmware(?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setLong(1, unitId);
			spStmt.setLong(2, rawDataId);
			spStmt.setString(3, reading.firmware);
			spStmt.setLong(4, reading.timeDiff);
			spStmt.setInt(5, reading.binJustOn ? 1 : 0);
			spStmt.setInt(6, reading.regularPeriodicReporting ? 1 : 0);
			spStmt.setInt(7, reading.nbiotSimIssue ? 1 : 0);
			spStmt.setString(8, SOURCE_SIGFOX);

			spStmt.executeQuery();

		} catch (SQLException ex) {
			log.error("UnitDAL.saveReadingFirmware: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UnitDAL.saveReadingFirmware(rawDataId, unitId, reading) - end");

		return;
	}
	
	
	public static long saveMessage(int unitId, byte[] msg, int userId) throws SQLException{

		log.info("UnitDAL.saveMessage(unitId, msg)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveUnitMessage(?, ?, ?) }";
		log.info("SP Call: " + spCall);

		long id = 0;
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, unitId);
			spStmt.setBytes(2, msg);
			spStmt.setInt(3, userId);
			ResultSet rs = spStmt.executeQuery();
			
			if (rs.next()) {
				id = rs.getInt("id");
			}

		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}
		
		return id;
	}
	

	public static void markMessageAsSent(UnitMessage unitMsg) throws SQLException{

		log.info("UnitDAL.markMessageAsSent(unitMsg)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call MarkMessageAsSent(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, unitMsg.messageId);
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("UnitDAL.getUnit" + ex.getMessage());
			throw ex;
		}
	}
	
	
 	public static void deactivate(int unitId, int userActionId) throws SQLException {
		log.info("UnitDAL.deactivate(unitId, actionUserId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		String spCall = "{ call deactivateUnit(?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, unitId);
			spStmt.setInt(2, userActionId);
			
			spStmt.executeUpdate();
			
		} catch (SQLException ex) {
			log.error("UserDAL.deactivate: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UserDAL.deactivate(unit, actionUserId) - end");
	}

 	
 	public static void activate(int unitId, int userActionId) throws SQLException {
		log.info("UnitDAL.activate(unitId, actionUserId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		String spCall = "{ call activateUnit(?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, unitId);
			spStmt.setInt(2, userActionId);
			
			spStmt.executeUpdate();
			
		} catch (SQLException ex) {
			log.error("UserDAL.activate: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UserDAL.deactivate(unit, actionUserId) - end");
	}

 	
 	public static Unit save(Unit unit, int actionUserId) throws SQLException {
		log.info("UnitDAL.save(unit, actionUserId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		String spCall = "{ call SaveUnit(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setLong(1, unit.id);
			spStmt.setInt(2, unit.owner.id);
			spStmt.setString(3, unit.serialNo.toUpperCase());
			spStmt.setInt(4, unit.deviceType.id);
			spStmt.setString(5, unit.location);
			spStmt.setDouble(6, unit.latitude);
			spStmt.setDouble(7, unit.longitude);
			spStmt.setInt(8, unit.binType.id);
			spStmt.setInt(9, unit.contentType.id);
			spStmt.setInt(10, unit.useBinTypeLevel ? 1 : 0);
			spStmt.setInt(11, unit.emptyLevel);
			spStmt.setInt(12, unit.fullLevel);
			spStmt.setInt(13, actionUserId);
		    
			spStmt.registerOutParameter(1, Types.BIGINT);
			
			spStmt.executeUpdate();
			
			unit.id = spStmt.getInt(1);
			
			log.debug("userId: " + unit.id);
			
		} catch (SQLException ex) {
			log.error("UserDAL.save: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UserDAL.save(unit, actionUserId) - end");

		return unit;
		
	}

 	
 	public static Unit install(Unit unit, int actionUserId) throws SQLException {
		log.info("UnitDAL.install(unit, actionUserId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		String spCall = "{ call InstallUnit(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setLong(1, unit.id);
			spStmt.setInt(2, unit.owner.id);
			spStmt.setString(3, unit.serialNo.toUpperCase());
			spStmt.setInt(4, unit.deviceType.id);
			spStmt.setString(5, unit.location);
			spStmt.setDouble(6, unit.latitude);
			spStmt.setDouble(7, unit.longitude);
			spStmt.setInt(8, unit.binType.id);
			spStmt.setInt(9, unit.contentType.id);
			spStmt.setInt(10, unit.useBinTypeLevel ? 1 : 0);
			spStmt.setInt(11, unit.emptyLevel);
			spStmt.setInt(12, unit.fullLevel);
			spStmt.setInt(13, actionUserId);
		    
			spStmt.registerOutParameter(1, Types.BIGINT);
			
			spStmt.executeUpdate();
			
			unit.id = spStmt.getInt(1);
			
			log.debug("userId: " + unit.id);
			
		} catch (SQLException ex) {
			log.error("UserDAL.install: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UserDAL.install(unit, actionUserId) - end");

		return unit;
	}

}
