package com.trandonsystems.britebin.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;

public class UnitServices {

	static Logger log = Logger.getLogger(UnitServices.class);

	public Unit getUnit(int parentId, int id) {
		log.info("UnitServices.getUnit(parentId, id)");
		return UnitDAL.get(parentId, id);
	}

	public Unit getUnit(int parentId, String serialNo) {
		log.info("UnitServices.getUnit(parentId, serialNo)");
		return UnitDAL.get(parentId, serialNo);
	}

	public List<Unit> getUnits(int parentId) {
		log.info("UnitServices.getUnits(parentId)");
		return UnitDAL.getUnits(parentId);
	}

	public List<UnitReading> getUnitReadings(int parentId, int id) {
		log.info("UnitServices.getUnitReadings(parentId, id)");
		return UnitDAL.getUnitReadings(parentId, id);
	}

	public List<UnitReading> getUnitReadings(int parentId, String serialNo) {
		log.info("UnitServices.getUnitReadings(parentId, id)");
		return UnitDAL.getUnitReadings(parentId, serialNo);
	}

	public List<UnitReading> getUnitReadings(int parentId, String serialNo, int limit) {
		log.info("UnitServices.getUnitReadings(parentId, id, limit)");
		return UnitDAL.getUnitReadings(parentId, serialNo, limit);
	}

}
