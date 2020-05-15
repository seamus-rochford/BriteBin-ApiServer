package com.trandonsystems.britebin.resources;

import java.util.ArrayList;
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
import com.google.gson.JsonObject;
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.services.Hex;
import com.trandonsystems.britebin.services.UnitServices;
import com.trandonsystems.britebin.services.UserServices;;

@Path("unit")
public class UnitResources {

	static Logger log = Logger.getLogger(UnitResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	UserServices userServices = new UserServices();
	UnitServices unitServices = new UnitServices();
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
		log.debug("Unit resource is working!");
        return "Unit resource is working!";
    }

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnit")
	@JWTTokenNeeded
	public Response getUnit(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			Unit unit = new Unit();
			if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				unit = unitServices.getUnit(userFilterId, serialNo);
			} else if (queryParams.containsKey("unitId")) {
				int unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
								
				unit = unitServices.getUnit(userFilterId, unitId);
			} else {
				unit = null;
			}
			
			return Response.status(Response.Status.OK) // 200 
				.entity(unit)
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
	@Path("getUnits")
	@JWTTokenNeeded
	public Response getUnits(@Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Get userId from JwtToken (Note: if the role is driver or technician it will return the parentId)
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			// All units for this user
			List<Unit>units = unitServices.getUnits(userFilterId);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(units)
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
	public Response save(@Context HttpHeaders httpHeader, Unit unit) {
		String serialNo = "";
		try {
			log.info("POST: save unit");
			log.info("Unit: " + gson.toJson(unit));
			
			serialNo = unit.serialNo;
			
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);

			unit = unitServices.save(unit, actionUserId);
			log.info("Saved unit: " + gson.toJson(unit));

			String json = Json.createObjectBuilder()
									.add("unit", gson.toJson(unit))
									.build()
									.toString();
			
			return Response.status(Response.Status.OK) // 200 
					.entity(json)
					.build();
		
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());

			String errorMsg = "";
			if (ex.getMessage().contains("Duplicate")) {
				errorMsg = "This serialNo is already in use.";
			} else {
				errorMsg = ex.getMessage();
			}
			
			String json = Json.createObjectBuilder()
					.add("serialNo", serialNo)
					.add("message", errorMsg)
					.build()
					.toString();
			
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity(json)
					.build();
		}
	}

	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("install")
	@JWTTokenNeeded
	public Response install(@Context HttpHeaders httpHeader, Unit unit) {
		try {
			log.info("POST: install unit");
			log.info("User: " + gson.toJson(unit));
			
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);

			unit = unitServices.install(unit, actionUserId);
			log.info("Saved unit: " + gson.toJson(unit));

			String json = Json.createObjectBuilder()
									.add("unit", gson.toJson(unit))
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings")
	@JWTTokenNeeded
	public Response getUnitReadings(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			List<UnitReading>unitReadings = new ArrayList<UnitReading>();
			
			// Get limit parameter if it exists
			int limit = -1;
			if (queryParams.containsKey("limit")) {
				try {
					limit = Integer.parseInt(queryParams.getFirst("limit"));
				}
				catch(Exception ex) {
					limit = -1;
				}
			}
			
			if (queryParams.containsKey("unitId")) {
				int unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
								
				unitReadings = unitServices.getUnitReadings(userFilterId, unitId, limit);
			} else if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				unitReadings = unitServices.getUnitReadings(userFilterId, serialNo, limit);
			} 
			
			return Response.status(Response.Status.OK) // 200 
				.entity(unitReadings)
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
	@Path("pullReadings")
	@JWTTokenNeeded
	public Response pullReadings(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		// this recovers readings in the database that were recorded before the unit was setup
		// And then returns all readings for the unit
		try {
			log.info("UnitResources.pullReadings");
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			List<UnitReading>unitReadings = new ArrayList<UnitReading>();
	
			int unitId = 0;
			if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
			} else {
				throw new Exception("unitId required for <pullReadings>");
			}
			
			String serialNo = "";
			if (queryParams.containsKey("serialNo")) {
				serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
			} else {
				throw new Exception("serialNo required for <pullReadings>");
			}
			
			unitReadings = unitServices.pullReadings(userFilterId, unitId, serialNo);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(unitReadings)
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
	@Path("getLatestReadings")
	@JWTTokenNeeded
	public Response getLatestReadings(@Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			List<UnitReading>latestReadings = unitServices.getLatestReadings(userFilterId);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(latestReadings)
				.build();
			
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}	
	}
	

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// These ones were created for engineers testing units
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{serialNo}")
	public List<UnitReading> getUnitReadings(@PathParam("serialNo") String serialNo) {
		log.info("getUnitReadings(serialNo)");
		return unitServices.getUnitReadingsTest(serialNo, -1);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{serialNo}/limit/{limit}")
	public List<UnitReading> getUnitReadings(@PathParam("serialNo") String serialNo, @PathParam("limit") int limit) {
		log.info("getUnitReadings(serialNo, limit)");
		return unitServices.getUnitReadingsTest(serialNo, limit);
	}


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("saveMessage")
	public Response saveMessage(@QueryParam("unitId") int unitId, @QueryParam("msg") String msg, @QueryParam("userId") int userId) {
		try {
			log.info("POST: saveMessage(sigfoxBody)");
			log.debug("unitId: " + unitId);
			log.debug("msg: " + msg);
			log.debug("userId: " + userId);

			byte[] data = Hex.hexStringToByteArray(msg);

			unitServices.saveMessage(unitId, data, userId);
	
			return Response.status(Response.Status.OK).build();
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
}
