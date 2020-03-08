package com.trandonsystems.britebin.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.services.UnitServices;;

@Path("unit")
public class UnitResources {

	static Logger log = Logger.getLogger(UserResource.class);

	UnitServices unitServices = new UnitServices();
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "User resource is working!";
    }
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("geUnitById/{userId}/{unitId}")
	public Unit getUnitById(@PathParam("userId") int userId, @PathParam("unitId") int unitId) {
		log.info("getUnitById(unitId)");
		return unitServices.getUnit(unitId);
	}

    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("geUnit/{userId}/{serialNo}")
	public Unit getUnit(@PathParam("userId") int userId, @PathParam("serialNo") String serialNo) {
		log.info("getUnit(serialNo)");
		return unitServices.getUnit(serialNo);
	}

    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnits/{userId}")
	public List<Unit> getUnits(@PathParam("userId") int userId) {
		log.info("getUnits()");
		return unitServices.getUnits();
	}
    	
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadingsById/{userId}/{unitId}")
	public List<UnitReading> getUnitReadingsByUnitId(@PathParam("userId") int userId, @PathParam("unitId") int unitId) {
		log.info("getUnitReadingsByUnitId(unitId)");
		return unitServices.getUnitReadings(unitId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{userId}/{serialNo}")
	public List<UnitReading> getUnitReadings(@PathParam("userId") int userId, @PathParam("serialNo") String serialNo) {
		log.info("getUnitReadings(serialNo)");
		return unitServices.getUnitReadings(serialNo);
	}

}
