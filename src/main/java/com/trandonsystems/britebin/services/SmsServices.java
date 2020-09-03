package com.trandonsystems.britebin.services;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.database.SmsDAL;
import com.trandonsystems.britebin.model.sms.SmsDeliveryReport;

public class SmsServices {

	static Logger log = Logger.getLogger(SmsServices.class);

	public void processSmsDeliveryReport(SmsDeliveryReport report) throws SQLException {
		
		SmsDAL.saveSmsDeliveryReport(report);
	}
}
