package com.trandonsystems.britebin.resources;

import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.services.SystemServices;

@Path("system")
public class SystemResources {

	static Logger log = Logger.getLogger(UserResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	SystemServices systemServices = new SystemServices();
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
		log.info("System resources is working");
        return "System resource is working!";
    }
    
    @GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getMobileVersion")
	@JWTTokenNeeded
	public Response getMobileVersion(@Context HttpHeaders httpHeaders) {
		
		try {
			log.info("system/getMobileVersion");
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String mobileVersion = systemServices.getMobileVersion();
			
			String json = Json.createObjectBuilder()
					.add("mobileVersion", mobileVersion)
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
	
}
