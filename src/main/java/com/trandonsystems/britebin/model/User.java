package com.trandonsystems.britebin.model;

import java.time.Instant;

public class User {

	public static int ROLE_INACTIVE = -1;  // Locked out
	public static int ROLE_REGISTERED = 0;
	public static int ROLE_ACTIVE = 1;

	public int id;
	public String email;
	public String password;
	public int role;  
	public int parentId;  
	public int status;  // 0 - registered, 1 - email verified, 2 - verified
	public String locale;
	public String name;
	public String addr1;
	public String addr2;
	public String city;
	public String county;
	public String postcode;
	public int country;
	public String mobile;
	public String homeTel;
	public String workTel;

	public Instant lastLoggedIn;
	public Instant lastActivity;

	public Instant insertDate;
	public int insertBy;
	public Instant modifiedDate;
	public int modifiedBy;
	
}