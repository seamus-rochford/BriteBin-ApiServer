package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.time.Instant;

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
			reading.serviceDoorOpen = ((flags & 0x40) == 0x40);
			reading.flapStuckOpen = ((flags & 0x80) == 0x80);
			
			int signalStrength = data[9] & 0xff;
			reading.nbIoTSignalStrength = signalStrength >> 4;

			// bytes 10 & 11 not used at the moment
			
			// Pass userId = 1 (admin user) so they can have access to all units
	        Unit unit = UnitDAL.getUnit(1, reading.serialNo);

	        reading.readingDateTime = Instant.now();
	        
	        // Set firmware parameters in the reading
	        reading.firmware = unit.firmware;
	        reading.binTime = unit.binTime;
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
			
			String binTime = String.format("%02d", data[7] & 0xff);
			binTime += ":" + String.format("%02d", data[8] & 0xff);
			binTime += ":" + String.format("%02d", data[9] & 0xff);

			reading.firmware = firmware;
			reading.binTime = binTime;

			// flags
			int flags5 = data[10] & 0xff;
			reading.binJustOn = ((flags5 & 0x80) == 0x01);
			reading.regularPeriodicReporting = ((flags5 & 0x80) == 0x02);
			reading.nbiotSimIssue = ((flags5 & 0x80) == 0x03);  // Irrelevant for NB-IoT because will not be received - only useful for Sigfox

			
	        unit = UnitDAL.getUnit(1, reading.serialNo);

	        reading.readingDateTime = Instant.now();
			
			log.info(reading);
			
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
			
			UnitMessage msg =  processData(rawDataId, sigfoxData);
							
        	log.debug("saveData - end");
        	
        	return msg;
        } catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
    	} 
    }

	public void markMessageAsSent(UnitMessage unitMsg) throws SQLException {
		UnitDAL.markMessageAsSent(unitMsg);
	}
	
}
