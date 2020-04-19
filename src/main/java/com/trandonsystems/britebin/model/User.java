package com.trandonsystems.britebin.model;

import java.time.Instant;

public class User {

	public static int USER_STATUS_INACTIVE = -1;  // Locked out
	public static int USER_STATUS_REGISTERED = 0;
	public static int USER_STATUS_ACTIVE = 1;

	public static int USER_ROLE_ADMIN = 0;
	public static int USER_ROLE_MUNICIPAL = 1;
	public static int USER_ROLE_SUB_MUNICIPAL = 2;
	public static int USER_ROLE_CUSTOMER = 3;
	public static int USER_ROLE_DRIVER = 4;
	
	public int id;
	public String email;
	public String password;
	public Role role;  
	public int parentId;  
	public String parentName;
	public Status status;  // -1 = inactive, 0 - registered, 1 -  active
	public Locale locale;
	public String name;
	public String addr1;
	public String addr2;
	public String city;
	public String county;
	public String postcode;
	public Country country;
	public String mobile;
	public String homeTel;
	public String workTel;

	// System variables
	public int binLevelAlert;
	
	public Instant lastLoggedIn;
	public Instant lastActivity;

	public Instant insertDate;
	public int insertBy;
	public Instant modifiedDate;
	public int modifiedBy;
	
	public String newPassword;
}