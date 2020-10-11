package com.trandonsystems.britebin.resources;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.model.RawData;
import com.trandonsystems.britebin.model.SigfoxBody;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.services.Hex;
import com.trandonsystems.britebin.services.SigfoxServices;
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
	public Response getUnits(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Get userId from JwtToken (Note: if the role is driver or technician it will return the parentId)
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			Boolean includeDeactive = false;
			if (queryParams.containsKey("includeDeactive")) {
				includeDeactive = queryParams.getFirst("includeDeactive").equalsIgnoreCase("true");
				log.debug("Include Deactive: " + includeDeactive);
			}
			
			// All units for this user
			List<Unit>units = unitServices.getUnits(userFilterId, includeDeactive);
			
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path("deactivate")
	@JWTTokenNeeded
	public Response deactivate(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Get userId from JwtToken 
			int actionUserId = userServices.getUserIdFromJwtToken(jwtToken);
			
			// Get user filter Id - if driver or technician the user filter is their parent Id
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			int unitId;
			if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				unitId = unitServices.getUnit(userFilterId, serialNo).id;
			} else if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				
				// Attempt to get to unit to validate they have access to this unit
				unitId = unitServices.getUnit(userFilterId, unitId).id;
				log.debug("unitId: " + unitId);								
			} else {
				throw new Exception("serialNo or unitId required for deactivating unit");
			}
			
			// Deactivate unit
			unitServices.deactivate(unitId, actionUserId);
			
			return Response.status(Response.Status.OK) // 200 
				.entity("")
				.build();
			
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}	
	}
    
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("activate")
	@JWTTokenNeeded
	public Response activate(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Get userId from JwtToken 
			int actionUserId = userServices.getUserIdFromJwtToken(jwtToken);
			
			// Get user filter Id - if driver or technician the user filter is their parent Id
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
						
			int unitId;
			if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				unitId = unitServices.getUnit(userFilterId, serialNo).id;
			} else if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				unitId = unitServices.getUnit(userFilterId, unitId).id;
				log.debug("unitId: " + unitId);								
			} else {
				throw new Exception("serialNo or unitId required for deactivating unit");
			}
			
			// Activate unit
			unitServices.activate(unitId, actionUserId);
			
			return Response.status(Response.Status.OK) // 200 
				.entity("")
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
	@Path("getUnitReadingsByPage")
	@JWTTokenNeeded
	public Response getUnitReadingsByPage(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			List<UnitReading>unitReadings = new ArrayList<UnitReading>();
			
			// Get dir parameter 
			String direction = "";
			if (queryParams.containsKey("direction")) {
				direction = queryParams.getFirst("direction");
				log.debug("direction: " + direction);
			} else {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("<dir> parameter not supplied")
						.build();
			}

			if (!(direction.equalsIgnoreCase("first") || direction.equalsIgnoreCase("prev") || direction.equalsIgnoreCase("next") || direction.equalsIgnoreCase("last"))) {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("Invalid value supplied for parameter <dir>, valid values 'first', 'prev', 'next' and 'last'")
						.build();
			}
			
			int lastId = 0;
			if (direction.equalsIgnoreCase("prev") || direction.equalsIgnoreCase("next")) {
				if (queryParams.containsKey("lastId")) {
					try {
						lastId = Integer.parseInt(queryParams.getFirst("lastId"));
						log.debug("lastId: " + lastId);					
					} catch (Exception ex) {
						return Response.status(Response.Status.BAD_REQUEST)
								.entity("Invalid value supplied for parameter <lastId>, it must be an integer")
								.build();
					}
				} else {
					return Response.status(Response.Status.BAD_REQUEST)
							.entity("<lastId> parameter not supplied")
							.build();
				}
			}
			
			int noRecords = 0;
			if (queryParams.containsKey("noRecords")) {
				try {
					noRecords = Integer.parseInt(queryParams.getFirst("noRecords"));
					log.debug("noRecords: " + noRecords);
					
					if (noRecords <= 0) {
						return Response.status(Response.Status.BAD_REQUEST)
								.entity("Invalid value supplied for parameter <noRecords>, it must be positive number")
								.build();
					}
				} catch (Exception ex) {
					return Response.status(Response.Status.BAD_REQUEST)
							.entity("Invalid value supplied for parameter <noRecords>, it must be an integer")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("<lastId> parameter not supplied")
						.build();
			}
			
			int unitId = 0;
			if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
			} else if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				Unit unit = unitServices.getUnit(userFilterId, serialNo);
				unitId = unit.id;
				log.debug("unitId: " + unitId);
			} 
			
			unitReadings = unitServices.getUnitReadings(userFilterId, unitId, direction, lastId, noRecords);		
			
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
	@Path("processUnprocessedData")
	@JWTTokenNeeded
    public Response processUnprocessedData() {
    	try {		
    		log.info("processUnprocessedData - start");
    		
			SigfoxServices sigfoxServices = new SigfoxServices();
			UnitServices unitServices = new UnitServices();

			// Process Sigfox RawData
			String source = "Sigfox";
			List<RawData> readings = unitServices.getUnprocessedRawData(source);
    		log.info("processUnprocessedData - " + readings.size() + " Sigfox readings to process");
    		for (int i = 0; i < readings.size(); i++) {
    			int rawDataId = readings.get(i).id;
    			Instant readingDateTime = readings.get(i).insertAt;
    			
    			String sigfoxStr = new String(readings.get(i).rawData, "UTF-8");
    			SigfoxBody sigfoxBody = gson.fromJson(sigfoxStr, SigfoxBody.class);
    			
    			sigfoxServices.saveRawDataOnly(rawDataId, readingDateTime, sigfoxBody);
    		}

    		// Process NB-IoT BriteBin RawData
    		source = "NB-IoT BB";
    		readings = unitServices.getUnprocessedRawData(source);
    		log.info("processUnprocessedData - " + readings.size() + " NB-IoT BB readings to process");
    		for (int i = 0; i < readings.size(); i++) {
    			int rawDataId = readings.get(i).id;
    			Instant readingDateTime = readings.get(i).insertAt;
    			byte[] reading = readings.get(i).rawData;
    			
    			unitServices.processBriteBinDataOnly(source, rawDataId, readingDateTime, reading);
    		}
    		
    		// Process NB-IoT Tekelek RawData
    		source = "NB-IoT Tek";
    		readings = unitServices.getUnprocessedRawData(source);
    		log.info("processUnprocessedData - " + readings.size() + " NB-IoT Tek readings to process");
    		for (int i = 0; i < readings.size(); i++) {
    			int rawDataId = readings.get(i).id;
    			Instant readingDateTime = readings.get(i).insertAt;
    			byte[] reading = readings.get(i).rawData;
    			
    			unitServices.processTekelekDataOnly(source, rawDataId, readingDateTime, reading);
    		}
    		
    		log.info("processUnprocessedData - end");

    		return Response.status(Response.Status.OK)
					.entity("Success")
					.build();    		
    	} catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();    		
    	}
    }
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getLatestReadings")
	@JWTTokenNeeded
	public Response getLatestReadings(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
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
			
			List<UnitReading>latestReadings = unitServices.getLatestReadings(userFilterId, includeDeactive);
			
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
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("sendDeviceMessage")
	@JWTTokenNeeded
	public Response sendDeviceMessage(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		// Generic send Device Message for someone who knows what they are doing
		try {
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userId = userServices.getUserIdFromJwtToken(jwtToken);
			
			int unitId = 0;
			if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				// Pass 1 as unit filter id so all units are available
				Unit unit = unitServices.getUnit(1, serialNo);
				unitId = unit.id;
				log.debug("unitId: " + unitId);
			} else if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Missing parameter - <unitId> or <serialNo> must be supplied")
						.build();
			}

			String msg = "";
			if (queryParams.containsKey("msg")) {
				msg = queryParams.getFirst("msg");
				log.debug("msg: " + msg);
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("parameter <msg> missing")
						.build();
			}			

			if (msg.length() > 16) {
				// msg can be maximum 8 bytes (16 hex characters)
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("<msg> too long - max. 8 bytes (16 hex charcaters)")
						.build();				
			}
			
			byte[] data = Hex.hexStringToByteArray(msg);
			
			if((data[7] & 0x01) == 1 && (data[7] & 0x02) == 2) {
				// Can NOT have "Lock Bin" and "Unlock Bin" set at the same time
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("<msg> byte 8 - has 'Lock Bin' and 'Unlock Bin' flags both set - these are mutually exclusive flags")
						.build();	
			}

			unitServices.saveMessage(unitId, data, userId);
	
			return Response.status(Response.Status.OK).entity("Success").build();
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
	@Path("sendDeviceMessageType2")
	@JWTTokenNeeded
	public Response sendDeviceMessageType2(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		try {
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userId = userServices.getUserIdFromJwtToken(jwtToken);
			
			int unitId = 0;
			if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				// Pass 1 as unit filter id so all units are available
				Unit unit = unitServices.getUnit(1, serialNo);
				unitId = unit.id;
				log.debug("unitId: " + unitId);
			} else if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Missing parameter - <unitId> or <serialNo> must be supplied")
						.build();
			}

			int compactionPeriod = 0;  // number of minutes
			if (queryParams.containsKey("compactionPeriod")) {
				compactionPeriod = Integer.parseInt(queryParams.getFirst("compactionPeriod"));
				log.debug("compactionPeriod: " + compactionPeriod);
				
				if (compactionPeriod <= 1) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <compactionPeriod> must be greater than 1 minute")
							.build();
				} else if (compactionPeriod > 255) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <compactionPeriod> must be less than 256 minutes")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <compactionPeriod> missing")
						.build();
			}			

			double batteryUVLO = 0.0;
			if (queryParams.containsKey("batteryUVLO")) {
				batteryUVLO = Double.parseDouble(queryParams.getFirst("batteryUVLO"));
				log.debug("batteryUVLO: " + batteryUVLO);
				
				if (batteryUVLO < 7.0) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("BaterryUVLO mus t be >= 7.0 Volts")
							.build();
				} else if (batteryUVLO > 19.75) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("BaterryUVLO must be <= 19.75 Volts")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <batteryUVLO> missing")
						.build();
			}			

			int overTemperatureTreshold = 0;
			if (queryParams.containsKey("overTemperatureTreshold")) {
				overTemperatureTreshold = Integer.parseInt(queryParams.getFirst("overTemperatureTreshold"));
				log.debug("overTemperatureTreshold: " + overTemperatureTreshold);
				
				if (overTemperatureTreshold < -128) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <overTemperatureTreshold> must be greater than -128 degrees Celcius")
							.build();
				} else if (overTemperatureTreshold > 127) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <overTemperatureTreshold> must be less than 128 degrees Celcius")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <overTemperatureTreshold> missing")
						.build();
			}	
				
			int underTemperatureTreshold = 0;
			if (queryParams.containsKey("underTemperatureTreshold")) {
				underTemperatureTreshold = Integer.parseInt(queryParams.getFirst("underTemperatureTreshold"));
				log.debug("underTemperatureTreshold: " + underTemperatureTreshold);
				
				if (overTemperatureTreshold < -128) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <underTemperatureTreshold> must be greater than -128 degrees Celcius")
							.build();
				} else if (overTemperatureTreshold > 127) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <underTemperatureTreshold> must be less than 128 degrees Celcius")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <underTemperatureTreshold> missing")
						.build();
			}	
				
			double compactionTreshold = 0.0;
			if (queryParams.containsKey("compactionTreshold")) {
				compactionTreshold = Double.parseDouble(queryParams.getFirst("compactionTreshold"));
				log.debug("compactionTreshold: " + compactionTreshold);
				
				if (compactionTreshold < 0) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <compactionTreshold> must be greater than or equal to 0.0 %")
							.build();
				} else if (compactionTreshold > 127) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <underTemperatureTreshold> must be less than or equal to 100.0 %")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <compactionTreshold> missing")
						.build();
			}	
				
			double binFullLevel = 0.0;
			if (queryParams.containsKey("binFullLevel")) {
				binFullLevel = Double.parseDouble(queryParams.getFirst("binFullLevel"));
				log.debug("binFullLevel: " + binFullLevel);
				
				if (binFullLevel < 0) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <binFullLevel> must be greater than or equal to 0.0 %")
							.build();
				} else if (binFullLevel > 127) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("Parameter <binFullLevel> must be less than or equal to 100.0 %")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <binFullLevel> missing")
						.build();
			}	
			
			int flags = 0;
			Boolean lockBin = false;
			if (queryParams.containsKey("lockBin")) {
				lockBin = queryParams.getFirst("lockBin").equalsIgnoreCase("true");
			}			
			log.debug("Lock Bin: " + lockBin);
			if (lockBin) {
				flags += 1;
			}
			
			Boolean unlockBin = false;
			if (queryParams.containsKey("unlockBin")) {
				unlockBin = queryParams.getFirst("unlockBin").equalsIgnoreCase("true");
			}			
			log.debug("Unlock Bin: " + unlockBin);
			
			if (lockBin && unlockBin) {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("'Lock Bin' and 'Unlock Bin' flags both set - these are mutually exclusive flags")
						.build();					
			}
			if (unlockBin) {
				flags += 2;
			}
						
			Boolean resetCompactCounter = false;
			if (queryParams.containsKey("resetCompactCounter")) {
				resetCompactCounter = queryParams.getFirst("resetCompactCounter").equalsIgnoreCase("true");
			}			
			log.debug("Reset Compact Counter: " + resetCompactCounter);
			if (resetCompactCounter) {
				flags += 4;
			}
			
			Boolean softwareResetMCU = false;
			if (queryParams.containsKey("softwareResetMCU")) {
				softwareResetMCU = queryParams.getFirst("softwareResetMCU").equalsIgnoreCase("true");
			}			
			log.debug("Software Reset MCU: " + softwareResetMCU);
			if (softwareResetMCU) {
				flags += 8;
			}
			
			// Not allowed anymore
