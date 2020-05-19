package com.trandonsystems.britebin.database;

import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.ContentType;
import com.trandonsystems.britebin.model.BinLevel;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.model.User;

public class UnitDAL {

	static Logger log = Logger.getLogger(UnitDAL.class);

	static final String SOURCE = "Sigfox";   // Saving Readings
	
	
	public UnitDAL() {
		log.trace("Constructor");
	}

	
	public static Unit getUnit(int userFilterId, int id) {

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

			unit.id = id;
			if (rs.next()) {

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

	
	public static Unit getUnit(int userFilterId, String serialNo) {

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

			unit.serialNo = serialNo.toUpperCase();
			if (rs.next()) {
				unit.id = rs.getInt("id");

				User owner = new User();
				owner.id = rs.getInt("users.id");
				owner.name = rs.getString("users.name");
				unit.owner = owner;
				
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

	
	public static List<Unit> getUnits(int userFilterId) {
		// Return all units based on "parentId" hierarchy
		
		log.info("UnitDAL.getUnits(userFilterId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Unit> units = new ArrayList<Unit>();

		String spCall = "{ call GetUnits(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Unit unit = new Unit();

				unit.id = rs.getInt("id");

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
	
	
	private static int computePercentageTekelek(int binType, int value) {
		int result = 0;
		
		double reading40percent = 0;
		double reading100percent = 0;
		
		if (binType == 1) {
			// Model 120
			reading40percent = 49.4;
			reading100percent = 18.0;
		} else {
			// Model 240, Model 360
			reading40percent = 73.9;
			reading100percent = 18.0;
		}
		
		double dominator = reading40percent - reading100percent;
		result = (int)Math.round(100 - (value - reading100percent) / dominator * 60);

		log.debug("computePercentageTekelek - Value: " + value + "    Percentage: " + result + " %");
		
		// Negative readings are wrong so for readability, we will display them as 0 %
		if (result < 0) {
			result = 0;
		}
		
		return result;
	}
	
	
	private static int computePercentagePelBin(int binType, int value) {
		int result = 0;
		
		double readingZeropercent = 0;
		double reading40percent = 0;
		double reading50percent = 0;
		double reading100percent = 0;
		
		if (binType == 1) {
			// Model 120 - uses reading at 50% to interpolate readings above and below 50%
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
				result = (int)Math.round(60 * (value - reading40percent) / (readingZeropercent - reading50percent)) + 40;
			} else {
				result = (int)Math.round(40 * (value - reading100percent) / (reading40percent - reading100percent));
			}
		}

		log.debug("computePercentagePelBin - Value: " + value + "    Percentage: " + result + " %");

		// Negative readings are wrong so for readability, we will display them as 0 %
		if (result < 0) {
			result = 0;
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
			unitReading.compactionDone = false;
		}
		
		unitReading.noFlapOpenings = rs.getInt("noFlapOpenings");
		unitReading.batteryVoltageReading = rs.getInt("batteryVoltage");
		unitReading.temperature = rs.getInt("temperature");
		unitReading.noCompactions = rs.getInt("noCompactions");
		unitReading.nbIoTSignalStrength = rs.getInt("nbIoTSignalStrength");
		
		unitReading.batteryUVLO = (rs.getInt("batteryUVLO") == 1);
		unitReading.binEmptiedLastPeriod = (rs.getInt("binEmptiedLastPeriod") == 1);
		unitReading.batteryOverTempLO = (rs.getInt("batteryOverTempLO") == 1);
		unitReading.binLocked = (rs.getInt("binLocked") == 1);
		unitReading.binFull = (rs.getInt("binFull") == 1);
		unitReading.binTilted = (rs.getInt("binTilted") == 1);
		unitReading.serviceDoorOpen = (rs.getInt("serviceDoorOpen") == 1);
		unitReading.flapStuckOpen = (rs.getInt("flapStuckOpen") == 1);

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
		
		return unitReading;
	}
	
	
	public static List<UnitReading> getUnitReadings(int userFilterId, int unitId, int limit) {

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
		}

		return unitReadings;
	}

	
	public static List<UnitReading> getUnitReadings(int userFilterId, String serialNo, int limit) {

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
		}

		return unitReadings;
	}

	
	// This one is for engineering testing only
	public static List<UnitReading> getUnitReadingsTest(String serialNo, int limit) {

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

	
	public static List<UnitReading> getLatestReadings(int userFilterId) {

		log.info("UnitDAL.getLatestReadings(userFilterId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetLatestReadings(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);

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

		String spCall = "{ call SaveRawReadings(?, ?) }";
		log.info("SP Call: " + spCall);

		long id = 0;
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setBytes(1, data);
			spStmt.setString(2, SOURCE);
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

		String spCall = "{ call SaveReading(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
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
		    
			spStmt.setString(25, SOURCE);

			spStmt.executeQuery();

		} catch (SQLException ex) {
			log.error("UnitDAL.saveReading: " + ex.getMessage());
			throw ex;
		}
		
		log.info("UnitDAL.saveReading(rawDataId, unitId, reading) - end");

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
