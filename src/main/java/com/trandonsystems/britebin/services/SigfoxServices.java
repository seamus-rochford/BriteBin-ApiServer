package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.SigfoxBody;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitMessage;
import com.trandonsystems.britebin.model.UnitReading;


public class SigfoxServices {

	static Logger log = Logger.getLogger(SigfoxServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	static final String SOURCE_SIGFOX = "Sigfox";   // Saving Readings

	
	public UnitMessage processData(long rawDataId, SigfoxBody sigfoxData) throws Exception {
		log.info("processData - start");

		UnitReading reading = new UnitReading();

		reading.serialNo = sigfoxData.deviceID;
		try {
			reading.rssi = Double.parseDouble(sigfoxData.rssi);
		} catch (Exception ex) {
			reading.rssi = 0.0;
		}
		try {
			reading.snr = Double.parseDouble(sigfoxData.snr);
		} catch (Exception ex) {
			reading.snr = 0.0;
		}
		
		byte[] data = Hex.hexStringToByteArray(sigfoxData.data);
		
		// Pass userId = 1 (admin user) so they can have access to all units
        Unit unit = UnitDAL.getUnit(1, reading.serialNo);

		reading.msgType = data[0] & 0xff;
		
		UnitMessage unitMsg = new UnitMessage();
		
		switch (reading.msgType) {
		case 1:  // Message Type 1
			reading.binLevelBC = data[1] & 0xff;
			reading.binLevel = data[2] & 0xff;
			reading.noFlapOpenings = (data[3] & 0xff) * 255 + (data[4] & 0xff);
			reading.batteryVoltageReading = data[5] & 0xff;
			reading.temperature = data[6];   // signed value
			reading.noCompactions = data[7] & 0xff;
			
			int flags = data[8] & 0xff;
			reading.batteryUVLO = ((flags & 0x01) == 0x01);
			reading.binEmptiedLastPeriod = ((flags & 0x02) == 0x02);
			reading.batteryOverTempLO = ((flags & 0x04) == 0x04);
			reading.binLocked = ((flags & 0x08) == 0x08);
			reading.binFull = ((flags & 0x10) == 0x10);
			reading.binTilted = ((flags & 0x20) == 0x20);
			reading.serviceDoorClosed = ((flags & 0x40) == 0x40);
			reading.flapStuckOpen = ((flags & 0x80) == 0x80);
			
			flags = data[9] & 0xff;
			reading.serviceDoorOpen = ((flags & 0x01) == 0x01);
			
			// No longer used
//			int signalStrength = data[9] & 0xff;
//			reading.nbIoTSignalStrength = signalStrength >> 4;
			reading.nbIoTSignalStrength = 0;

			// bytes 10 & 11 not used at the moment
			
	        reading.readingDateTime = Instant.now();
	        
	        // Set firmware parameters in the reading
	        reading.firmware = unit.firmware;
	        reading.timeDiff = unit.timeDiff;
	        reading.binJustOn = unit.binJustOn;
	        reading.regularPeriodicReporting = unit.regularPeriodicReporting;
	        reading.nbiotSimIssue = unit.nbiotSimIssue;
			
			unitMsg = UnitDAL.saveReading(rawDataId, unit.id, reading);
			
			unitMsg.serialNo = unit.serialNo;
			break;
		
		case 5:  // Message Type = 5
			String firmware = String.format("%02d", data[1] & 0xff);
			firmware += "-" + String.format("%02d", data[2] & 0xff);
			firmware += "-" + String.format("%02d", data[3] & 0xff);
			firmware += " " + String.format("%02d", data[4] & 0xff);
			firmware += ":" + String.format("%02d", data[5] & 0xff);
			firmware += ":" + String.format("%02d", data[6] & 0xff);
			
			reading.firmware = firmware;

			// flags
			int flags5 = data[10] & 0xff;
			reading.binJustOn = ((flags5 & 0x80) == 0x01);
			reading.regularPeriodicReporting = ((flags5 & 0x80) == 0x02);
			reading.nbiotSimIssue = ((flags5 & 0x80) == 0x03);  // Irrelevant for NB-IoT because will not be received - only useful for Sigfox
			
	        reading.readingDateTime = Instant.now();
			
			log.info(reading);
			
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
				
				// Note the time may not be correct on a firmware send just after a switch on - so ignore after a just on
				// timeDiff is in seconds if timeDiff > or less than 5 minutes (300 seconds) request to sent a time rest to the firmware
		        if (!reading.binJustOn && (timeDiff <= 300 || timeDiff >= 300)) {
		        	
		        	// Save we want to set the time but the time bytes will only be set just before we send the message 
		        	// - do NOT save current date/time because this will be wrong by the time we send the message
		        	byte[] msgData = {(byte)0x04, // Message Type = 4 - set time on the unit
							(byte)0x00, // year = 00
							(byte)0x00, // month = 00
							(byte)0x00, // day = 00
							(byte)0x00, // hours = 00
							(byte)0x00, // minutes = 00
							(byte)0x00, // seconds = 00
							(byte)0x00 // Flags - all set to ignore
		        	};
		        	
		        	UnitDAL.saveMessage(unit.id, msgData, 0);
		        }
			} catch (Exception ex) {
				log.error("ERROR: Message Type = 5: Converting device time to valid time failed " + ex.getMessage());
				reading.timeDiff = 0;
			}

			unitMsg = UnitDAL.saveReadingFirmware(rawDataId, unit.id, reading);
		
			unitMsg.serialNo = unit.serialNo;

			break;
			
		default:
			throw new Exception("SigfoxServices.processData: Unknown message type msgType: " + reading.msgType);
		}


		log.info("processData - end");

		return unitMsg;
	}
	
	
 	public UnitMessage saveData(String serialNo, SigfoxBody sigfoxData) throws Exception {
        try {
//        	log.debug("saveData - start");
//        	log.debug("Sigfox Data: " + gson.toJson(sigfoxData));
        	
			// Save the raw data to the DB
			long rawDataId = UnitDAL.saveRawData(gson.toJson(sigfoxData).getBytes("utf-8"));
			if (!sigfoxData.deviceID.contentEquals(serialNo)) {
				throw new Exception("API parameter id does NOT match body deviceId");
			}
			
			UnitMessage msg = processData(rawDataId, sigfoxData);
							
        	log.debug("saveData - end");
        	
        	return msg;
        } catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
    	} 
    }
	
 	
	public void saveRawDataOnly(int rawDataId, Instant readingDateTime, SigfoxBody sigfoxData) throws Exception {
 		// This is used to process data got from rawData table, therefore, we do NOT want to save it to the rawData table either
 		// And since it is not called from a device, we do not want to process any return messages
		// And since it could be in the rawData table for a while, we need to use the rawData insertAt date/time
		log.info("saveRawDataOnly - start");

		UnitReading reading = new UnitReading();
		
		try {
			reading.serialNo = sigfoxData.deviceID;
			try {
				reading.rssi = Double.parseDouble(sigfoxData.rssi);
			} catch (Exception ex) {
				reading.rssi = 0.0;
			}
			try {
				reading.snr = Double.parseDouble(sigfoxData.snr);
			} catch (Exception ex) {
				reading.snr = 0.0;
			}
			
			byte[] data = Hex.hexStringToByteArray(sigfoxData.data);
			
			reading.msgType = data[0] & 0xff;
			
			switch (reading.msgType) {
			case 1:  // Message Type 1
				reading.binLevelBC = data[1] & 0xff;
				reading.binLevel = data[2] & 0xff;
				reading.noFlapOpenings = (data[3] & 0xff) * 255 + (data[4] & 0xff);
				reading.batteryVoltageReading = data[5] & 0xff;
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
	
				// bytes 10 & 11 not used at the moment
				
				// Pass userId = 1 (admin user) so they can have access to all units
		        Unit unit = UnitDAL.getUnit(1, reading.serialNo);
	
		        reading.readingDateTime = readingDateTime;
		        
		        // Set firmware parameters in the reading
		        reading.firmware = unit.firmware;
		        reading.timeDiff = unit.timeDiff;
		        reading.binJustOn = unit.binJustOn;
		        reading.regularPeriodicReporting = unit.regularPeriodicReporting;
		        reading.nbiotSimIssue = unit.nbiotSimIssue;
				
				UnitDAL.saveReadingOnly(SOURCE_SIGFOX, rawDataId, unit.id, reading);
				break;
			
			case 5:  // Message Type = 5
				if (data.length < 10) {
					throw new Exception("RawDataId: " + rawDataId + " - Message type 5 Error - data should be at least 11 bytes long");
				}
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
				reading.binJustOn = ((flags5 & 0x80) == 0x01);
				reading.regularPeriodicReporting = ((flags5 & 0x80) == 0x02);
				reading.nbiotSimIssue = ((flags5 & 0x80) == 0x03);  // Irrelevant for NB-IoT because will not be received - only useful for Sigfox
	
				
		        unit = UnitDAL.getUnit(1, reading.serialNo);
	
		        reading.readingDateTime = Instant.now();
				
				log.info(reading);
				
				UnitDAL.saveReadingFirmwareOnly(rawDataId, unit.id, reading);
			
				break;
				
			default:
				throw new Exception("SigfoxServices.processData: Unknown message type msgType: " + reading.msgType);
			}
	
	
			log.info("saveRawDataOnly - end");
			
        } catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
    	} 
		
		return;
	}
	
	
	public void markMessageAsSent(UnitMessage unitMsg) throws SQLException {
		UnitDAL.markMessageAsSent(unitMsg);
	}
	
}
