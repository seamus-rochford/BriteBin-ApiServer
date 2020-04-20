package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.ContentType;
import com.trandonsystems.britebin.model.BinLevel;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.Locale;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.model.Status;

public class SystemDAL {

	static Logger log = Logger.getLogger(SystemDAL.class);

	public static String getSystemVariableValue(String name) {
		
		log.info("SystemDAL.getSystemVariableValue(" + name + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String systemValue = "";

		String spCall = "{ call getSystemVariableValue(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, name);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				systemValue = rs.getString("value");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return systemValue;
	}

	public static List<BinType> getBinTypes(String locale) {
		
		log.info("LookupDAL.getBinTypes(" + locale + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<BinType> binTypes = new ArrayList<BinType>();

		String spCall = "{ call GetBinTypes(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				BinType binType = new BinType();

				binType.id = rs.getInt("id");
				binType.name = rs.getString("name");
				binType.emptyLevel = rs.getInt("emptyLevel");
				binType.fullLevel = rs.getInt("fullLevel");

				binTypes.add(binType);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return binTypes;
	}

	public static List<ContentType> getContentTypes(String locale) {
		
		log.info("LookupDAL.getBinTypes");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<ContentType> contentTypes = new ArrayList<ContentType>();

		String spCall = "{ call GetContentTypes(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				ContentType contentType = new ContentType();

				contentType.id = rs.getInt("id");
				contentType.name = rs.getString("name");

				contentTypes.add(contentType);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return contentTypes;
	}

	public static List<Country> getCountries(String locale) {
		
		log.info("LookupDAL.getCountries");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Country> countries = new ArrayList<Country>();

		String spCall = "{ call GetCountries(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Country country = new Country();

				country.id = rs.getInt("id");
				country.name = rs.getString("name");
				country.abbr = rs.getString("abbr");

				countries.add(country);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return countries;
	}

	public static List<DeviceType> getDeviceTypes(String locale) {
		
		log.info("LookupDAL.getDeviceTypes");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<DeviceType> deviceTypes = new ArrayList<DeviceType>();

		String spCall = "{ call GetDeviceTypes(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				DeviceType deviceType = new DeviceType();

				deviceType.id = rs.getInt("id");
				deviceType.name = rs.getString("name");

				deviceTypes.add(deviceType);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return deviceTypes;
	}

	public static List<Locale> getLocales(String translateLocale) {
		
		log.info("LookupDAL.getLocales");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Locale> locales = new ArrayList<Locale>();

		String spCall = "{ call GetLocales(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, translateLocale);
			ResultSet rs = spStmt.executeQuery();
			
			while (rs.next()) {
				Locale locale = new Locale();

				locale.abbr = rs.getString("abbr");
				locale.name = rs.getString("Name");

				locales.add(locale);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return locales;
	}

	
	public static List<Role> getRoles(String locale) {
		
		log.info("LookupDAL.getRoles");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Role> roles = new ArrayList<Role>();

		String spCall = "{ call GetRoles(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Role role = new Role();

				role.id = rs.getInt("id");
				role.name = rs.getString("Name");

				roles.add(role);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return roles;
	}

	public static List<Status> getStatus(String locale) {
		
		log.info("LookupDAL.getRoles");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Status> statusList = new ArrayList<Status>();

		String spCall = "{ call GetStatus(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Status status = new Status();

				status.id = rs.getInt("id");
				status.name = rs.getString("Name");

				statusList.add(status);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return statusList;
	}

}