//			Boolean forceTimeSyncNTPServer = false;
//			if (queryParams.containsKey("forceTimeSyncNTPServer")) {
//				forceTimeSyncNTPServer = queryParams.getFirst("forceTimeSyncNTPServer").equalsIgnoreCase("true");
//			}			
//			log.debug("Force Time Sync NTP Server: " + forceTimeSyncNTPServer);
//			if (forceTimeSyncNTPServer) {
//				flags += 16;
//			}
			
			// Build the byte array
			byte[] msg = new byte[8];
			msg[0] = (byte)2;		// Message Type 2
			msg[1] = (byte)compactionPeriod;
			msg[2] = (byte)((int) Math.round((batteryUVLO - 7.0) / 0.5));
			msg[3] = (byte)overTemperatureTreshold;
			msg[4] = (byte)underTemperatureTreshold;
			msg[5] = (byte)((int) Math.round(compactionTreshold / 0.5));
			msg[6] = (byte)((int) Math.round(binFullLevel / 0.5));
			msg[7] = (byte)flags;	

			unitServices.saveMessage(unitId, msg, userId);
	
			return Response.status(Response.Status.OK).entity("Success").build();
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
	@Path("sendDeviceMessageType3")
	@JWTTokenNeeded
	public Response sendDeviceMessageType3(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		try {
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userId = userServices.getUserIdFromJwtToken(jwtToken);
			
			int unitId = 0;
			if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				// Pass 1 as unit filter id so all units are available
				Unit unit = unitServices.getUnit(1, serialNo);
				unitId = unit.id;
				log.debug("unitId: " + unitId);
			} else if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Missing parameter - <unitId> or <serialNo> must be supplied")
						.build();
			}

