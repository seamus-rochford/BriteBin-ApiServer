package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.sms.SmsDeliveryReport;


public class SmsDAL {

	static Logger log = Logger.getLogger(AlertDAL.class);

	public static void saveSmsDeliveryReport(SmsDeliveryReport smsDeliveryReport) throws SQLException {
		
		log.info("SmsDAL.saveSmsDeliveryReport()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call SaveSMSDlr(?, ?, ?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, smsDeliveryReport.messageId);
			spStmt.setString(2, smsDeliveryReport.transactionId);
			spStmt.setString(3, smsDeliveryReport.channel);
			spStmt.setString(4, smsDeliveryReport.time);
			spStmt.setInt(5, smsDeliveryReport.status.code);
			spStmt.setString(6, smsDeliveryReport.status.details);
			spStmt.executeUpdate();

			return;

		} catch (SQLException ex) {
			log.error("ERROR - saveSmsDeliveryReport: " + ex.getMessage());
			throw ex;
		}			
	}		
}
