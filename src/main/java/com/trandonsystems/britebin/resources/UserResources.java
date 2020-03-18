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
        return "User resource is working!";
    }
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUser")
	@JWTTokenNeeded
	public Response getUser(@Context UriInfo ui, @Context HttpHeaders hh) {
		
		try {
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			UserServices userServices = new UserServices();
			int userId = userServices.getUserIdFromJwtToken(jwtToken);
			
			User user = new User();
			if (queryParams.containsKey("email")) {
				String email = queryParams.getFirst("email");
				log.debug("email: " + email);
				
				user = userServices.getUser(userId, email);
			} else if (queryParams.containsKey("id")) {
				int id = Integer.parseInt(queryParams.getFirst("id"));
				log.debug("id: " + id);
								
				user = userServices.getUser(userId, id);
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
		try {
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			UserServices userServices = new UserServices();
			int userId = userServices.getUserIdFromJwtToken(jwtToken);
	
			List<User>users = userServices.getUsers(userId);
			
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
			}
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

    
}
