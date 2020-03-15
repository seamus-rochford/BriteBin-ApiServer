package com.trandonsystems.britebin.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

@Path("TestServer")
public class TestServer {

	static Logger log = Logger.getLogger(TestServer.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
    	log.info("API Server is working!");
        return "API Server is working!";
    }
}
