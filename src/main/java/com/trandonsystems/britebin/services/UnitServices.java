package com.trandonsystems.britebin.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;

public class UnitServices {

	static Logger log = Logger.getLogger(UnitServices.class);

	public Unit getUnit(int id) {
		log.info("UnitServices.getUnit(id)");
		return UnitDAL.get(id);
	}

	public Unit getUnit(String serialNo) {
		log.info("UnitServices.getUnit(serialNo)");
		return UnitDAL.get(serialNo);
	}

	public List<Unit> getUnits() {
		log.info("UnitServices.getUnits");
		return UnitDAL.getUnits();
	}

	public List<UnitReading> getUnitReadings(int id) {
		log.info("UnitServices.getUnitReadings(id)");
		return UnitDAL.getUnitReadings(id);
	}

	public List<UnitReading> getUnitReadings(String serialNo) {
		log.info("UnitServices.getUnitReadings(id)");
		return UnitDAL.getUnitReadings(serialNo);
	}

}