//			int timezone = 0;  //  (PCB Timezone; Example GMT-2 would be -2)
			// Not allowed anymore - using UTC time zone
//			if (queryParams.containsKey("timezone")) {
//				timezone = Integer.parseInt(queryParams.getFirst("timezone"));
//				log.debug("timezone: " + timezone);
//				
//				if (timezone <= 12) {
//					return Response.status(Response.Status.BAD_REQUEST) // 400 
//							.entity("Parameter <timezone> must be greater than or equal to 12")
//							.build();
//				} else if (timezone >= 12) {
//					return Response.status(Response.Status.BAD_REQUEST) // 400 
//							.entity("Parameter <timezone> must be less than or equal to 12")
//							.build();
//				}
//			} else {
//				return Response.status(Response.Status.BAD_REQUEST) // 400 
//						.entity("Parameter <timezone> missing")
//						.build();
//			}			

			int nightModeEnterHours = 0;
			if (queryParams.containsKey("nightModeEnterHours")) {
				nightModeEnterHours = Integer.parseInt(queryParams.getFirst("nightModeEnterHours"));
				log.debug("nightModeEnterHours: " + nightModeEnterHours);
				
				if (nightModeEnterHours < 0) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeEnterHours must be >= 0 hours")
							.build();
				} else if (nightModeEnterHours > 23) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeEnterHours must be < 24 hours")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <nightModeEnterHours> missing")
						.build();
			}			

			int nightModeEnterMinutes = 0;
			if (queryParams.containsKey("nightModeEnterMinutes")) {
				nightModeEnterHours = Integer.parseInt(queryParams.getFirst("nightModeEnterMinutes"));
				log.debug("nightModeEnterMinutes: " + nightModeEnterHours);
				
				if (nightModeEnterMinutes < 0) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeEnterMinutes must be >= 0 minutes")
							.build();
				} else if (nightModeEnterMinutes > 23) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeEnterMinutes must be <= 59 minutes")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <nightModeEnterHours> missing")
						.build();
			}	
				
			int nightModeExitHours = 0;
			if (queryParams.containsKey("nightModeExitHours")) {
				nightModeExitHours = Integer.parseInt(queryParams.getFirst("nightModeExitHours"));
				log.debug("nightModeExitHours: " + nightModeExitHours);
				
				if (nightModeExitHours < 0) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeExitHours must be >= 0 hours")
							.build();
				} else if (nightModeExitHours > 23) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeExitHours must be < 24 hours")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <nightModeExitHours> missing")
						.build();
			}			

			int nightModeExitMinutes = 0;
			if (queryParams.containsKey("nightModeExitMinutes")) {
				nightModeExitMinutes = Integer.parseInt(queryParams.getFirst("nightModeExitMinutes"));
				log.debug("nightModeExitMinutes: " + nightModeExitMinutes);
				
				if (nightModeExitMinutes < 0) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeExitMinutes must be >= 0 minutes")
							.build();
				} else if (nightModeExitMinutes > 23) {
					return Response.status(Response.Status.BAD_REQUEST) // 400 
							.entity("nightModeExitMinutes must be <= 59 minutes")
							.build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Parameter <nightModeExitMinutes> missing")
						.build();
			}	
				
			int flags = 0;
			Boolean lockBin = false;
			if (queryParams.containsKey("lockBin")) {
				lockBin = queryParams.getFirst("lockBin").equalsIgnoreCase("true");
			}			
			log.debug("Lock Bin: " + lockBin);
			if (lockBin) {
				flags += 1;
			}
			
			Boolean unlockBin = false;
			if (queryParams.containsKey("unlockBin")) {
				unlockBin = queryParams.getFirst("unlockBin").equalsIgnoreCase("true");
			}			
			log.debug("Unlock Bin: " + unlockBin);
			
			if (lockBin && unlockBin) {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("'Lock Bin' and 'Unlock Bin' flags both set - these are mutually exclusive flags")
						.build();					
			}
			if (unlockBin) {
				flags += 2;
			}
						
			Boolean resetCompactCounter = false;
			if (queryParams.containsKey("resetCompactCounter")) {
				resetCompactCounter = queryParams.getFirst("resetCompactCounter").equalsIgnoreCase("true");
			}			
			log.debug("Reset Compact Counter: " + resetCompactCounter);
			if (resetCompactCounter) {
				flags += 4;
			}
			
			Boolean softwareResetMCU = false;
			if (queryParams.containsKey("softwareResetMCU")) {
				softwareResetMCU = queryParams.getFirst("softwareResetMCU").equalsIgnoreCase("true");
			}			
			log.debug("Software Reset MCU: " + softwareResetMCU);
			if (softwareResetMCU) {
				flags += 8;
			}
			
			// Not used anymore
