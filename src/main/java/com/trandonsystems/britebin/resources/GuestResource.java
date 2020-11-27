package com.trandonsystems.britebin.resources;

import java.util.List;

import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.model.GuestUnit;
import com.trandonsystems.britebin.services.GuestServices;
import com.trandonsystems.britebin.services.UserServices;

@Path("guest")
public class GuestResource {

	static Logger log = Logger.getLogger(UserResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	UserServices userServices = new UserServices();
	GuestServices guestServices = new GuestServices();
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
		log.debug("Guest resource is working!");
        return "Guest resource is working!";
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getGuestUnit")
	@JWTTokenNeeded
	public Response getGuestUnit(@Context UriInfo uriInfo) {
		log.debug("getGuestUnit");
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			
			GuestUnit guestUnit = new GuestUnit();
			if (queryParams.containsKey("guestUnitId")) {
				int guestUnitId = Integer.parseInt(queryParams.getFirst("guestUnitId"));
				log.debug("guestUnitId: " + guestUnitId);
				
				guestUnit = guestServices.getGuestUnit(guestUnitId);
			} else {
				log.debug("Parameter <<guestUnitId>> missing");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("ERROR: Parameter <<guestUnitId>> missing")
						.build();
			}
			
			return Response.status(Response.Status.OK) // 200 
				.entity(guestUnit)
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
	@Path("getGuestUnits")
	@JWTTokenNeeded
	public Response getGuestUnits(@Context UriInfo uriInfo) {
		log.debug("getUsers");
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

			int guestId = 0;
			if (queryParams.containsKey("guestId")) {
				guestId = Integer.parseInt(queryParams.getFirst("guestId"));
			} else {
				guestId = -1;
			}
			
			log.debug("guestId: " + guestId);
			List<GuestUnit>guestUnits = guestServices.getGuestUnits(guestId);
			
			log.debug("No. guestUnits: " + guestUnits.size());
			return Response.status(Response.Status.OK) // 200 
				.entity(guestUnits)
				.build();
			
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}
	}
	
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("deleteGuestUnit")
	@JWTTokenNeeded
	public Response deleteGuestUnit(@Context UriInfo uriInfo) {
		log.debug("deleteGuestUnit");
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			
			int rowsDeleted = 0;
			
			if (queryParams.containsKey("guestUnitId")) {
				int guestUnitId = Integer.parseInt(queryParams.getFirst("guestUnitId"));
				log.debug("guestUnitId: " + guestUnitId);
				
				guestServices.deleteGuestUnit(guestUnitId);
			} else if (queryParams.containsKey("guestId")) {
				int guestId = Integer.parseInt(queryParams.getFirst("guestId"));
				log.debug("guestId: " + guestId);
				
				rowsDeleted = guestServices.deleteGuestUnits(guestId);
			} else {
				log.debug("Parameter <<guestUnitId>> or <<guestId>> must be supplied");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("ERROR: Parameter <<guestUnitId>> missing")
						.build();
			}
			
			String json = Json.createObjectBuilder()
					.add("Result", "Delete Success")
					.add("Rows Deleted", rowsDeleted)
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
	@Path("save")
	@JWTTokenNeeded
	public Response save(GuestUnit guestUnit) {
		try {
			log.info("POST: save guest unit");
			log.info("guestUnit: " + gson.toJson(guestUnit));
			
			guestServices.save(guestUnit);

			String json = Json.createObjectBuilder()
									.add("Result", "Success")
									.build()
									.toString();
			
			return Response.status(Response.Status.OK) // 200 
					.entity(json)
					.build();
		
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());

			String json = Json.createObjectBuilder()
					.add("message", ex.getMessage())
					.build()
					.toString();
			
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity(json)
					.build();
		}
	}

}
