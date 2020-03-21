package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.BinContentType;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.Role;

public class LookupDAL {

	static Logger log = Logger.getLogger(LookupDAL.class);

	public static List<BinType> getBinTypes(String locale) {
		
		log.info("LookupDAL.getBinTypes");
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

				binTypes.add(binType);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return binTypes;
	}

	public static List<BinContentType> getBinContentTypes(String locale) {
		
		log.info("LookupDAL.getBinTypes");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<BinContentType> binContentTypes = new ArrayList<BinContentType>();

		String spCall = "{ call GetContentTypes(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				BinContentType binContentType = new BinContentType();

				binContentType.id = rs.getInt("id");
				binContentType.name = rs.getString("name");

				binContentTypes.add(binContentType);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return binContentTypes;
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

}
