package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.AlertDAL;
import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.User;

public class AlertServices {

	static Logger log = Logger.getLogger(AlertServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public List<Alert> getAdminAlerts(int customerId) {
		log.info("AlertServices.getAdminAlerts(customerId)");
		return AlertDAL.getAlertsAdmin(customerId);
	}
	
	public List<Alert> getDistributorAlerts(int customerId) {
		log.info("AlertServices.getDistributorAlerts(customerId)");
		return AlertDAL.getAlertsDistributors(customerId);
	}
	
	public List<Alert> getAlertsTechnicians(int customerId, int userRole) {
		log.info("AlertServices.getAlertsTechnicians(customerId)");
		
		List<Alert> alerts = new ArrayList<Alert>();
		if (userRole == User.USER_ROLE_ADMIN) {
			alerts = AlertDAL.getAlertsAdminTechnicians(customerId);
			
			List<Alert> distAlerts = AlertDAL.getAlertsDistributorTechnicians(customerId);
			
			for(int i = 0; i < distAlerts.size(); i++) {
				alerts.add(distAlerts.get(i));
			}
		} else {
			alerts = AlertDAL.getAlertsDistributorTechnicians(customerId);
		}
		return alerts;
	}
	
	public List<Alert> getAlertsCorporate(int customerId) {
		log.info("AlertServices.getAlertsCorporate(customerId)");
		return AlertDAL.getAlertsCorporate(customerId);
	}
	
	public List<Alert> getAlertsCustomer(int customerId) {
		log.info("AlertServices.getAlertsCustomer(customerId)");
		return AlertDAL.getAlertsCustomer(customerId);
	}
	
	public List<Alert> getAlertsDrivers(int customerId) {
		log.info("AlertServices.getAlertsDrivers(customerId)");
		
		List<Alert> alerts = AlertDAL.getAlertsCorporateDrivers(customerId);
		List<Alert> custDriverAlerts = AlertDAL.getAlertsCustomerDrivers(customerId);
		
		// Concatenate the 2 lists
		for(int i = 0; i < custDriverAlerts.size(); i++) {
			alerts.add(custDriverAlerts.get(i));
		}		
		return alerts;
	}

	public List<Alert> saveAlerts(List<Alert> alerts, int actionUserId) throws SQLException {
		log.info("AlertService.saveAlerts");
		
		return AlertDAL.saveAlerts(alerts, actionUserId);
	}
	
}
