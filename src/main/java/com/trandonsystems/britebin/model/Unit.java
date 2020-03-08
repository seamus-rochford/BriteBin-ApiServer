package com.trandonsystems.britebin.model;

import java.time.Instant;

public class Unit {

	public int id;
	public int ownerId;
	public String serialNo;
	public int protocolType;
	public String location;
	public double latitude;
	public double longitude;
	public int tankTypeId;
	public int useTankTypeLevel;
	public int minLevel;
	public int maxLevel;
	public Instant lastActivity;

	public Instant insertDate;
	public int insertBy;
	public Instant modifiedDate;
	public int modifiedBy;
		
}
