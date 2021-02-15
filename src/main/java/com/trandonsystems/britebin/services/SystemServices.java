package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.SystemDAL;
import com.trandonsystems.britebin.model.KeyValue;


public class SystemServices {

	static Logger log = Logger.getLogger(SystemServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public String getSysConfigValue(String name) throws SQLException {
		log.info("SystemServices.getSysConfigValue()");
		return SystemDAL.getSysConfigValue(name);
	}

	public List<KeyValue> getSysConfigValues() throws SQLException {
		log.info("SystemServices.getSysConfigValues()");
		return SystemDAL.getSysConfigValues();
	}

	public void saveSysConfigValue(KeyValue kv, int actionUserId) throws SQLException {
		log.info("SystemServices.saveConfigValue(kv)");
		SystemDAL.saveSysConfigValue(kv, actionUserId);
	}
}
