package com.trandonsystems.britebin.resources;

import java.util.List;

import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.model.BinLevel;
import com.trandonsystems.britebin.model.KeyValue;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.services.Hex;
import com.trandonsystems.britebin.services.SystemServices;
import com.trandonsystems.britebin.services.UnitServices;
import com.trandonsystems.britebin.services.UserServices;

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
	public Response getMobileVersion() {
		
		try {
			log.info("systemResource.getMobileVersion");
	
			String mobileVersion = systemServices.getSysConfigValue("mobileVersion");
			
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
	
    
    @GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getSysConfigValue")
	@JWTTokenNeeded
	public Response getSysConfigValue(@Context UriInfo ui) {
		
		try {
			log.info("systemResource.getSysConfigValue(name)");
			
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			
			String name = queryParams.getFirst("name");
			log.info("Name: " + name);
	
			KeyValue keyValue = new KeyValue();
			keyValue.key = name;
			keyValue.value = systemServices.getSysConfigValue(name);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(keyValue)
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
	@Path("getSysConfigValues")
	@JWTTokenNeeded
	public Response getSysConfigValues() {
		
		try {
			log.info("systemResource.getSysConfigValues");
	
			List<KeyValue> configValues = systemServices.getSysConfigValues();

			return Response.status(Response.Status.OK) // 200 
					.entity(gson.toJson(configValues))
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
	@Path("saveSysConfigValue")
	@JWTTokenNeeded
	public Response saveSysConfigValue(@Context HttpHeaders httpHeader, KeyValue kv) {
		try {
			log.info("POST: saveSysConfigValue(KeyValue)");
			log.info("KeyValue: " + gson.toJson(kv));

			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			UserServices userServices = new UserServices();
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);

			systemServices.saveSysConfigValue(kv, actionUserId);
			log.info("Saved sysConfigValue");

			String json = Json.createObjectBuilder()
									.add("Success", true)
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
    
}
