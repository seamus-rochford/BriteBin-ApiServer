package com.trandonsystems.britebin.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.UserDAL;
import com.trandonsystems.britebin.model.User;

public class UserServices {

	static Logger log = Logger.getLogger(UserServices.class);

	public List<User> getUsers(int parentId) {
		log.info("UserService.getUsers(parentId)");
		return UserDAL.getUsers(parentId);
	}

	public User getUser(int id) {
		log.info("UserService.getUser(int id)");
		return UserDAL.get(id);
	}

	public User getUser(String email) {
		log.info("UserService.getUser(String email)");
		return UserDAL.get(email);
	}

	public int loginUser(User user) {
		log.info("UserService.loginUser");
		
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			log.debug(gson.toJson(user));
			
			User dbUser = UserDAL.get(user.email);
			
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
			UserDAL.updateLastLoggedIn(user.email);
			
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
	
}
