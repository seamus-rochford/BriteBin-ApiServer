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
        return "Unit resource is working!";
    }
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnit/{parentId}/unitId/{unitId}")
	public Unit getUnitById(@PathParam("parentId") int parentId, @PathParam("unitId") int unitId) {
		log.info("getUnitById(parentId, unitId)");
		return unitServices.getUnit(parentId, unitId);
	}

    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnit/{parentId}/serialNo/{serialNo}")
	public Unit getUnit(@PathParam("parentId") int parentId, @PathParam("serialNo") String serialNo) {
		log.info("getUnit(parentId, serialNo)");
		return unitServices.getUnit(parentId, serialNo);
	}

    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnits/{parentId}")
	public List<Unit> getUnits(@PathParam("parentId") int parentId) {
		log.info("getUnits()");
		return unitServices.getUnits(parentId);
	}
    	
    
	// This one was created for engineers testing units
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{serialNo}")
	public List<UnitReading> getUnitReadings(@PathParam("serialNo") String serialNo) {
		log.info("getUnitReadings(serialNo)");
		return unitServices.getUnitReadings(1, serialNo);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{serialNo}/limit/{limit}")
	public List<UnitReading> getUnitReadings(@PathParam("serialNo") String serialNo, @PathParam("limit") int limit) {
		log.info("getUnitReadings(serialNo, limit)");
		return unitServices.getUnitReadings(1, serialNo, limit);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{parentId}/unitId/{unitId}")
	public List<UnitReading> getUnitReadingsByUnitId(@PathParam("parentId") int parentId, @PathParam("unitId") int unitId) {
		log.info("getUnitReadingsByUnitId(unitId)");
		return unitServices.getUnitReadings(parentId, unitId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUnitReadings/{parentId}/serialNo/{serialNo}")
	public List<UnitReading> getUnitReadings(@PathParam("parentId") int parentId, @PathParam("serialNo") String serialNo) {
		log.info("getUnitReadings(serialNo)");
		return unitServices.getUnitReadings(parentId, serialNo);
	}

}
