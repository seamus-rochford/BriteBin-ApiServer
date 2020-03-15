package com.trandonsystems.britebin.database;

import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jasypt.util.password.*;

import com.trandonsystems.britebin.model.User;

public class UserDAL {

	static Logger log = Logger.getLogger(UserDAL.class);
	static ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();

	public UserDAL() {
		log.trace("Constructor");

		// Configure password Encrypter
		passwordEncryptor.setAlgorithm("SHA-1");
		passwordEncryptor.setPlainDigest(true);
	}

	public static User getBySQL(int id) {

		log.info("UserDAL.getBySQL");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String sql = "SELECT * FROM user WHERE id = " + id;
		log.info("SQL: " + sql);
		
		User user = new User();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			user.id = id;
			if (rs.next()) {
				user.email = rs.getString("email");
				user.password = rs.getString("password");
				user.role = rs.getInt("role");
				user.parentId = rs.getInt("parentId");
				user.status = rs.getInt("status");
				user.locale = rs.getString("locale");
				user.name = rs.getString("name");
				user.addr1 = rs.getString("addr1");
				user.addr2 = rs.getString("addr2");
				user.city = rs.getString("city");
				user.county = rs.getString("county");
				user.postcode = rs.getString("postcode");
				user.country = rs.getInt("country");
				user.mobile = rs.getString("mobile");
				user.homeTel = rs.getString("homeTel");
				user.workTel = rs.getString("workTel");

				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastLoggedIn = rs.getTimestamp("last_logged_in");
				if (lastLoggedIn == null) {
					user.lastLoggedIn = null;
				}
				else {
					java.time.Instant lastLoggedInInstant = lastLoggedIn.toInstant();
					user.lastLoggedIn = lastLoggedInInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("last_activity");
				if (lastActivity == null) {
					user.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					user.lastActivity = lastActivityInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					user.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					user.insertDate = insertDateInstant;
				}
				user.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					user.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					user.modifiedDate = modifiedDateInstant;
				}
				user.modifiedBy = rs.getInt("modifiedBy");

			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}

		return user;
	}

