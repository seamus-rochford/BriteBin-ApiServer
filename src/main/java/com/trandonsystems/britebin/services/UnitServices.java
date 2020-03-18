package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;

public class UnitServices {

	static Logger log = Logger.getLogger(UnitServices.class);

	public Unit getUnit(int parentId, int id) {
		log.info("UnitServices.getUnit(parentId, id)");
		return UnitDAL.getUnit(parentId, id);
	}

	public Unit getUnit(int parentId, String serialNo) {
		log.info("UnitServices.getUnit(parentId, serialNo)");
		return UnitDAL.getUnit(parentId, serialNo);
	}

	public List<Unit> getUnits(int parentId) {
		log.info("UnitServices.getUnits(parentId)");
		return UnitDAL.getUnits(parentId);
	}

	public List<UnitReading> getUnitReadings(int parentId, int id, int limit) {
		log.info("UnitServices.getUnitReadings(parentId, id, limit)");
		return UnitDAL.getUnitReadings(parentId, id, limit);
	}

	public List<UnitReading> getUnitReadings(int parentId, String serialNo, int limit) {
		log.info("UnitServices.getUnitReadings(parentId, id, limit)");
		return UnitDAL.getUnitReadings(parentId, serialNo, limit);
	}

	public void saveMessage(int unitId, byte[] data, int userId) throws SQLException {
		UnitDAL.saveMessage(unitId, data, userId);
	}
}
