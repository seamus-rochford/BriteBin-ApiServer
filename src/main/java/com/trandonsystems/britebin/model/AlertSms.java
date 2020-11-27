package com.trandonsystems.britebin.model;

import java.time.Instant;

public class AlertSms {

	public int id;
	public int alertId;
	public String phoneNo;
	public String message;
	public int status;
	public int httpResponseCode;
	public int responseResultCode;
	public String responseResultDesc;
	public int smsCount;
	public Instant insertDate;
	public Instant responseDateTime;
	public String comments;

}
