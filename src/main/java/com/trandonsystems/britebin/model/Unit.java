package com.trandonsystems.britebin.model;

import java.time.Instant;

public class Unit {

	public int id;
	public User owner;
	public String serialNo;
	
	public String sigfoxAltId;   // If serial number too large for sigfox use this id instead
	
	public DeviceType deviceType;
	public String location;
	public double latitude;
	public double longitude;
	public BinType binType;
	public ContentType contentType;
	public UnitStatus status;
	public boolean useBinTypeLevel;
	public int emptyLevel;
	public int fullLevel;
	public Instant lastActivity;

	// firmware values
	public String firmware;
	public long timeDiff;
	
	// flags
	public boolean binJustOn;
	public boolean regularPeriodicReporting;
	public boolean nbiotSimIssue;
	
	// Control values
	public Instant insertDate;
	public int insertBy;
	public Instant modifiedDate;
	public int modifiedBy;
		
	public int reading40percent;
	public int reading100percent;
}
