package com.trandonsystems.britebin.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JsonWebToken;
import com.trandonsystems.britebin.database.UserDAL;
import com.trandonsystems.britebin.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class UserServices {

	static Logger log = Logger.getLogger(UserServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public List<User> getUsers(int parentId) {
		log.info("UserService.getUsers(parentId)");
		return UserDAL.getUsers(parentId);
	}

	public User getUser(int parentId, int id) {
		log.info("UserService.getUser(int id)");
		return UserDAL.get(parentId, id);
	}

	public User getUser(int parentId, String email) {
		log.info("UserService.getUser(parentId, email)");
		return UserDAL.get(parentId, email);
	}

	public int loginUser(User user) {
		log.info("UserService.loginUser");
		
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			log.debug(gson.toJson(user));
			
			User dbUser = UserDAL.get(1, user.email);
			
			if (dbUser.id == 0) {
				// User not found
				return -1;
			}
			else {
				try {
					if (!UserDAL.passwordMatch(user.password, dbUser.password)) {
						// Passwords do NOT match
						return -2;
					}
				} catch (Exception e) {
					// if password saved in DB is not an encrypted string then passwordMatch will throw NULL value exception
					return -2;
				}
			}
	
			// Update last loggedIn
			UserDAL.updateLastLoggedIn(dbUser.id);
			
			// Assign the dbUser fields to user (because that is the equivalent to pass by reference)
			user.id = dbUser.id;
			user.name = dbUser.name;
			user.email = dbUser.email;
			//user.password = dbUser.password;
			user.role = dbUser.role;
			user.parentId = dbUser.parentId;
			user.status = dbUser.status;
			user.locale = dbUser.locale;
			user.name = dbUser.name;
			user.addr1 = dbUser.addr1;
			user.addr2 = dbUser.addr2;
			user.city = dbUser.city;
			user.county = dbUser.county;
			user.postcode = dbUser.postcode;
			user.country = dbUser.country;
			user.mobile = dbUser.mobile;
			user.homeTel = dbUser.homeTel;
			user.workTel = dbUser.workTel;
			
			user.lastActivity = dbUser.lastActivity;
			user.lastLoggedIn = dbUser.lastLoggedIn;
			
	//		user.insertDate = dbUser.insertDate;
	//		user.insertBy = dbUser.insertBy;
	//		user.modifiedDate = dbUser.modifiedDate;
	//		user.modifiedBy = dbUser.modifiedBy;
				
			log.info("user: " + gson.toJson(user));
		}
		catch(Exception e) {
			log.error("ERROR: " + e.getMessage()); 
			return -3;
		}
		
		// valid user
		return 0;
	}
	
	public boolean verifyToken(int id, String jwtToken) {
		try {
			Claims jwtClaims = JsonWebToken.decodeJWT(jwtToken);
			
	        log.debug("Id: " + jwtClaims.getId());
	        log.debug("Sub: " + jwtClaims.getSubject());
	        log.debug("Iss: " + jwtClaims.getIssuer());
	        log.debug("At: " + jwtClaims.getIssuedAt());
	        log.debug("Exp: " + jwtClaims.getExpiration());
	        log.debug("Name: " + jwtClaims.get("name"));
	        log.debug("role: " + jwtClaims.get("role"));
	        log.debug("email: " + jwtClaims.get("email"));
	        log.debug("parent: " + jwtClaims.get("parent"));
	        log.debug("Status: " + jwtClaims.get("status"));
	        
	        log.debug("jwtClaims: " + jwtClaims);
		
			if (Integer.parseInt(jwtClaims.getId()) == id) {
				return true;
			}
		}
		catch (ExpiredJwtException e) {
			log.error("Token expired exception");
		}
		catch (UnsupportedJwtException e) {
			log.error("Token unsupported exception");
		}
		catch (MalformedJwtException e) {
			log.error("Token malformed exception");
		}
		catch (SignatureException e) {
			log.error("Token signature exception");
		}
		catch (IllegalArgumentException e) {
			log.error("Token illegal exception");
		}

		return false;
	}
	
	public int getUserIdFromJwtToken(String jwtToken) {
		try {
			Claims jwtClaims = JsonWebToken.decodeJWT(jwtToken);
			
	        log.debug("Id: " + jwtClaims.getId());
	        log.debug("Sub: " + jwtClaims.getSubject());
	        log.debug("Iss: " + jwtClaims.getIssuer());
	        log.debug("At: " + jwtClaims.getIssuedAt());
	        log.debug("Exp: " + jwtClaims.getExpiration());
	        log.debug("Name: " + jwtClaims.get("name"));
	        log.debug("role: " + jwtClaims.get("role"));
	        log.debug("email: " + jwtClaims.get("email"));
	        log.debug("parent: " + jwtClaims.get("parent"));
	        log.debug("Status: " + jwtClaims.get("status"));
	        
	        log.debug("jwtClaims: " + jwtClaims);
		
			return Integer.parseInt(jwtClaims.getId());
		}
		catch (ExpiredJwtException e) {
			log.error("Token expired exception");
		}
		catch (UnsupportedJwtException e) {
			log.error("Token unsupported exception");
		}
		catch (MalformedJwtException e) {
			log.error("Token malformed exception");
		}
		catch (SignatureException e) {
			log.error("Token signature exception");
		}
		catch (IllegalArgumentException e) {
			log.error("Token illegal exception");
		}

		return 0;
	}
}
