package com.trandonsystems.britebin.services;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.SystemDAL;


public class SystemServices {

	static Logger log = Logger.getLogger(SystemServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

   	
	public String getMobileVersion() {
		log.info("SystemServices.getMobileVersion()");
		return SystemDAL.getSystemVariableValue("MobileVersion");
	}

}
