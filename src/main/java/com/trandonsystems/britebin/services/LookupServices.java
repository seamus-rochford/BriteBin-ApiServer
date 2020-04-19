package com.trandonsystems.britebin.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.LookupDAL;
import com.trandonsystems.britebin.model.ContentType;
import com.trandonsystems.britebin.model.BinLevel;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.Locale;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.model.Status;

public class LookupServices {

	static Logger log = Logger.getLogger(LookupServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

   	
	public List<BinLevel> getBinLevels(String locale) {
		log.info("LookupServices.getBinLevels(locale)");
		return LookupDAL.getBinLevels(locale);
	}
	
	public List<BinType> getBinTypes(String locale) {
		log.info("LookupServices.getBinTypes(locale)");
		return LookupDAL.getBinTypes(locale);
	}
	
	public List<ContentType> getContentTypes(String locale) {
		log.info("LookupServices.getContentTypes(locale)");
		return LookupDAL.getContentTypes(locale);
	}
	
	public List<DeviceType> getDeviceTypes(String locale) {
		log.info("LookupServices.getDeviceTypes(locale)");
		return LookupDAL.getDeviceTypes(locale);
	}
	
	public List<Country> getCountries(String locale) {
		log.info("LookupServices.getCountries(locale)");
		return LookupDAL.getCountries(locale);
	}
	
	public List<Locale> getLocales(String locale) {
		log.info("LookupServices.getLocales(locale)");
		return LookupDAL.getLocales(locale);
	}
	
	public List<Role> getRoles(String locale) {
		log.info("LookupServices.getRoles(locale)");
		return LookupDAL.getRoles(locale);
	}
	
	public List<Status> getStatus(String locale) {
		log.info("LookupServices.getRoles(locale)");
		return LookupDAL.getStatus(locale);
	}
	
}
