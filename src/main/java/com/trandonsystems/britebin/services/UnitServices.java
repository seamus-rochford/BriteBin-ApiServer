package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;

public class UnitServices {

	static Logger log = Logger.getLogger(UnitServices.class);

	public Unit getUnit(int userFilterId, int id) {
		log.info("UnitServices.getUnit(userFilterId, id)");
		return UnitDAL.getUnit(userFilterId, id);
	}

	public Unit getUnit(int userFilterId, String serialNo) {
		log.info("UnitServices.getUnit(userFilterId, serialNo)");
		return UnitDAL.getUnit(userFilterId, serialNo);
	}

	public List<Unit> getUnits(int userFilterId) {
		log.info("UnitServices.getUnits(userFilterId)");
		return UnitDAL.getUnits(userFilterId);
	}

	public Unit save(Unit unit, int actionUserId) throws SQLException {
		log.info("UnitService.save");
		
		return UnitDAL.save(unit, actionUserId);
	}
	
	public List<UnitReading> getUnitReadings(int userFilterId, int id, int limit) {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.getUnitReadings(userFilterId, id, limit);
	}

	public List<UnitReading> getUnitReadings(int userFilterId, String serialNo, int limit) {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.getUnitReadings(userFilterId, serialNo, limit);
	}

	public List<UnitReading> pullReadings(int userFilterId, int unitId, String serialNo) throws SQLException {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.pullReadings(userFilterId, unitId, serialNo);
	}

	// for engineering testing only
	public List<UnitReading> getUnitReadingsTest(String serialNo, int limit) {
		log.info("UnitServices.getUnitReadings(userFilterId, id, limit)");
		return UnitDAL.getUnitReadingsTest(serialNo, limit);
	}

	public List<UnitReading> getLatestReadings(int userFilterId) {
		log.info("UnitServices.getLatestReadings(userFilterId)");
		return UnitDAL.getLatestReadings(userFilterId);
	}

	public void saveMessage(int unitId, byte[] data, int userId) throws SQLException {
		UnitDAL.saveMessage(unitId, data, userId);
	}
}
