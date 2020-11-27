package com.trandonsystems.britebin.model;

import java.time.Instant;

// This object is used to get records from the alert table for trouble shooting
// It is used in conjunction with the getAlerts API call
public class AlertObject {

	public int id;
	public int alertType;
	public int unitId;
	public Instant alertDateTime;
	public int status;
	public int unitReadingId;
	public String comments;
	public int damageId;
	
}
