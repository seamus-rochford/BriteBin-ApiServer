package com.trandonsystems.britebin.model;

import java.time.Instant;

public class AlertPush {

	public int id;
	public int alertId;
	public String gcmToken;
	public String title;
	public String body;
	public int status;
	public Instant insertDate;
	public String comments;
	public int responseResultCode;
	public String responseResultDesc;
	public Instant responseDateTime;

}
