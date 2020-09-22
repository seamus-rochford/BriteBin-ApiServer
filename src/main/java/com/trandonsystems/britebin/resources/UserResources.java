package com.trandonsystems.britebin.resources;

import java.util.List;

import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
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
	public Response getUser(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		log.debug("getUser");
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
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
			
			return Response.status(Response.Status.OK) // 200 
				.entity(user)
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
	public Response getUsers(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		log.debug("getUsers");
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
	
			Boolean includeDeactive = false;
			if (queryParams.containsKey("includeDeactive")) {
				includeDeactive = queryParams.getFirst("includeDeactive").equalsIgnoreCase("true");
				log.debug("Include Deactive: " + includeDeactive);
			}
			
			List<User>users = userServices.getUsers(userFilterId, includeDeactive);
			
			log.debug("No. Users: " + users.size());
			return Response.status(Response.Status.OK) // 200 
				.entity(users)
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
	@Path("getPossibleParents")
	@JWTTokenNeeded
	public Response getPossibleParents(@Context HttpHeaders httpHeader) {
		log.debug("getPossibleParents");
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
	
			List<User>users = userServices.getPossibleParents(userFilterId);
			
			log.debug("No. Users: " + users.size());
			return Response.status(Response.Status.OK) // 200 
				.entity(users)
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
	@Path("getPossibleBinParents")
	@JWTTokenNeeded
	public Response getPossibleBinParents(@Context HttpHeaders httpHeader) {
		log.debug("getPossibleBinParents");
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
	
			List<User>users = userServices.getPossibleBinParents(userFilterId);
			
			log.debug("No. Users: " + users.size());
			return Response.status(Response.Status.OK) // 200 
				.entity(users)
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
	public Response login(@Context HttpServletRequest request, User user) {
		try {
			log.info("POST: Login user");
			log.info("User: " + user.email);
			log.info("GCM Token: " + user.gcmToken);
			
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
				String token = JsonWebToken.createJWT(user);
				// Clear the password so that it is NOT sent back
				user.password = null;
				
				if (user.status.id == User.USER_STATUS_ACTIVE) {
					
					String json = Json.createObjectBuilder()
											.add("token", token)
											.add("user", gson.toJson(user))
											.build()
											.toString();

					return Response.status(Response.Status.OK) // 200 
							.entity(json)
							.build();
					
				} else if (user.status.id == User.USER_STATUS_REGISTERED) {
					
					String json = Json.createObjectBuilder()
											.add("token", token)
											.add("user", gson.toJson(user))
											.add("message", "User registered but must change password")
											.build()
											.toString();

					return Response.status(Response.Status.CREATED) // 201 
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("verifyToken")
	@JWTTokenNeeded
	public Response verifyToken(@Context HttpServletRequest request, @Context HttpHeaders httpHeader) {
		try {
			log.info("POST: verifyToken");

			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			if (authorization == null) {
				return Response.status(Response.Status.UNAUTHORIZED)
								.entity("Authorization token missing")
								.build();
			}
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("Token verified");
				
//			HttpSession session = request.getSession(false);
//			
//			log.info("Session Object: ");
//			log.info(gson.toJson(session));

			// Get new jwtToken
			String newToken = JsonWebToken.verify(jwtToken);
			String json = Json.createObjectBuilder()
					.add("token", newToken)
					.build()
					.toString();
			
			return Response.status(Response.Status.OK) // 200 
					.entity(json)
					.build();

		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

	private String SuccessJson() {
		String json = Json.createObjectBuilder()
				.add("Success", true)
				.build()
				.toString();
		return json;
	}
	
	@POST
	@Path("deactivateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response deactivateUser(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		try {
			log.info("deactivateUser(user)");
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
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
						.entity(SuccessJson())
						.build();
			} else if (queryParams.containsKey("userId")) {
				int userId = Integer.parseInt(queryParams.getFirst("userId"));
				log.debug("userId: " + userId);
								
				userServices.setUserStatus(userId, User.USER_STATUS_INACTIVE, actionUserId);
				
				return Response.status(Response.Status.OK) // 200 
						.entity(SuccessJson())
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
	@Path("activateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response activateUser(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		try {
			log.info("activateUser(user)");
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);

			if (queryParams.containsKey("email")) {
				String email = queryParams.getFirst("email");
				log.debug("email: " + email);
				
				userServices.setUserStatus(email, User.USER_STATUS_REGISTERED, actionUserId);
				
				return Response.status(Response.Status.OK) // 200 
						.entity(SuccessJson())
						.build();
			} else if (queryParams.containsKey("userId")) {
				int userId = Integer.parseInt(queryParams.getFirst("userId"));
				log.debug("userId: " + userId);
								
				userServices.setUserStatus(userId, User.USER_STATUS_REGISTERED, actionUserId);
				
				return Response.status(Response.Status.OK) // 200 
						.entity(SuccessJson())
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

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("save")
	@JWTTokenNeeded
	public Response save(@Context HttpHeaders httpHeader, User user) {
		try {
			log.info("POST: save user");
			log.info("User: " + gson.toJson(user));
			
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);

			user = userServices.save(user, actionUserId);
			log.info("Saved user: " + gson.toJson(user));

			user.password = null;
			
			String json = Json.createObjectBuilder()
									.add("user", gson.toJson(user))
									.build()
									.toString();
			
			return Response.status(Response.Status.OK) // 200 
					.entity(json)
					.build();
		
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

	@GET
	@Path("changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response changePassword(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		// This is called by the user themselves
		try {
			log.info("changePassword");
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Normally I would be getting the userFilterId but because it could be a technician or a driver logging in - we want the userId here
			int actionUserId = userServices.getUserIdFromJwtToken(jwtToken);
			log.debug("actionUserId: " + actionUserId);
	
			User user = new User();
			if (queryParams.containsKey("userId")) {
				user.id = Integer.parseInt(queryParams.getFirst("userId"));
				
				if (user.id != actionUserId) {
					// This routine can only be called by the user themselves
					throw new Exception("This resource can only be called by the user themselves");
				}
			} else {
				throw new Exception("No user Id supplied");
			}
			if (queryParams.containsKey("oldPassword")) {
				user.password = queryParams.getFirst("oldPassword");
			} else {
				throw new Exception("No 'OLD' password supplied");
			}				
			if (queryParams.containsKey("newPassword")) {
				user.newPassword = queryParams.getFirst("newPassword");
			} else {
				throw new Exception("No 'NEW' password supplied");
			}
				
			if (queryParams.containsKey("confirmPassword")) {
				String confirmPassword = queryParams.getFirst("confirmPassword");
				
				if (!user.newPassword.equals(confirmPassword)) {
					throw new Exception("'NEW' password and 'CONFIRM' password do not match");
				}
			} else {
				throw new Exception("No 'CONFIRM' password supplied");
			}
			
			if (user.newPassword.equals(user.password)) {
				throw new Exception("New password can NOT be the same as the old password");
			}
			
			log.debug("Passwords match");
			userServices.resetPassword(user);
				
			return Response.status(Response.Status.OK) // 200 
					.entity(SuccessJson())
					.build();
			
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

	@GET
	@Path("resetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JWTTokenNeeded
	public Response resetPassword(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		// This is called by an admin user
		try {
			log.info("resetPassword");
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);
	
			User user = new User();
			if (queryParams.containsKey("userId")) {
				user.id = Integer.parseInt(queryParams.getFirst("userId"));
			} else {
				throw new Exception("No user Id supplied");
			}			
			if (queryParams.containsKey("newPassword")) {
				user.newPassword = queryParams.getFirst("newPassword");
			} else {
				throw new Exception("No 'NEW' password supplied");
			}
				
			if (queryParams.containsKey("confirmPassword")) {
				String confirmPassword = queryParams.getFirst("confirmPassword");
				
				if (!user.newPassword.equals(confirmPassword)) {
					throw new Exception("'NEW' password and 'CONFIRM' password do not match");
				}
			} else {
				throw new Exception("No 'CONFIRM' password supplied");
			}
				
			log.debug("Passwords match");
			userServices.resetPassword(user, actionUserId);
				
			return Response.status(Response.Status.OK) // 200 
					.entity(SuccessJson())
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
