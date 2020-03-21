package com.trandonsystems.britebin.resources;

import java.util.List;

import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.auth.JsonWebToken;
import com.trandonsystems.britebin.model.User;
import com.trandonsystems.britebin.services.UserServices;

import org.apache.log4j.Logger;

@Path("user")
public class UserResources {

	static Logger log = Logger.getLogger(UserResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	UserServices userServices = new UserServices();
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
		log.debug("User resource is working!");
        return "User resource is working!";
    }
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUser")
	@JWTTokenNeeded
	public Response getUser(@Context UriInfo ui, @Context HttpHeaders hh) {
		log.debug("getUser");
		
		try {
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			User user = new User();
			if (queryParams.containsKey("email")) {
				String email = queryParams.getFirst("email");
				log.debug("email: " + email);
				
				user = userServices.getUser(userFilterId, email);
			} else if (queryParams.containsKey("userId")) {
				int id = Integer.parseInt(queryParams.getFirst("userId"));
				log.debug("id: " + id);
								
				user = userServices.getUser(userFilterId, id);
			} else {
				log.debug("invalid parameter key");
				user = null;
			}
			
			// Get a new token
			String newToken = JsonWebToken.verify(jwtToken);		
			
			String json = Json.createObjectBuilder()
					.add("token", newToken)
					.add("user", gson.toJson(user))
					.build()
					.toString();
			
			return Response.status(Response.Status.OK) // 200 
				.entity(json)
				.build();
		
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}	
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUsers")
	@JWTTokenNeeded
	public Response getUsers(@Context UriInfo ui, @Context HttpHeaders hh) {
		log.debug("getUsers");
		try {
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
	
			List<User>users = userServices.getUsers(userFilterId);
			
			// Get a new token
			String newToken = JsonWebToken.verify(jwtToken);
			
			String usersJson = gson.toJson(users);
			String json = Json.createObjectBuilder()
					.add("token", newToken)
					.add("users", usersJson)
					.build()
					.toString();
			
			return Response.status(Response.Status.OK) // 200 
				.entity(json)
				.build();
			
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}
	}
		
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("login")
	public Response login(User user) {
		try {
			log.info("POST: Login user");
			log.info("User: " + user.email);
			
			int errorCode = userServices.loginUser(user);
			log.info("ErrorCode: " + errorCode);

			switch (errorCode) {
			case -1:
				return Response.status(Response.Status.UNAUTHORIZED) // 401 
						.entity("Invalid email")
						.build();
			case -2:
				return Response.status(Response.Status.UNAUTHORIZED) // 401 
						.entity("Invalid password")
						.build();
			default:
				if (user.status == User.USER_STATUS_ACTIVE) {
					String token = JsonWebToken.createJWT(user);
					// Clear the password so that it is NOT sent back
					user.password = null;
					
					String json = Json.createObjectBuilder()
											.add("token", token)
											.add("user", gson.toJson(user))
											.build()
											.toString();
					return Response.status(Response.Status.OK) // 200 
							.entity(json)
							.build();
				} else if (user.status == User.USER_STATUS_REGISTERED) {
					return Response.status(Response.Status.UNAUTHORIZED) // 401 
							.entity("400 - User registered but must change password")
							.build();
				}
				// User is inactive
				return Response.status(Response.Status.UNAUTHORIZED) // 401 
						.entity("Unauthorized")
						.build();
			}
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("resetPassword")
	public Response resetPassowrd(User user) {
		try {
			log.info("POST: Login user");
			log.info("User: " + user.email);
			
			// Save the password because it will be rest when we call loginUser
			String newPassword = user.newPassword;
			
			int errorCode = userServices.loginUser(user);
			log.info("ErrorCode: " + errorCode);

			switch (errorCode) {
			case -1:
				return Response.status(Response.Status.UNAUTHORIZED) // 401 
						.entity("Invalid email")
						.build();
			case -2:
				return Response.status(Response.Status.UNAUTHORIZED) // 401 
						.entity("Invalid password")
						.build();
			default:
				if (user.status == User.USER_STATUS_ACTIVE || user.status == User.USER_STATUS_REGISTERED) {
					
					user.newPassword = newPassword;
					
					userServices.resetPassword(user);
					
					String token = JsonWebToken.createJWT(user);

					// Clear the password so that it is NOT sent back
					user.password = null;
					user.newPassword = null;
					
					String json = Json.createObjectBuilder()
											.add("token", token)
											.add("user", gson.toJson(user))
											.build()
											.toString();
					return Response.status(Response.Status.OK) // 200 
							.entity(json)
							.build();
				}
				
				// User is inactive
				return Response.status(Response.Status.UNAUTHORIZED) // 401 
						.entity("Unauthorized")
						.build();
			}
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("deactivateUser")
	@JWTTokenNeeded
	public Response deactivateUser(@Context UriInfo ui, @Context HttpHeaders hh) {
		try {
			log.info("deactivateUser(user)");
			
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);
	
			if (queryParams.containsKey("email")) {
				String email = queryParams.getFirst("email");
				log.debug("email: " + email);
				
				userServices.setUserStatus(email, User.USER_STATUS_INACTIVE, actionUserId);
				
				return Response.status(Response.Status.OK) // 200 
						.entity("Success")
						.build();
			} else if (queryParams.containsKey("userId")) {
				int userId = Integer.parseInt(queryParams.getFirst("userId"));
				log.debug("userId: " + userId);
								
				userServices.setUserStatus(userId, User.USER_STATUS_INACTIVE, actionUserId);
				
				return Response.status(Response.Status.OK) // 200 
						.entity("Success")
						.build();
			} 
			
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("User not identified - please send userId or email of user")
					.build();
			
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("activateUser")
	@JWTTokenNeeded
	public Response activateUser(@Context UriInfo ui, @Context HttpHeaders hh) {
		try {
			log.info("activateUser(user)");
			
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);

			if (queryParams.containsKey("email")) {
				String email = queryParams.getFirst("email");
				log.debug("email: " + email);
				
				userServices.setUserStatus(email, User.USER_STATUS_ACTIVE, actionUserId);
				
				return Response.status(Response.Status.OK) // 200 
						.entity("Success")
						.build();
			} else if (queryParams.containsKey("userId")) {
				int userId = Integer.parseInt(queryParams.getFirst("userId"));
				log.debug("userId: " + userId);
								
				userServices.setUserStatus(userId, User.USER_STATUS_ACTIVE, actionUserId);
				
				return Response.status(Response.Status.OK) // 200 
						.entity("Success")
						.build();
			} 
			
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("User not identified - please send id or email of user")
					.build();
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

	
}
