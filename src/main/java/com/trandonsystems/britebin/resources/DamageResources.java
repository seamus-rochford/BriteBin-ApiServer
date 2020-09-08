package com.trandonsystems.britebin.resources;

import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;  
import org.glassfish.jersey.media.multipart.FormDataParam; 

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.model.Damage;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.services.DamageServices;
import com.trandonsystems.britebin.services.UnitServices;
import com.trandonsystems.britebin.services.UserServices;

@Path("damage")
public class DamageResources {

	static final long MEDIUM_BLOB_SIZE = 16777215;
	
	static Logger log = Logger.getLogger(DamageResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	DamageServices damageServices = new DamageServices();
	UserServices userServices = new UserServices();
	UnitServices unitServices = new UnitServices();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getDamage")
	@JWTTokenNeeded
	public Response getDamage(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
//			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			
			Damage damage = new Damage();
			if (queryParams.containsKey("damageId")) {
				int damageId = Integer.parseInt(queryParams.getFirst("damageId"));
				log.debug("damageId: " + damageId);
								
				damage = damageServices.getDamage(damageId, userFilterId);
			} else {
				throw new Exception("No <<damageId>> supplied as parameter");
			}
			
			return Response.status(Response.Status.OK) // 200 
				.entity(damage)
				.build();
			
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}	
	}
	
	
	@GET
	@Produces({"image/png", "image/jpg"})
	@Path("getDamageHistoryImage")
	@JWTTokenNeeded
	public Response getDamageHistoryImage(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
//			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
//			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			
			byte[] image = new byte[0];
			if (queryParams.containsKey("damageHistoryId")) {
				int damageHistoryId = Integer.parseInt(queryParams.getFirst("damageHistoryId"));
				log.debug("damageHistoryId: " + damageHistoryId);
								
				image = damageServices.getDamageHistoryImage(damageHistoryId);
			} else {
				throw new Exception("No <<damageId>> supplied as parameter");
			}
			
			return Response.status(Response.Status.OK) // 200 
				.entity(image)
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
	@Path("getDamages")
	@JWTTokenNeeded
	public Response getDamages(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
//			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);

			// Get the parameters
			int damageStatusId = 0;
			if (queryParams.containsKey("damageStatusId")) {
				damageStatusId = Integer.parseInt(queryParams.getFirst("damageStatusId"));
			} else {
				damageStatusId = 0;
			}
			
			Instant fromInst;
			if (queryParams.containsKey("fromDate")) {
				String fromDate = queryParams.getFirst("fromDate") + "T00:00:00.000Z";
				fromInst = Instant.parse(fromDate);
			} else {
				// if no fromDate supplied assume date a long time ago
				fromInst = Instant.parse("2000-01-01T00:00:00.000Z");
			}			

			Instant toInst;
			if (queryParams.containsKey("toDate")) {
				String toDate = queryParams.getFirst("toDate") + "T23:59:59.000Z";
				toInst = Instant.parse(toDate);
			} else {
				// if no toDate supplied assume a date a long time in the future
				toInst = Instant.parse("2099-12-31T23:59:59.000Z");
			}			

			List<Damage> damages = damageServices.getDamages(damageStatusId, fromInst, toInst, userFilterId);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(damages)
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
	@Path("getAssignedDamages")
	@JWTTokenNeeded
	public Response getAssignedDamages(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
						
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
//			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);

			// Get the parameters
			int assignedUserId = 0;
			if (queryParams.containsKey("assignedUserId")) {
				assignedUserId = Integer.parseInt(queryParams.getFirst("assignedUserId"));
			} else {
				throw new Exception("No <<assignedUserId>> parameter supplied");
			}
			
			Instant fromInst;
			if (queryParams.containsKey("fromDate")) {
				String fromDate = queryParams.getFirst("fromDate") + "T00:00:00.000Z";
				fromInst = Instant.parse(fromDate);
			} else {
				// if no fromDate supplied assume date a long time ago
				fromInst = Instant.parse("2000-01-01T00:00:00.000Z");
			}			

			Instant toInst;
			if (queryParams.containsKey("toDate")) {
				String toDate = queryParams.getFirst("toDate") + "T23:59:59.000Z";
				toInst = Instant.parse(toDate);
			} else {
				// if no toDate supplied assume a date a long time in the future
				toInst = Instant.parse("2099-12-31T23:59:59.000Z");
			}

			List<Damage> damages = damageServices.getAssignedDamages(assignedUserId, fromInst, toInst, userFilterId);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(damages)
				.build();
			
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}	
	}
	
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("report")
	@JWTTokenNeeded
	public Response report(
			@Context UriInfo uriInfo, 
			@Context HttpHeaders httpHeader,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {
		try {
			log.info("POST: report");
			
			boolean imageIncluded = false;
			String uploadImageFileName = "";
			
	        // check if all form parameters are provided
	        if (uploadedInputStream == null || fileDetails == null) {
	        	imageIncluded = false;
//	            return Response.status(400).entity("Invalid form data").build();
	        } else {
	        	imageIncluded = true;

		        // Create folder if it does not exist
		        UtilResources.createFolderIfNotExists(UtilResources.UPLOAD_FOLDER);
		        
		        log.debug("Upload image to server");
		        uploadImageFileName = UtilResources.UPLOAD_FOLDER + fileDetails.getFileName();
		        UtilResources.saveToFile(uploadedInputStream, uploadImageFileName);
		        log.debug("upload image file to server - complete");		        

	    		File file = new File(uploadImageFileName);
	    		if (file.length() > MEDIUM_BLOB_SIZE) {
	    			log.error(Response.Status.BAD_REQUEST + " - Image is too large to save in the database");
	    			return Response.status(Response.Status.BAD_REQUEST) // 400 
	    					.entity("Image is too large to save in the database")
	    					.build();	    		}
	        	
	        }
	        
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserIdFromJwtToken(jwtToken);
//			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			
			// Get the damage parameters
			int damageTypeId = 0;
			if (queryParams.containsKey("damageTypeId")) {
				damageTypeId = Integer.parseInt(queryParams.getFirst("damageTypeId"));
				log.debug("DamageTypeId: " + damageTypeId);
			} else {
				throw new Exception("No 'damageTypeId' supplied");
			}
			
			int unitId = 0;
			if (queryParams.containsKey("unitId")) {
				unitId = Integer.parseInt(queryParams.getFirst("unitId"));
				log.debug("unitId: " + unitId);
			} else if (queryParams.containsKey("serialNo")) {
				String serialNo = queryParams.getFirst("serialNo");
				log.debug("serialNo: " + serialNo);
				Unit unit = unitServices.getUnit(1, serialNo);
				unitId = unit.id;
			}
			else {
				throw new Exception("No 'serialNo' or 'unitId' supplied");
			}
			
			String comment = "";
			if (queryParams.containsKey("comment")) {
				comment = queryParams.getFirst("comment");
			} else {
				throw new Exception("No 'comment' supplied");
			}
			
			Damage damage = damageServices.report(damageTypeId, unitId, comment, actionUserId, imageIncluded, uploadImageFileName);
			log.info("Damage Reported: " + gson.toJson(damage));

//			String json = Json.createObjectBuilder()
//									.add("damage", gson.toJson(damage))
//									.build()
//									.toString();
			
			return Response.status(Response.Status.OK) // 200 
					.entity(damage)
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
	@Path("assign")
	@JWTTokenNeeded
	public Response assign(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		try {
			log.info("POST: assign");
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserIdFromJwtToken(jwtToken);
//			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			
			// Get the damage parameters
			int damageId = 0;
			if (queryParams.containsKey("damageId")) {
				damageId = Integer.parseInt(queryParams.getFirst("damageId"));
			} else {
				throw new Exception("No 'damageId' supplied");
			}
			
			int assignedUserId = 0;
			if (queryParams.containsKey("assignedUserId")) {
				assignedUserId = Integer.parseInt(queryParams.getFirst("assignedUserId"));
			} else {
				throw new Exception("No 'assignedUserId' supplied");
			}
			
			String comment = "";
			if (queryParams.containsKey("comment")) {
				comment = queryParams.getFirst("comment");
			} else {
				throw new Exception("No 'comment' supplied");
			}
			
			Damage damage = damageServices.assign(damageId, assignedUserId, comment, actionUserId);
			log.info("Damage Reported: " + gson.toJson(damage));

			return Response.status(Response.Status.OK) // 200 
						.entity(damage)
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
	@Path("close")
	@JWTTokenNeeded
	public Response close(@Context UriInfo uriInfo, @Context HttpHeaders httpHeader) {
		try {
			log.info("POST: assign");
			
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserIdFromJwtToken(jwtToken);
//			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			

			// Get the damage parameters
			int damageId = 0;
			if (queryParams.containsKey("damageId")) {
				damageId = Integer.parseInt(queryParams.getFirst("damageId"));
			} else {
				throw new Exception("No 'damageId' supplied");
			}
			
			String comment = "";
			if (queryParams.containsKey("comment")) {
				comment = queryParams.getFirst("comment");
			} else {
				throw new Exception("No 'comment' supplied");
			}
			
			Damage damage = damageServices.close(damageId, comment, actionUserId);
			log.info("Damage Reported: " + gson.toJson(damage));

			return Response.status(Response.Status.OK) // 200 
						.entity(damage)
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
