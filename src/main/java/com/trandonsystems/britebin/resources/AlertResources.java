package com.trandonsystems.britebin.resources;

import java.util.ArrayList;
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

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JWTTokenNeeded;
import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.UserServices;

@Path("alert")
public class AlertResources {

	static Logger log = Logger.getLogger(AlertResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	UserServices userServices = new UserServices();
	AlertServices alertServices = new AlertServices();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAdminAlerts")
	@JWTTokenNeeded
	public Response getAdminAlerts(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Get userId from JwtToken (Note: if the role is driver or technician it will return the parentId)
//			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			int role = userServices.getUserRoleFromJwtToken(jwtToken);
			
			int customerId = 0;
			if (queryParams.containsKey("customerId")) {
				customerId = Integer.parseInt(queryParams.getFirst("customerId"));
				log.debug("customerId: " + customerId);
			} else {
				throw new Exception("customerId required for <getAdminAlerts>");
			}
			
			List<Alert> alerts = new ArrayList<Alert>();			
			if (role == 0) {
				alerts = alertServices.getAdminAlerts(customerId);
			}
			
			return Response.status(Response.Status.OK) // 200 
				.entity(alerts)
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
	@Path("getDistributorAlerts")
	@JWTTokenNeeded
	public Response getDistributorAlerts(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Get userId from JwtToken (Note: if the role is driver or technician it will return the parentId)
//			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			int role = userServices.getUserRoleFromJwtToken(jwtToken);
			
			int customerId = 0;
			if (queryParams.containsKey("customerId")) {
				customerId = Integer.parseInt(queryParams.getFirst("customerId"));
				log.debug("customerId: " + customerId);
			} else {
				throw new Exception("customerId required for <getAdminAlerts>");
			}
			
			List<Alert> alerts = new ArrayList<Alert>();			
			if (role <= 1) {
				alerts = alertServices.getDistributorAlerts(customerId);
			}
			
			return Response.status(Response.Status.OK) // 200 
				.entity(alerts)
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
	@Path("getTechnicianAlerts")
	@JWTTokenNeeded
	public Response getTechnicianAlerts(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			// Get userId from JwtToken (Note: if the role is driver or technician it will return the parentId)
//			int userFilterId = userServices.getUserFilterIdFromJwtToken(jwtToken);
			
			int userRoleId = userServices.getUserRoleFromJwtToken(jwtToken);
			
			int customerId = 0;
			if (queryParams.containsKey("customerId")) {
				customerId = Integer.parseInt(queryParams.getFirst("customerId"));
				log.debug("customerId: " + customerId);
			} else {
				throw new Exception("customerId required for <getAdminAlerts>");
			}
			
			List<Alert> alerts = alertServices.getAlertsTechnicians(customerId, userRoleId);			
			
			return Response.status(Response.Status.OK) // 200 
				.entity(alerts)
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
	@Path("getCorporateAlerts")
	@JWTTokenNeeded
	public Response getCorporateAlerts(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int customerId = 0;
			if (queryParams.containsKey("customerId")) {
				customerId = Integer.parseInt(queryParams.getFirst("customerId"));
				log.debug("customerId: " + customerId);
			} else {
				throw new Exception("customerId required for <getAdminAlerts>");
			}
			
			List<Alert> alerts = alertServices.getAlertsCorporate(customerId);			

			return Response.status(Response.Status.OK) // 200 
				.entity(alerts)
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
	@Path("getCustomerAlerts")
	@JWTTokenNeeded
	public Response getCustomerAlerts(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int customerId = 0;
			if (queryParams.containsKey("customerId")) {
				customerId = Integer.parseInt(queryParams.getFirst("customerId"));
				log.debug("customerId: " + customerId);
			} else {
				throw new Exception("customerId required for <getAdminAlerts>");
			}
			
			List<Alert> alerts = alertServices.getAlertsCustomer(customerId);;			

			return Response.status(Response.Status.OK) // 200 
				.entity(alerts)
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
	@Path("getDriverAlerts")
	@JWTTokenNeeded
	public Response getDriverAlerts(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int customerId = 0;
			if (queryParams.containsKey("customerId")) {
				customerId = Integer.parseInt(queryParams.getFirst("customerId"));
				log.debug("customerId: " + customerId);
			} else {
				throw new Exception("customerId required for <getAdminAlerts>");
			}
			
			List<Alert> alerts = alertServices.getAlertsDrivers(customerId);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(alerts)
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
	@Path("saveAlerts")
	@JWTTokenNeeded
	public Response saveAlerts(@Context HttpHeaders httpHeader, List<Alert> alerts) {

		try {
			log.info("POST: save alerts");
			log.info("Alerts: " + gson.toJson(alerts));
			
			MultivaluedMap<String, String> queryHeaders = httpHeader.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);

			alerts = alertServices.saveAlerts(alerts, actionUserId);
			log.info("Saved alert: " + gson.toJson(alerts));

			String json = Json.createObjectBuilder()
									.add("alerts", gson.toJson(alerts))
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
