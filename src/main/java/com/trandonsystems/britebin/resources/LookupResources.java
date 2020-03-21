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
import com.trandonsystems.britebin.model.Locale;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.services.LookupServices;
import com.trandonsystems.britebin.services.UserServices;

@Path("lookup")
public class LookupResources {

	static Logger log = Logger.getLogger(UserResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	LookupServices lookupServices = new LookupServices();
	UserServices userServices = new UserServices();
	
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
	public Response getBinTypes(@Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			log.debug("locale: " + locale);
			
			List<BinType> binTypes = lookupServices.getBinTypes(locale);
						
			return Response.status(Response.Status.OK) // 200 
				.entity(gson.toJson(binTypes))
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
	public Response getBinContentTypes(@Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
									
			List<BinContentType> binContentTypes = lookupServices.getBinContentTypes(locale);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(gson.toJson(binContentTypes))
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
	public Response getCountries(@Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			
			List<Country> countries = lookupServices.getCountries(locale);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(gson.toJson(countries))
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
	@Path("getLocales")
	@JWTTokenNeeded
	public Response getLocales(@Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			
			List<Locale> locales = lookupServices.getLocales(locale);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(gson.toJson(locales))
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
	public Response getRoles(@Context HttpHeaders httpHeaders) {
		
		try {
			MultivaluedMap<String, String> queryHeaders = httpHeaders.getRequestHeaders();
			
			String authorization = queryHeaders.getFirst("Authorization");
			log.debug("authorization: " + authorization);
	
			String jwtToken = authorization.substring(7);
			log.debug("jwtToken: " + jwtToken);
	
			String locale = userServices.getUserLocaleFromJwtToken(jwtToken);
			
			List<Role> roles = lookupServices.getRoles(locale);
			
			return Response.status(Response.Status.OK) // 200 
				.entity(gson.toJson(roles))
				.build();
		
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("ERROR: " + ex.getMessage())
					.build();
		}	
	}
	
	
}
