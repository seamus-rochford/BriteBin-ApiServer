package com.trandonsystems.britebin.resources;

import java.util.List;

import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JsonWebToken;
import com.trandonsystems.britebin.model.User;
import com.trandonsystems.britebin.services.UserServices;

import org.apache.log4j.Logger;

@Path("user")
public class UserResource {

	static Logger log = Logger.getLogger(UserResource.class);

	UserServices userServices = new UserServices();
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "User resource is working!";
    }
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUserById/{userId}")
	public User getUserById(@PathParam("userId") int userId) {
		return userServices.getUser(userId);
	}
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUser/{email}")
	public User getUser(@PathParam("email") String email) {
		return userServices.getUser(email);
	}
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUsers")
	public List<User> getUsers() {
		return userServices.getUsers();
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
				user.password = "";
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String userJson = gson.toJson(user);
				
				String json = Json.createObjectBuilder()
										.add("token", token)
										.add("user", userJson)
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
