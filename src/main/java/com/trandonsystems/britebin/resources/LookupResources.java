package com.trandonsystems.britebin.resources;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.ws.rs.GET;
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
import com.trandonsystems.britebin.auth.JsonWebToken;
import com.trandonsystems.britebin.model.BinContentType;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.services.LookupServices;

@Path("lookup")
public class LookupResources {

	static Logger log = Logger.getLogger(UserResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
		log.info("Lookup resources is working");
        return "Lookup resource is working!";
    }
    
    @GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getBinTypes")
	@JWTTokenNeeded
	public Response getBinTypes(@Context UriInfo ui, @Context HttpHeaders hh) {
		
		try {
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
			
			List<BinType> binTypes = new ArrayList<BinType>();
			if (queryParams.containsKey("locale")) {
				String locale = queryParams.getFirst("locale");
				log.debug("locale: " + locale);
				
				LookupServices lookupServices = new LookupServices();
				binTypes = lookupServices.getBinTypes(locale);
			} 
			
			// Get a new token
			String newToken = JsonWebToken.verify(jwtToken);		
			
			String json = Json.createObjectBuilder()
					.add("token", newToken)
					.add("binTypes", gson.toJson(binTypes))
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
	@Path("getBinContentTypes")
	@JWTTokenNeeded
	public Response getBinContentTypes(@Context UriInfo ui, @Context HttpHeaders hh) {
		
		try {
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
			
			List<BinContentType> binContentTypes = new ArrayList<BinContentType>();
			if (queryParams.containsKey("locale")) {
				String locale = queryParams.getFirst("locale");
				log.debug("locale: " + locale);
				
				LookupServices lookupServices = new LookupServices();
				binContentTypes = lookupServices.getBinContentTypes(locale);
			} 
			
			// Get a new token
			String newToken = JsonWebToken.verify(jwtToken);		
			
			String json = Json.createObjectBuilder()
					.add("token", newToken)
					.add("binContentTypes", gson.toJson(binContentTypes))
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
	@Path("getCountries")
	@JWTTokenNeeded
	public Response getCountries(@Context UriInfo ui, @Context HttpHeaders hh) {
		
		try {
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
			
			List<Country> countries = new ArrayList<Country>();
			if (queryParams.containsKey("locale")) {
				String locale = queryParams.getFirst("locale");
				log.debug("locale: " + locale);
				
				LookupServices lookupServices = new LookupServices();
				countries = lookupServices.getCountries(locale);
			} 
			
			// Get a new token
			String newToken = JsonWebToken.verify(jwtToken);		
			
			String json = Json.createObjectBuilder()
					.add("token", newToken)
					.add("countries", gson.toJson(countries))
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
	@Path("getRoles")
	@JWTTokenNeeded
	public Response getRoles(@Context UriInfo ui, @Context HttpHeaders hh) {
		
		try {
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			MultivaluedMap<String, String> queryHeaders = hh.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
			
			List<Role> roles = new ArrayList<Role>();
			if (queryParams.containsKey("locale")) {
				String locale = queryParams.getFirst("locale");
				log.debug("locale: " + locale);
				
				LookupServices lookupServices = new LookupServices();
				roles = lookupServices.getRoles(locale);
			} 
			
			// Get a new token
			String newToken = JsonWebToken.verify(jwtToken);		
			
			String json = Json.createObjectBuilder()
					.add("token", newToken)
					.add("roles", gson.toJson(roles))
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
