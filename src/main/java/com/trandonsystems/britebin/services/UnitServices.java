package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;


import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.RawData;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;


public class UnitServices {

	static Logger log = Logger.getLogger(UnitServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public Unit getUnit(int userFilterId, int id) {
		log.info("UnitServices.getUnit(userFilterId, id)");
		return UnitDAL.getUnit(userFilterId, id);
	}

	public Unit getUnit(int userFilterId, String serialNo) {
		log.info("UnitServices.getUnit(userFilterId, serialNo)");
		return UnitDAL.getUnit(userFilterId, serialNo);
	}

	public List<Unit> getUnits(int userFilterId) {
		log.info("UnitServices.getUnits(userFilterId)");
		return UnitDAL.getUnits(userFilterId);
	}

	public Unit save(Unit unit, int actionUserId) throws SQLException {
		log.info("UnitService.save");
		
		return UnitDAL.save(unit, actionUserId);
	}
	
	public Unit install(Unit unit, int actionUserId) throws SQLException {
		log.info("UnitService.install");
		
		// Get the unit details form the DB
		Unit dbUnit = UnitDAL.getUnit(actionUserId, unit.serialNo);
		log.info("dbUnit: " + gson.toJson(dbUnit));

		dbUnit.latitude = unit.latitude;
		dbUnit.longitude = unit.longitude;
		dbUnit.location = unit.location;
		
		// Update the unit latitude and longitude and location
		return UnitDAL.install(dbUnit, actionUserId);
	}
	
	public List<UnitReading> getUnitReadings(int userFilterId, int id, int limit) {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.getUnitReadings(userFilterId, id, limit);
	}

	public List<UnitReading> getUnitReadings(int userFilterId, String serialNo, int limit) {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.getUnitReadings(userFilterId, serialNo, limit);
	}

	public List<UnitReading> pullReadings(int userFilterId, int unitId, String serialNo) throws SQLException {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.pullReadings(userFilterId, unitId, serialNo);
	}

	// for engineering testing only
	public List<UnitReading> getUnitReadingsTest(String serialNo, int limit) {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.getUnitReadingsTest(serialNo, limit);
	}

	public List<UnitReading> getLatestReadings(int userFilterId) {
		log.info("UnitServices.getLatestReadings(userFilterId)");
		return UnitDAL.getLatestReadings(userFilterId);
	}

	public void saveMessage(int unitId, byte[] data, int userId) throws SQLException {
		UnitDAL.saveMessage(unitId, data, userId);
	}
	
	public List<RawData> getUnprocessedRawData(String source) throws SQLException {
		
		List<RawData> readings = UnitDAL.getUnprocessedRawData(source);
		
		return readings;
	}
	
	public void processBriteBinDataOnly(String source, long rawDataId, Instant readingDateTime, byte[] data) throws Exception {
		// This is used to processRawData (NB-IoT BriteBin) that did not get processed - it does not send back a unitMessage because it is 
		// not communicating directly with the device
		log.info("saveUnitReadingOnly - start");

		try {
			UnitReading reading = new UnitReading();
	
			reading.msgType = data[0] & 0xff;
			
			// Helper variables
			int idSize;
			Unit unit;
	
			switch (reading.msgType) {
			case 1:
				reading.binLevelBC = data[1] & 0xff;
				reading.binLevel = data[2] & 0xff;
				reading.noFlapOpenings = (data[3] & 0xff) * 256 + (data[4] & 0xff);
				reading.batteryVoltage = data[5] & 0xff;
				reading.temperature = data[6];   // signed value
				reading.noCompactions = data[7] & 0xff;
				
				int flags = data[8] & 0xff;
				reading.batteryUVLO = ((flags & 0x01) == 0x01);
				reading.binEmptiedLastPeriod = ((flags & 0x02) == 0x02);
				reading.batteryOverTempLO = ((flags & 0x04) == 0x04);
				reading.binLocked = ((flags & 0x08) == 0x08);
				reading.binFull = ((flags & 0x10) == 0x10);
				reading.binTilted = ((flags & 0x20) == 0x20);
				reading.serviceDoorOpen = ((flags & 0x40) == 0x40);
				reading.flapStuckOpen = ((flags & 0x80) == 0x80);
				
				int signalStrength = data[9] & 0xff;
				reading.nbIoTSignalStrength = signalStrength >> 4;
				
				// These come in as unsigned and i need to formulate them
				reading.rsrq = -19.5 + 0.5 * (data[10] & 0xff);   
				reading.rsrp = -140 + (data[11] & 0xff);
				
				idSize = data[12]; // note this is byte length
				
				if (data.length > 30) {
					// BriteBin Tcp Messages are 30 bytes or less
					throw new Exception("BriteBin messages are 30 bytes or less - this message is " + data.length + "bytes long");
				} else if (data.length < (13 + idSize)) {
					throw new Exception("There is not enough bytes in the message for a serialNo of size " + idSize + " bytes.");
				}
				reading.serialNo = "";
				for (int i = 0; i < idSize; i++) {
					reading.serialNo += Hex.ByteToHex(data[13 + i]);
				}
				
				log.debug("SerialNo (before): " + reading.serialNo);
				// Remove leading zero's from serialNo
				int i = 0;
				while (reading.serialNo.charAt(i) == '0' && i < reading.serialNo.length() - 1)
				    i++;
				reading.serialNo = reading.serialNo.substring(i).toUpperCase();
				log.debug("SerialNo (after): " + reading.serialNo);
				
		        unit = UnitDAL.getUnit(1, reading.serialNo);
	
		        reading.readingDateTime = readingDateTime;
				
		        // Set firmware parameters in the reading
		        reading.firmware = unit.firmware;
		        reading.timeDiff = unit.timeDiff;
		        reading.binJustOn = unit.binJustOn;
		        reading.regularPeriodicReporting = unit.regularPeriodicReporting;
		        reading.nbiotSimIssue = unit.nbiotSimIssue;
		        
		        log.info(reading);
				
				UnitDAL.saveReadingOnly(source, rawDataId, unit.id, reading);
				
				break;
			case 5:
				String firmware = String.format("%02d", data[1] & 0xff);
				firmware += "-" + String.format("%02d", data[2] & 0xff);
				firmware += "-" + String.format("%02d", data[3] & 0xff);
				firmware += " " + String.format("%02d", data[4] & 0xff);
				firmware += ":" + String.format("%02d", data[5] & 0xff);
				firmware += ":" + String.format("%02d", data[6] & 0xff);
				
				reading.firmware = firmware;

				try {
					int hour = Integer.parseInt(String.format("%02d", data[7] & 0xff));
					int minute = Integer.parseInt(String.format("%02d", data[8] & 0xff));
					int second = Integer.parseInt(String.format("%02d", data[9] & 0xff));

					Instant now = Instant.now();
					Instant unitTime = now.atZone(ZoneOffset.UTC)
												.withHour(hour)
												.withMinute(minute)
												.withSecond(second)
												.toInstant();
					long timeDiff = unitTime.getEpochSecond() - now.getEpochSecond();
				
					reading.timeDiff = timeDiff;
				} catch (Exception ex) {
					log.error("ERROR: Message Type = 5: Converting device time to valid time failed " + ex.getMessage());
					reading.timeDiff = 0;
				}
	
				// flags
				int flags5 = data[10] & 0xff;
				reading.binJustOn = ((flags5 & 0x01) == 0x01);
				reading.regularPeriodicReporting = ((flags5 & 0x02) == 0x02);
				reading.nbiotSimIssue = ((flags5 & 0x04) == 0x04);  // irrelevent for NB-IoT because will not be received - only useful for Sigfox
	
				// Byte 11 reserved - ignore
				
				idSize = data[12]; // note this is byte length
				
				if (data.length > 30) {
					// BriteBin TCP Messages are 30 bytes or less
					throw new Exception("BriteBin messages are 30 bytes or less - this message is " + data.length + "bytes long");
				} else if (data.length < (13 + idSize)) {
					throw new Exception("There is not enough bytes in the message for a serialNo of size " + idSize + " bytes.");
				}
				reading.serialNo = "";
				for (int j = 0; j < idSize; j++) {
					reading.serialNo += Hex.ByteToHex(data[13 + j]);
				}
				
				log.debug("SerialNo (before): " + reading.serialNo);
				// Remove leading zero's from serialNo
				int j = 0;
				while (reading.serialNo.charAt(j) == '0' && j < reading.serialNo.length() - 1)
				    j++;
				reading.serialNo = reading.serialNo.substring(j).toUpperCase();
				log.debug("SerialNo (after): " + reading.serialNo);
				
		        unit = UnitDAL.getUnit(1, reading.serialNo);
	
		        reading.readingDateTime = readingDateTime;
				
				log.info(reading);
				
				UnitDAL.saveReadingFirmwareOnly(rawDataId, unit.id, reading);
				
				break;
			default:
				throw new Exception("UnitServices.saveUnitReading: Unknown message type msgType: " + reading.msgType);
			}
	
			log.info("saveUnitReadingOnly - end");
			
		} catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
		
		return;
	}

	// This considerably different from BriteBin reading processing
	// Tekelek send multiple messages in a single TCP communication
	public void processTekelekDataOnly(String source, long rawDataId, Instant readingDateTime, byte[] data) throws Exception {
		// This is used to processRawData (NB-IoT Tekelek) that did not get processed - it does not send back a unitMessage because it is 
		// not communicating directly with the device
		
		log.info("processTekelekData - start");
		UnitReading reading = new UnitReading();

		int productType = data[0] & 0xff;

        // serialNo is IMEI - each of the half bytes in bytes 7 to 14 have the digits of the IMEI - converting each byte to it HEX will give each of the 2 digits in that byte
        reading.serialNo = Hex.ByteToHex(data[7]) + Hex.ByteToHex(data[8]) + Hex.ByteToHex(data[9]) + Hex.ByteToHex(data[10]) 
                        + Hex.ByteToHex(data[11]) + Hex.ByteToHex(data[12]) + Hex.ByteToHex(data[13]) + Hex.ByteToHex(data[14]);
        Unit unit = UnitDAL.getUnit(1, reading.serialNo);
		        
        reading.msgType = (int)data[15];

        int sampleInterval = 0;
        int loggerSpeed = (int)data[23];

        if (reading.msgType == 8) {
            sampleInterval = ((loggerSpeed & 128) == 128) ? 15 : 1;
        } else if (reading.msgType == 4) {
            int noIntervals = loggerSpeed & 127;  // lower 7 bits define the number of intervals between logging samples

            sampleInterval = 15 * noIntervals;
        }
 
      
        log.debug("Product Type:" + productType);
        log.debug("IMEI:" + reading.serialNo);
        log.debug("Message Type:" + reading.msgType);
        log.debug("Logger Interval: " + sampleInterval);

        // Only interested in message type 4 & 8 - ignore all other message types
        if (reading.msgType != 4 && reading.msgType != 8) {
            log.info("Message Type: " + reading.msgType + " >>> Not supported");
        } else {
	
        	// This is the time the raw-data was saved, so the readings are relative to this
	        Instant readingTime = readingDateTime;
	
	        int index = 26;
	        int readingsCount = 0;
	        boolean finished = false;
	        while (!finished) {
	            // Process the data 
	            reading.rssi = (int)data[index] & 15;
	            reading.temperature = ((int)(data[index + 1] & 0xff) >> 1) - 30;
	            reading.src = ((int)data[index + 2] >> 2) & 15;
	            reading.binLevel = (((int)data[index + 2] & 3) << 8) + (int)(data[index + 3] & 0xff);
	
	            log.debug("RTC: " + readingTime + "   cms: " + reading.binLevel);
	
	            // Put default values into all other fields - NOT used by Tekelek units
				reading.binLevelBC = 0;
				reading.noFlapOpenings = 0;
				reading.batteryVoltage = 0;
				reading.noCompactions = 0;
				
				reading.batteryUVLO = false;
				reading.binEmptiedLastPeriod = false;
				reading.batteryOverTempLO = false;
				reading.binLocked = false;
				reading.binFull = false;
				reading.binTilted = false;
				reading.serviceDoorOpen = false;
				reading.flapStuckOpen = false;
				
				reading.nbIoTSignalStrength = 0;
				reading.snr = 0;
				reading.ber = 0;

				reading.readingDateTime = readingTime;
	            log.info(gson.toJson(reading));
				
	            // Save data to database
				UnitDAL.saveReadingOnly(source, rawDataId, unit.id, reading);
	
	            readingsCount++;
	
	            if (readingsCount == 28) {
	                finished = true;
	            } else {
	                index += 4;
	
	                readingTime = readingTime.minus(sampleInterval, ChronoUnit.MINUTES);;
	
	                if (data[index] + data[index+1] + data[index+2] + data[index+3] == 0) {
	                    finished = true;
	                }
	            }
	        }
	        
        }
		log.info("processTekelekData - end");

		return;
	}
	

	
}
