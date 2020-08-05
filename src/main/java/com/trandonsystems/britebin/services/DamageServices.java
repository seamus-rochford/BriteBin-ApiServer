package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.DamageDAL;
import com.trandonsystems.britebin.model.Damage;


public class DamageServices {

	static Logger log = Logger.getLogger(DamageServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	
	public Damage getDamage(int damageId, int userFilterId) throws SQLException {
		log.info("DamageServices.getDamage(damageId, userFilterId)");
		return DamageDAL.getDamage(damageId, userFilterId);
	}
	
	public byte[] getDamageHistoryImage(int damageHistoryId) throws SQLException {
		log.info("DamageServices.getDamage(damageId, userFilterId)");
		return DamageDAL.getDamageHistoryImage(damageHistoryId);
	}

	public List<Damage> getDamages(int damageStatusId, Instant fromDate, Instant toDate, int userFilterId) throws SQLException {
		log.info("DamageServices.getDamages(damageStatusId, fromDate, toDate, userFilterId)");
		return DamageDAL.getDamages(damageStatusId, fromDate, toDate, userFilterId);
	}
	
	public List<Damage> getAssignedDamages(int assignedUserId, Instant fromDate, Instant toDate, int userFilterId) throws SQLException {
		log.info("DamageServices.getAssignedDamages(userFilterId)");
		return DamageDAL.getAssignedDamages(assignedUserId, fromDate, toDate, userFilterId);
	}
	
	public Damage report(int damageType, int unitId, String comment, int actionUserId, boolean imageIncluded, String imageFileName) throws Exception {
		log.info("DamageServices.report()");
		
		if (imageIncluded) {
			return DamageDAL.report(damageType, unitId, comment, actionUserId, imageFileName);
		}
		return DamageDAL.report(damageType, unitId, comment, actionUserId);
	}

	public Damage assign(int damageId, int assignedUserId, String comment, int actionUserId) throws SQLException {
		log.info("DamageServices.assign()");
		
		return DamageDAL.assign(damageId, assignedUserId, comment, actionUserId);
	}

	public Damage close(int damageId, String comment, int actionUserId) throws SQLException {
		log.info("DamageServices.close()");
		
		return DamageDAL.close(damageId, comment, actionUserId);
	}


}
