package com.trandonsystems.britebin.model;

public class Alert {

	public int id;
	
	public int customerId; // This is the bin owner
	public User user;		// This is the person to be alerted
	
	public boolean binFull;

	public boolean batteryUVLO;
	public boolean binEmptiedLastPeriod;
	public boolean batteryOverTempLO;
	public boolean binLocked;
	public boolean binTilted;
	public boolean serviceDoorOpen;
	public boolean flapStuckOpen;

	public boolean damage;

	public boolean notReporting;
	
	public boolean email;
	public boolean sms;
	public boolean whatsApp;
	public boolean pushNotification;
	
}
