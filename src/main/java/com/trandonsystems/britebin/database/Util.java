package com.trandonsystems.britebin.database;

import java.security.*;

public class Util {

	// Local DB
	public static String connUrl = "jdbc:mysql://localhost:3306/britebin";
	public static String username = "admin";
	public static String password = "Rebel123456#.";

	public static String MD5(String md5) {
	   try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(md5.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < array.length; ++i) {
	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	       }
	        return sb.toString();
	    } catch (java.security.NoSuchAlgorithmException e) {
	    }
	    return null;
	}
}
