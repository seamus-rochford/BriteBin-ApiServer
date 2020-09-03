package com.trandonsystems.britebin.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.trandonsystems.britebin.model.sms.SmsDeliveryReport;
import com.trandonsystems.britebin.services.SmsServices;


@Path("sms")
public class SmsResources {

	static Logger log = Logger.getLogger(UnitResources.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dlr")
	public Response deliveryReport(SmsDeliveryReport report) {
		log.info("POST: deliveryReport");
		log.info("Delivery Report: " + gson.toJson(report));
		
		SmsServices smsServices = new SmsServices();
		
		try {
			smsServices.processSmsDeliveryReport(report);
			
		} catch (Exception ex) {
			log.error("Api sms/dlr Exception: " + ex.getMessage());
			// because this is called from an external where they want a response 200 - we simply throw away the error
			// We should still try an solve an issue that cause this error
		}

		return Response.status(Response.Status.OK) // 200 
				.build();
			
	}
	
}