//			Boolean forceTimeSyncNTPServer = false;
//			if (queryParams.containsKey("forceTimeSyncNTPServer")) {
//				forceTimeSyncNTPServer = queryParams.getFirst("forceTimeSyncNTPServer").equalsIgnoreCase("true");
//			}			
//			log.debug("Force Time Sync NTP Server: " + forceTimeSyncNTPServer);
//			if (forceTimeSyncNTPServer) {
//				flags += 16;
//			}
			
			int nightModeEnter = Math.round((nightModeEnterHours * 60 + nightModeEnterMinutes) / 10);
			int nightModeExit = Math.round((nightModeExitHours * 60 + nightModeExitMinutes) / 10);
			// Build the byte array
			byte[] msg = new byte[8];
			msg[0] = (byte)3;		// Message Type 2
			msg[1] = (byte)0;
			msg[2] = (byte)nightModeEnter;
			msg[3] = (byte)nightModeExit;
			msg[4] = (byte)0;
			msg[5] = (byte)0;
			msg[6] = (byte)0;
			msg[7] = (byte)flags;	

			unitServices.saveMessage(unitId, msg, userId);
	
			return Response.status(Response.Status.OK).entity("Success").build();
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
	@Path("sendDeviceMessageType4")
	@JWTTokenNeeded
	public Response sendDeviceMessageType4(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		try {
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userId = userServices.getUserIdFromJwtToken(jwtToken);
			
			int unitId = 0;
			if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				
				// Pass 1 as unit filter id so all units are available
				Unit unit = unitServices.getUnit(1, serialNo);
				unitId = unit.id;
				log.debug("unitId: " + unitId);
			} else if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
			} else {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("Missing parameter - <unitId> or <serialNo> must be supplied")
						.build();
			}
			
			int flags = 0;
			Boolean lockBin = false;
			if (queryParams.containsKey("lockBin")) {
				lockBin = queryParams.getFirst("lockBin").equalsIgnoreCase("true");
			}			
			log.debug("Lock Bin: " + lockBin);
			if (lockBin) {
				flags += 1;
			}
			
			Boolean unlockBin = false;
			if (queryParams.containsKey("unlockBin")) {
				unlockBin = queryParams.getFirst("unlockBin").equalsIgnoreCase("true");
			}			
			log.debug("Unlock Bin: " + unlockBin);
			
			if (lockBin && unlockBin) {
				return Response.status(Response.Status.BAD_REQUEST) // 400 
						.entity("'Lock Bin' and 'Unlock Bin' flags both set - these are mutually exclusive flags")
						.build();					
			}
			if (unlockBin) {
				flags += 2;
			}
						
			Boolean resetCompactCounter = false;
			if (queryParams.containsKey("resetCompactCounter")) {
				resetCompactCounter = queryParams.getFirst("resetCompactCounter").equalsIgnoreCase("true");
			}			
			log.debug("Reset Compact Counter: " + resetCompactCounter);
			if (resetCompactCounter) {
				flags += 4;
			}
			
			Boolean softwareResetMCU = false;
			if (queryParams.containsKey("softwareResetMCU")) {
				softwareResetMCU = queryParams.getFirst("softwareResetMCU").equalsIgnoreCase("true");
			}			
			log.debug("Software Reset MCU: " + softwareResetMCU);
			if (softwareResetMCU) {
				flags += 8;
			}
			
			// Not used anymore
