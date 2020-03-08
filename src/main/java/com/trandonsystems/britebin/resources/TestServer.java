package com.trandonsystems.britebin.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("TestServer")
public class TestServer {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "API Server is working!";
    }
}
