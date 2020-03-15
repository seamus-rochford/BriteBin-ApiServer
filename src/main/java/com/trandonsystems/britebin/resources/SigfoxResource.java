package com.trandonsystems.britebin.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.auth.BriteBinApiKeyNeeded;
import com.trandonsystems.britebin.model.SigfoxBody;
import com.trandonsystems.britebin.services.SigfoxServices;
import com.trandonsystems.britebin.services.UnitServices;

@Path("sigfox")
public class SigfoxResource {

	static Logger log = Logger.getLogger(UserResource.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	UnitServices unitServices = new UnitServices();
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
    	log.debug("Sigfox resource is working!");
        return "Sigfox resource is working!";
    }
    
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("postReading")
//	@BriteBinApiKeyNeeded
	public Response postReading(@QueryParam("id") String id, SigfoxBody sigfoxBody) {
		try {
			log.info("POST: postReading(sigfoxBody)");
			log.debug("DeviceId: " + id);
			
			log.debug("Sigfox Body: ");
			log.debug(gson.toJson(sigfoxBody));
			
			SigfoxServices sigfoxServices = new SigfoxServices();
			sigfoxServices.saveData(id, sigfoxBody);
	
			return Response.status(Response.Status.OK).build();
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}

    
	// Example of using PATH-PARAMS
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("examplePathParam/{devicdeId}")
	@BriteBinApiKeyNeeded
	public Response examplePathParam(@PathParam("deviceId") String deviceId) {
		log.info("POST: examplePathParam(deviceId)");
		log.debug("DeviceId: " + deviceId);
		
		Response response = Response.status(200).entity("examplePathParam worked - deviceId : " + deviceId).build();
		
		return response;
	}

	// Example of using QUERY PARAMS
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("exampleQueryParam")
	@BriteBinApiKeyNeeded
	public Response exampleQueryParam(@QueryParam("deviceId") String deviceId, @QueryParam("userId") String userId) {
		log.info("POST: exampleQueryParam(deviceId, userId)");
		log.debug("DeviceId: " + deviceId);
		log.debug("userId: " + userId);
		
		JsonObject paramsObj = new JsonObject();
		paramsObj.addProperty("DeviceId", deviceId);
		paramsObj.addProperty("UserId", userId);
		
		Response response = Response.status(200).entity("exampleQueryParam worked:" + paramsObj.toString()).build();
		
		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("exampleMultiParams")
	@BriteBinApiKeyNeeded
	public Response exampleMultiParams(@Context UriInfo ui) {
		log.info("POST: exampleMultiParams(deviceId)");
		
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

		String deviceId = queryParams.getFirst("deviceId");
		log.debug("DeviceId: " + deviceId);
		
		String userId = queryParams.getFirst("userId");
		log.debug("UserId: " + userId);
		
		String source = queryParams.getFirst("source");
		log.debug("Source: " + source);
		
		JsonObject respObj = new JsonObject();
		respObj.addProperty("Result", "exampleMultiParams worked");
		
		JsonObject paramsObj = new JsonObject();
		paramsObj.addProperty("DeviceId", deviceId);
		paramsObj.addProperty("UserId", userId);
		paramsObj.addProperty("Source", source);

		respObj.add("Params", paramsObj);
		
		Response response = Response.status(200).entity(respObj.toString()).build();
		
		return response;
	}

}