//			Boolean forceTimeSyncNTPServer = false;
//			if (queryParams.containsKey("forceTimeSyncNTPServer")) {
//				forceTimeSyncNTPServer = queryParams.getFirst("forceTimeSyncNTPServer").equalsIgnoreCase("true");
//			}			
//			log.debug("Force Time Sync NTP Server: " + forceTimeSyncNTPServer);
//			if (forceTimeSyncNTPServer) {
//				flags += 16;
//			}
			
			// Get UTC Date/Time
			LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
			
			// Build the byte array
			byte[] msg = new byte[8];
			msg[0] = (byte)4; // Message Type = 4 - Set time of the unit
			msg[1] = (byte)(now.getYear() % 100);  // Get 2 digit year part binFullLevel
			msg[2] = (byte)now.getMonthValue();
			msg[3] = (byte)now.getDayOfMonth();
			msg[4] = (byte)now.getHour();
			msg[5] = (byte)now.getMinute();
			msg[6] = (byte)now.getSecond();
			msg[7] = (byte)flags;

			unitServices.saveMessage(unitId, msg, userId);
	
			return Response.status(Response.Status.OK).entity("Success").build();
		}
		catch(Exception ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST) // 400 
					.entity("Error: " + ex.getMessage())
					.build();
		}
	}		
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// These ones were created for engineers testing units
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{serialNo}")
	public List<UnitReading> getUnitReadings(@PathParam("serialNo") String serialNo) {
		try {
			log.info("getUnitReadings(serialNo)");
			return unitServices.getUnitReadingsTest(serialNo, -1);
		} catch (SQLException ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return null;			
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{serialNo}/limit/{limit}")
	public List<UnitReading> getUnitReadings(@PathParam("serialNo") String serialNo, @PathParam("limit") int limit) {
		try {
			log.info("getUnitReadings(serialNo, limit)");
			return unitServices.getUnitReadingsTest(serialNo, limit);
		} catch (SQLException ex) {
			log.error(Response.Status.BAD_REQUEST + " - " + ex.getMessage());
			return null;			
		}
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
	
			return Response.status(Response.Status.OK).entity("Success").build();
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
