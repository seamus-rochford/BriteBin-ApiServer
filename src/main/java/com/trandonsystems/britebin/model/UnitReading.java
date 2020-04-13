package com.trandonsystems.britebin.model;

import java.time.Instant;

public class UnitReading {

	public long id;
	
	public Unit unit;
	
	public String serialNo;
	
	// data
	public int msgType;
	
	public int binLevel;		// unsigned
	public int binLevelBC;		// unsigned
	public int noFlapOpenings;	// unsigned
	public int batteryVoltage;	// unsigned
	public int temperature;		// *** signed ***
	public int noCompactions;	// unsigned
	
	// Signal strengths
	public int nbIoTSignalStrength;
	
	//flags
	public boolean batteryUVLO;
	public boolean binEmptiedLastPeriod;
	public boolean batteryOverTempLO;
	public boolean binLocked;
	public boolean binFull;
	public boolean binTilted;
	public boolean serviceDoorOpen;
	public boolean flapStuckOpen;

	public double rssi;		// Received Signal Strength Indicator
	public int src;			// Sonic Result Code - used by tekelek
	public double snr;
	public int ber;
	public double rsrq;	// Reference Signal Received Quality
	public int rsrp; 	// Reference Signal Received Power
	
	public Instant readingDateTime;
	public Instant insertDateTime;
}
