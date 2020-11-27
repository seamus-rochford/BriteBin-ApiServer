package com.trandonsystems.britebin.auth;


import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import org.apache.log4j.Logger;

@Provider
@BriteBinApiKeyNeeded
@Priority(Priorities.AUTHENTICATION)
public class BriteBinApiKeyNeededFilter implements ContainerRequestFilter {

	static String DEFAULT_BRITEBIN_API_KEY = "a89f4e43-acf1-4c5d-9721-cc643d78d8db";
	
    static Logger log = Logger.getLogger(BriteBinApiKeyNeededFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

    	// Uncomment the next line to allow through all tokens
//    	return;
    	
        // Get the BRITEBIN_API_KEY HTTP Authorization header from the request
        String briteBinApiKey = requestContext.getHeaderString("BRITEBIN_API_KEY");
//        log.info("#### briteBinApiKey : " + briteBinApiKey);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (briteBinApiKey == null) {
        	log.error("#### invalid briteBinApiKey : " + briteBinApiKey);
            throw new NotAuthorizedException("BRITEBIN_API_KEY header must be provided");
        }

        try {
            // Validate the briteBinApiKey
        	String envBriteBinApiKey = System.getenv("BRITEBIN_API_KEY");
        	if (envBriteBinApiKey == null) {
        		log.info("<<BRITEBIN_API_KEY>> not defined in the current environment - using default BRITEBIN_API_KEY");
        		envBriteBinApiKey = DEFAULT_BRITEBIN_API_KEY;
        	}
        	
        	if (!briteBinApiKey.contentEquals(envBriteBinApiKey)) {
        		throw new Exception();
        	}
        	
//        	log.info("#### valid token : " + briteBinApiKey);

        } catch (Exception e) {
        	log.error("#### invalid token : " + briteBinApiKey);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid BRITEBIN_API_KEY").build());
        }
    }
}
