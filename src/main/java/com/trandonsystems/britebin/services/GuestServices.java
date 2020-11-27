package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.GuestDAL;
import com.trandonsystems.britebin.model.GuestUnit;

public class GuestServices {

	static Logger log = Logger.getLogger(UnitServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();


	public GuestUnit getGuestUnit(int guestUnitId) throws SQLException {
		log.info("GuestServices.getGuestUnit(guestUnitId)");
		return GuestDAL.getGuestUnit(guestUnitId);
	}

	
	public List<GuestUnit> getGuestUnits(int guestId) throws SQLException {		
		log.info("GuestServices.getGuestUnits(guestId)");
		return GuestDAL.getGuestUnits(guestId);
	}


	public int deleteGuestUnit(int guestUnitId) throws SQLException {
		log.info("GuestServices.deleteGuestUnit(guestUnitId)");
		return GuestDAL.deleteGuestUnit(guestUnitId);
	}

	
	public int deleteGuestUnits(int guestId) throws SQLException {
		log.info("GuestServices.deleteGuestUnits(guestId)");
		return GuestDAL.deleteGuestUnits(guestId);
	}

	
 	public void save(GuestUnit guestUnit) throws SQLException {
		log.info("GuestServices.save(guestUnit, actionUserId)");
		GuestDAL.save(guestUnit);
	}

}
