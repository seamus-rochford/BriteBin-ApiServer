package com.trandonsystems.britebin.services;

import java.time.Instant;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.SigfoxBody;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;


public class SigfoxServices {

	static Logger log = Logger.getLogger(SigfoxServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public void processData(long rawDataId, SigfoxBody sigfoxData) throws Exception {
		log.info("processData - start");

		UnitReading reading = new UnitReading();

		reading.serialNo = sigfoxData.deviceId;
		try {
			reading.rssi = Integer.parseInt(sigfoxData.rssi);
		} catch (NumberFormatException ex) {
			throw new Exception("rssi - must be an integer");
		}
		try {
			reading.snr = Integer.parseInt(sigfoxData.snr);
		} catch (NumberFormatException ex) {
			throw new Exception("snr - must be an integer");
		}
		
		// get the payload
		byte[] data = Hex.hexStringToByteArray(sigfoxData.payload);
		
		reading.msgType = data[0] & 0xff;

		switch (reading.msgType) {
		case 1:
			reading.binLevelBC = data[1] & 0xff;
			reading.binLevel = data[2] & 0xff;
			reading.noFlapOpening = (data[3] & 0xff) * 255 + (data[4] & 0xff);
			reading.batteryVoltage = data[5] & 0xff;
			reading.temperature = data[6];   // signed value
			reading.noCompactions = data[7] & 0xff;
			
			int flags = data[8] & 0xff;
			reading.batteryUVLO = ((flags & 0x80) == 0x80);
			reading.binEmptiedLastPeriod = ((flags & 0x40) == 0x40);
			reading.overUnderTempLO = ((flags & 0x20) == 0x20);
			reading.binLocked = ((flags & 0x10) == 0x10);
			reading.binFull = ((flags & 0x08) == 0x08);
			reading.binTilted = ((flags & 0x04) == 0x04);
			reading.serviceDoorOpen = ((flags & 0x02) == 0x02);
			reading.flapStuckOpen = ((flags & 0x01) == 0x01);
			
			int signalStrength = data[9] & 0xff;
			reading.nbIoTSignalStrength = signalStrength >> 4;

			// bytes 10 & 11 not used at the moment
			
	        Unit unit = UnitDAL.getUnitBySerialNo(reading.serialNo);

	        reading.readingDateTime = Instant.now();
			
			log.info(reading);
			
			UnitDAL.saveReading(rawDataId, unit.id, reading);
			
			break;
			
		default:
			throw new Exception("SigfoxServices.processData: Unknown message type msgType: " + reading.msgType);
		}

		log.info("processData - end");

		return;
	}
	
	public void saveData(String serialNo, SigfoxBody sigfoxData) throws Exception {
        try {
        	log.debug("saveData - start");

			// Save the raw data to the DB
			long rawDataId = UnitDAL.saveRawData(gson.toJson(sigfoxData).getBytes("utf-8"));
			if (!sigfoxData.deviceId.contentEquals(serialNo)) {
				throw new Exception("API parameter id does NOT match body deviceId");
			}
			
			processData(rawDataId, sigfoxData);
							
        	log.debug("saveData - end");
        } catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
    	} 
    }
    
}
