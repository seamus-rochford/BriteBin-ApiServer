package com.trandonsystems.britebin.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.LookupDAL;
import com.trandonsystems.britebin.model.BinContentType;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.Role;

public class LookupServices {

	static Logger log = Logger.getLogger(LookupServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

   	
	public List<BinType> getBinTypes(String locale) {
		log.info("LookupServices.getBinTypes(locale)");
		return LookupDAL.getBinTypes(locale);
	}
	
	public List<BinContentType> getBinContentTypes(String locale) {
		log.info("LookupServices.getBinContentTypes(locale)");
		return LookupDAL.getBinContentTypes(locale);
	}
	
	public List<Country> getCountries(String locale) {
		log.info("LookupServices.getCountries(locale)");
		return LookupDAL.getCountries(locale);
	}
	
	public List<Role> getRoles(String locale) {
		log.info("LookupServices.getRoles(locale)");
		return LookupDAL.getRoles(locale);
	}
	
}
