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
    	log.debug("Sigfox Data: " + gson.toJson(sigfoxData));

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

		switch (reading.msgType) {
		case 1:
			reading.binLevelBC = data[1] & 0xff;
			reading.binLevel = data[2] & 0xff;
			reading.noFlapOpenings = (data[3] & 0xff) * 255 + (data[4] & 0xff);
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

			// bytes 10 & 11 not used at the moment
			
			// Pass userId = 1 (admin user) so they can have access to all units
	        Unit unit = UnitDAL.getUnit(1, reading.serialNo);

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
        	log.debug("Sigfox Data: " + gson.toJson(sigfoxData));
        	
			// Save the raw data to the DB
			long rawDataId = UnitDAL.saveRawData(gson.toJson(sigfoxData).getBytes("utf-8"));
			if (!sigfoxData.deviceID.contentEquals(serialNo)) {
				throw new Exception("API parameter id does NOT match body deviceId");
			}
			
        	log.debug("Sigfox Data: " + gson.toJson(sigfoxData));
			processData(rawDataId, sigfoxData);
							
        	log.debug("saveData - end");
        } catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
    	} 
    }
    
}
