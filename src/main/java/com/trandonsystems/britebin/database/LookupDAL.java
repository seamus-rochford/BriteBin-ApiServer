package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.ContentType;
import com.trandonsystems.britebin.model.BinLevel;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.DamageStatus;
import com.trandonsystems.britebin.model.DamageType;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.Locale;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UserStatus;

public class LookupDAL {

	static Logger log = Logger.getLogger(LookupDAL.class);

	public static List<BinLevel> getBinLevels(String locale) {
		
		log.info("LookupDAL.getBinLevels(" + locale + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<BinLevel> binLevels = new ArrayList<BinLevel>();

		String spCall = "{ call getBinLevels(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				BinLevel binLevel = new BinLevel();

				binLevel.id = rs.getInt("id");
				binLevel.name = rs.getString("name");

				binLevels.add(binLevel);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return binLevels;
	}

	public static List<BinType> getBinTypes(String locale) {
		
		log.info("LookupDAL.getBinTypes(" + locale + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
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
			Class.forName("com.mysql.cj.jdbc.Driver");
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
			Class.forName("com.mysql.cj.jdbc.Driver");
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
			Class.forName("com.mysql.cj.jdbc.Driver");
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
			Class.forName("com.mysql.cj.jdbc.Driver");
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
			Class.forName("com.mysql.cj.jdbc.Driver");
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

	public static List<UserStatus> getStatus(String locale) {
		
		log.info("LookupDAL.getStatus");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UserStatus> statusList = new ArrayList<UserStatus>();

		String spCall = "{ call GetStatus(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UserStatus status = new UserStatus();

				status.id = rs.getInt("id");
				status.name = rs.getString("Name");

				statusList.add(status);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return statusList;
	}

	public static List<DamageStatus> getDamageStatus(String locale) {
		
		log.info("LookupDAL.getDamageStatus");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<DamageStatus> damageStatusList = new ArrayList<DamageStatus>();

		String spCall = "{ call GetDamageStatus(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				DamageStatus damageStatus = new DamageStatus();

				damageStatus.id = rs.getInt("id");
				damageStatus.name = rs.getString("Name");

				damageStatusList.add(damageStatus);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return damageStatusList;
	}

	public static List<DamageType> getDamageTypes(String locale) {
		
		log.info("LookupDAL.getDamageTypes");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<DamageType> damageTypeList = new ArrayList<DamageType>();

		String spCall = "{ call GetDamageTypes(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				DamageType damageType = new DamageType();

				damageType.id = rs.getInt("id");
				damageType.name = rs.getString("Name");

				damageTypeList.add(damageType);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return damageTypeList;
	}

 	public static BinType save(BinType binType) throws SQLException {
		log.info("LookupDAL.save(binType)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		String spCall = "{ call SaveRefBinType(?, ?, ?, ?, ?) }";
		log.debug("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, binType.id);
			spStmt.setString(2, binType.name);
			spStmt.setInt(3, binType.emptyLevel);
			spStmt.setInt(4, binType.fullLevel);
			spStmt.setInt(5, binType.capacity);
		    
			spStmt.registerOutParameter(1, Types.BIGINT);
			
			spStmt.executeUpdate();
			
			binType.id = spStmt.getInt(1);
			
			log.debug("binTypeId: " + binType.id);
			
		} catch (SQLException ex) {
			log.error("UserDAL.save: " + ex.getMessage());
			throw ex;
		}
		
		log.info("LookupDAL.save(binType) - end");

		return binType;
		
	}

 	

}