	public static User get(int id) {

		log.info("UserDAL.get(id)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUserById(?) }";
		log.info("SP Call: " + spCall);
		
		User user = new User();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			ResultSet rs = spStmt.executeQuery();

			user.id = id;
			if (rs.next()) {
				user.email = rs.getString("email");
				user.password = rs.getString("password");
				user.role = rs.getInt("role");
				user.parentId = rs.getInt("parentId");
				user.status = rs.getInt("status");
				user.locale = rs.getString("locale");
				user.name = rs.getString("name");
				user.addr1 = rs.getString("addr1");
				user.addr2 = rs.getString("addr2");
				user.city = rs.getString("city");
				user.county = rs.getString("county");
				user.postcode = rs.getString("postcode");
				user.country = rs.getInt("country");
				user.mobile = rs.getString("mobile");
				user.homeTel = rs.getString("homeTel");
				user.workTel = rs.getString("workTel");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastLoggedIn = rs.getTimestamp("lastLoggedIn");
				if (lastLoggedIn == null) {
					user.lastLoggedIn = null;
				}
				else {
					java.time.Instant lastLoggedInInstant = lastLoggedIn.toInstant();
					user.lastLoggedIn = lastLoggedInInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("lastActivity");
				if (lastActivity == null) {
					user.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					user.lastActivity = lastActivityInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					user.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					user.insertDate = insertDateInstant;
				}
				user.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					user.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					user.modifiedDate = modifiedDateInstant;
				}
				user.modifiedBy = rs.getInt("modifiedBy");

			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}

		return user;
	}

	public static User get(String email) {

		log.info("UserDAL.get(email)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUser(?) }";
		log.info("SP Call: " + spCall);

		User user = new User();
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, email);
			ResultSet rs = spStmt.executeQuery();

			user.email = email;
			if (rs.next()) {
				user.id = rs.getInt("id");
				user.password = rs.getString("password");
				user.role = rs.getInt("role");
				user.parentId = rs.getInt("parentId");
				user.status = rs.getInt("status");
				user.locale = rs.getString("locale");
				user.name = rs.getString("name");
				user.addr1 = rs.getString("addr1");
				user.addr2 = rs.getString("addr2");
				user.city = rs.getString("city");
				user.county = rs.getString("county");
				user.postcode = rs.getString("postcode");
				user.country = rs.getInt("country");
				user.mobile = rs.getString("mobile");
				user.homeTel = rs.getString("homeTel");
				user.workTel = rs.getString("workTel");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastLoggedIn = rs.getTimestamp("lastLoggedIn");
				if (lastLoggedIn == null) {
					user.lastLoggedIn = null;
				}
				else {
					java.time.Instant lastLoggedInInstant = lastLoggedIn.toInstant();
					user.lastLoggedIn = lastLoggedInInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("lastActivity");
				if (lastActivity == null) {
					user.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					user.lastActivity = lastActivityInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					user.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					user.insertDate = insertDateInstant;
				}
				user.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					user.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					user.modifiedDate = modifiedDateInstant;
				}
				user.modifiedBy = rs.getInt("modifiedBy");

			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}

		return user;
	}

	public static List<User> getUsers(int parentId) {

		log.info("UserDAL.getUsers");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<User> users = new ArrayList<User>();

		String spCall = "{ call GetUsers() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				User user = new User();

				int userId = rs.getInt("id");

				user.id = userId;
				user.email = rs.getString("email");
				user.password = rs.getString("password");
				user.role = rs.getInt("role");
				user.parentId = rs.getInt("parentId");
				user.status = rs.getInt("status");
				user.locale = rs.getString("locale");
				user.name = rs.getString("name");
				user.addr1 = rs.getString("addr1");
				user.addr2 = rs.getString("addr2");
				user.city = rs.getString("city");
				user.county = rs.getString("county");
				user.postcode = rs.getString("postcode");
				user.country = rs.getInt("country");
				user.mobile = rs.getString("mobile");
				user.homeTel = rs.getString("homeTel");
				user.workTel = rs.getString("workTel");
				  
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastLoggedIn = rs.getTimestamp("lastLoggedIn");
				if (lastLoggedIn == null) {
					user.lastLoggedIn = null;
				}
				else {
					java.time.Instant lastLoggedInInstant = lastLoggedIn.toInstant();
					user.lastLoggedIn = lastLoggedInInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("lastActivity");
				if (lastActivity == null) {
					user.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					user.lastActivity = lastActivityInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					user.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					user.insertDate = insertDateInstant;
				}
				user.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					user.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					user.modifiedDate = modifiedDateInstant;
				}
				user.modifiedBy = rs.getInt("modifiedBy");


				users.add(user);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return users;
	}

//	public static String encryptedPassword(String password) {
//		log.info("UserDAL.encryptedPassword");
//		return passwordEncryptor.encryptPassword(password);
//	}
//	public static boolean passwordMatch(String inputPassword, String encryptedPassword) {
//		log.info("UserDAL.passwordMatch");
//		return passwordEncryptor.checkPassword(inputPassword, encryptedPassword);
//	}
	
	public static String encryptPassword(String password) {
		log.info("UserDAL.encryptPassword");
		return Util.MD5(password);
	}
	
	public static boolean passwordMatch(String inputPassword, String encryptedPassword) {
		log.info("UserDAL.passwordMatch");
		log.info("inputPassword: " + inputPassword);
		log.info("inputPassword.md5: " + Util.MD5(inputPassword));
		log.info("DB Password: " + encryptedPassword);
		return (Util.MD5(inputPassword).equals(encryptedPassword));
	}
	
	// Update user - NOTE: Pwd is NOT updated
	public static User update(User user, int currentUserId) throws Exception {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		log.info("UserDAL.save");
		int userId = user.id;

		String sqlStmt = "UPDATE user SET fname = ?, lname = ?, tel = ?, email = ?, role_id = ?, status = ?, locale = ?, operator_id = ?, bank_id = ?, modified_date = ?, modified_by = ?"
					+ " WHERE id = ?";

		log.debug("SQL: " + sqlStmt);
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password)) {

			try (PreparedStatement prepStmt = conn.prepareStatement(sqlStmt, Statement.RETURN_GENERATED_KEYS)) {
				conn.setAutoCommit(false);

				log.debug("Set name: " + user.name);
				prepStmt.setString(1, user.name);
								
				log.debug("Set Email: " + user.email);
				prepStmt.setString(4, user.email);
				
				log.debug("Set Role: " + user.role);
				prepStmt.setInt(5, user.role);
				
				log.debug("Set Status: " + user.status);
				prepStmt.setInt(6, user.status);
				
				log.debug("Set Locale: " + user.locale);
				prepStmt.setString(6, user.locale);
				
				Instant currentInstant = Instant.now().truncatedTo(ChronoUnit.SECONDS); //gives UTC datetime
				user.modifiedDate = currentInstant;
				prepStmt.setTimestamp(9, Timestamp.from(currentInstant));
				
				// Modified By
				user.modifiedBy = currentUserId;
				prepStmt.setInt(10, currentUserId);
				
				prepStmt.setInt(11,  user.id);
				
				log.debug("Execute SQL");
				log.debug("SQL: " + prepStmt.toString());
				
				int affectedRows = prepStmt.executeUpdate();

				log.debug("SQL executed - affectedRows: " + affectedRows + "        UserId: " + userId);
				
				// If successful insert
				if (affectedRows > 0 && userId == 0) {
					ResultSet generatedKeys = prepStmt.getGeneratedKeys();
					if (generatedKeys.next()) {
						userId = generatedKeys.getInt(1);
						user.id = userId;
					}
				}
				
				conn.commit();

				log.info("User update complete ... user id: " + user.id);

				return user;
			} catch (Exception e) {
				log.error(e.getMessage());

				conn.rollback();
				throw e;
			}
		} catch (Exception e) {
			log.error(e.getMessage());

			throw e;
		}		
	}

	public static User updateStatus(User user, int status, int modifiedByUserId) throws Exception {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}
		
		log.info("UserDAL.updateStatus");

		String sqlStmt = "UPDATE user SET status = ?, modified_date = ?, modified_by = ? WHERE id = ?";
		log.debug("SQL: " + sqlStmt);
		
		try (Connection conn = DriverManager.getConnection(Util.connUrl, Util.username, Util.password)) {

			try (PreparedStatement prepStmt = conn.prepareStatement(sqlStmt, Statement.RETURN_GENERATED_KEYS)) {
				conn.setAutoCommit(false);

				log.debug("Status: " + status);
				user.status = status;
				prepStmt.setInt(1, status);

				Instant currentInstant = Instant.now().truncatedTo(ChronoUnit.SECONDS); //gives UTC datetime
				user.modifiedDate = currentInstant;
				prepStmt.setTimestamp(2, Timestamp.from(currentInstant));
				
				log.debug("ModifiedBy: " + modifiedByUserId);
				user.modifiedBy = modifiedByUserId;
				prepStmt.setInt(3,  modifiedByUserId);
				
				prepStmt.setInt(4, user.id);
				
				log.debug("SQL: " + prepStmt.toString());
				
				int affectedRows = prepStmt.executeUpdate();

				log.debug("SQL executed - affectedRows: " + affectedRows + "        UserId: " + user.id);
				
				conn.commit();

				log.info("Update Status complete ... user id: " + user.id);

				return user;
			} catch (Exception e) {
				log.error(e.getMessage());

				conn.rollback();
				throw e;
			}
		} catch (Exception e) {
			log.error(e.getMessage());

			throw e;
		}
		
	}
	
	public static void updateLastLoggedIn(String email) {
		// Implement this
	}
}
