package com.trandonsystems.britebin.model;

import java.time.Instant;

public class AlertEmail {

	public int id;
	public int alertId;
	public String email;
	public String subject;
	public boolean htmlBody;
	public String body;
	public int status;
	public Instant insertDate;
	public Instant sentDateTime;
	public String comments;

}
