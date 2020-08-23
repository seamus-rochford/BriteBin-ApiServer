package com.trandonsystems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JsonWebToken;
import com.trandonsystems.britebin.database.SystemDAL;
import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.database.UserDAL;
import com.trandonsystems.britebin.database.Util;
import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.Damage;
import com.trandonsystems.britebin.model.KeyValue;
import com.trandonsystems.britebin.model.SigfoxBody;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.model.User;
import com.trandonsystems.britebin.resources.SystemResources;
import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.DamageServices;
import com.trandonsystems.britebin.services.SigfoxServices;
import com.trandonsystems.britebin.services.UnitServices;
import com.trandonsystems.britebin.services.UserServices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.jasypt.util.password.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.json.Json;

import org.apache.log4j.Logger;


public class Test {

	static ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
	static Logger log = Logger.getLogger(Test.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	static UserServices us = new UserServices();

	private static void testLogging() {
		
		log.trace("Trace logging");
		log.debug("Debug logging");
		log.info("Info logging");
		log.warn("Warn logging");
		log.error("Error logging");
		log.fatal("Fatal logging");
	}
	
	private static void testInstantFormating() {
	
		DateTimeFormatter formatter =
			    DateTimeFormatter.ofPattern("HH:mm:ss")
			                     .withLocale( Locale.UK )
			                     .withZone( ZoneId.systemDefault() );

		Instant instant = Instant.now();
		String output = formatter.format( instant );
		
		System.out.println("formatter: " + formatter + " with zone: " + formatter.getZone() + " and Locale: " + formatter.getLocale() );
		System.out.println("instant: " + instant );
		System.out.println("output: " + output );				
	}
	
	private static void testEnvVariables() {
		log.info("Test starting ...new ...");
		log.info("Catalina Home: "  + System.getenv("{catalina.home}"));

		log.info("ENV_NAME: " + System.getenv(("{ENV_HOME}")));
		log.info("BRITEBIN_API_KEY: " + System.getenv(("{BRITEBIN_API_KEY}")));
		log.info("System Env Variables: " + System.getenv());
	}
	

	public static void testJwtToken() {
		User user = new User();
		user.email = "tommy@pelmfg.com";
		user.password = "1234566789766";
		
		UserServices userServices = new UserServices();
		int errorCode = userServices.loginUser(user);
		System.out.println(errorCode);
		
		user = userServices.getUser(1, "tommy@pelmfg.com");
		System.out.println("User: " + gson.toJson(user));		
		
		String jwtToken2 = JsonWebToken.createJWT(user);
		System.out.println("JWT: " + jwtToken2);		
		
		try {
			Claims jwtClaims2 = JsonWebToken.decodeJWT(jwtToken2);
			
	        System.out.println("Id: " + jwtClaims2.getId());
	        System.out.println("Sub: " + jwtClaims2.getSubject());
	        System.out.println("Iss: " + jwtClaims2.getIssuer());
	        System.out.println("At: " + jwtClaims2.getIssuedAt());
	        System.out.println("Exp: " + jwtClaims2.getExpiration());
	        System.out.println("Fname: " + jwtClaims2.get("fname"));
	        System.out.println("Lname: " + jwtClaims2.get("lname"));
	        System.out.println("Tel: " + jwtClaims2.get("tel"));
	        System.out.println("EntityId: " + jwtClaims2.get("EntityId"));
	        System.out.println("RoleId: " + jwtClaims2.get("RoleId"));
	        System.out.println("Status: " + jwtClaims2.get("Status"));
	        
	        System.out.println("jwtClaims2: " + jwtClaims2);
			System.out.println("JSON: " + gson.toJson(jwtClaims2));
		
		}
		catch (ExpiredJwtException e) {
			System.out.println("Token expired exception");
		}
		catch (UnsupportedJwtException e) {
			System.out.println("Token unsupported exception");
		}
		catch (MalformedJwtException e) {
			System.out.println("Token malformed exception");
		}
		catch (SignatureException e) {
			System.out.println("Token signature exception");
		}
		catch (IllegalArgumentException e) {
			System.out.println("Token illegal exception");
		}
	}

	private static void testLogin( ) {
		
		User user = new User();
		user.email = "corkcc@example.com";
		user.password = "seamus";
		
		int result = us.loginUser(user);
		log.info("Login result: " + result);
		log.info(gson.toJson(user));
	}

	
	private static void testPasswordEncryption() {
		// Passowrd set at the moment
		// tommy = tommy
		// colm = colm
		// lorand = lorandK
		// seamus = Rebel1
		// tomislav = tom
		String passwordEncrypted = UserDAL.encryptPassword("tommy");
		System.out.println(passwordEncrypted);
		boolean result = UserDAL.passwordMatch("tommy", passwordEncrypted);
		System.out.println("Password Matches: " + result);

		String encryptedStr = Util.MD5("tommy");
		System.out.println(encryptedStr);
	
	}
	
	private static void testUsers() {
	
		UserServices us = new UserServices();	
		List<User> users = us.getUsers(1);
		
		log.info("Users: ");
		log.info(gson.toJson(users));
	}
	
	private static void testUnitReadings() {
		
		UnitServices us = new UnitServices();
		List<UnitReading> list = us.getUnitReadings(1, "0861075021004551", -1);
		
		log.info("Readings: ");
		log.info(gson.toJson(list));
	}
	
	private static String getRandomGuid() {
		String guid = UUID.randomUUID().toString();
		
		log.info("Guid: " + guid);
		return guid;
	}
	
	private static void testSaveData() {
		SigfoxBody sigfoxData = new SigfoxBody();
		
		sigfoxData.deviceID = "X1Y2Z3W4";
		sigfoxData.rssi = "123";
		sigfoxData.snr = "11";
//		data.payload = "01284800554466345A760000";
		sigfoxData.data = "019A0000007e1b00FF000000";
		
		String dataStr = gson.toJson(sigfoxData.data);
		
		log.info("Data: " + gson.toJson(sigfoxData.data));
		
		try {	
			SigfoxServices ss = new SigfoxServices();
			ss.saveData("X1Y2Z3W4", sigfoxData);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
	
	private static void testInstallUnit() {

		Unit unit = new Unit();
		unit.serialNo = "X1Y2Z3W4";
		unit.latitude = 1234.56;
		unit.longitude = 678.34;
		unit.location = "somewhere";
		
		try {	
			UnitServices us = new UnitServices();
			us.install(unit, 5);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
	
	private static void testSaveUser() {

		User user = us.getUser(1, 6);
		
		log.info("Data: " + gson.toJson(user));
		
		user.id = 0;
		user.name = "Rochford Seamus";
		user.password = "seamus";
		try {	
			user = us.save(user, 1);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
	
	private static void testAlertService() {
		
		AlertServices as = new AlertServices();
		
		List<Alert> list = as.getAdminAlerts(22);
		
		System.out.println(gson.toJson(list));
	}
	
	private static String BuildJson() {
		String innerJson = Json.createObjectBuilder()
				.add("downlinkData", "ABCDEF01")
				.build()
				.toString();
		
		String json = Json.createObjectBuilder()
				.add("deviceId", innerJson)
				.build()
				.toString();	
		
		json = "{'deviceId': {'downlinkData':'ABABABAB'}}";
		
		return json;
	}
	
	public static void main(String[] args) {

		String msg = BuildJson();
		System.out.println(msg);
		
//		int percent = UnitDAL.computePercentagePelBin(1, 28);
//		log.info("Level 28: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 29);
//		log.info("Level 29: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 35);
//		log.info("Level 35: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 52);
//		log.info("Level 52: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 53);
//		log.info("Level 53: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 54);
//		log.info("Level 54: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 100);
//		log.info("Level 100: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 156);
//		log.info("Level 156: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(1, 160);
//		log.info("Level 160: " + percent + " %");
//
//		log.info("BinType 2 & 3:");
//		percent = UnitDAL.computePercentagePelBin(2, 145);
//		log.info("Level 145: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(2, 140);
//		log.info("Level 140: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(2, 90);
//		log.info("Level 90: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(2, 42);
//		log.info("Level 42: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(2, 30);
//		log.info("Level 30: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(2, 21);
//		log.info("Level 21: " + percent + " %");
//
//		percent = UnitDAL.computePercentagePelBin(2, 18);
//		log.info("Level 18: " + percent + " %");

		
//		int percent = UnitDAL.computePercentageTekelek(2, 150);
//		log.info("Level 150: " + percent + " %");
//
//		percent = UnitDAL.computePercentageTekelek(2, 100);
//		log.info("Level 100: " + percent + " %");
//
//		percent = UnitDAL.computePercentageTekelek(2, 75);
//		log.info("Level 75: " + percent + " %");
//
//		percent = UnitDAL.computePercentageTekelek(2, 73);
//		log.info("Level 73: " + percent + " %");
//
//		percent = UnitDAL.computePercentageTekelek(2, 72);
//		log.info("Level 72: " + percent + " %");
//
//		percent = UnitDAL.computePercentageTekelek(2, 45);
//		log.info("Level 45: " + percent + " %");
//
//		percent = UnitDAL.computePercentageTekelek(2, 18);
//		log.info("Level 18: " + percent + " %");
//
//		percent = UnitDAL.computePercentageTekelek(2, 16);
//		log.info("Level 16: " + percent + " %");

		
//		try {
//			DamageServices damageServices = new DamageServices();
//		
//			Damage damage = damageServices.getDamage(15, 1);
//			
//			log.info("Damage: " + gson.toJson(damage));
//			
//			log.info("First history: " + gson.toJson(damage.damageHistory.get(0)));
//			
//		} catch(Exception ex) {
//			log.error(ex.getMessage());
//		}
		
		
//		String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2MyIsImlhdCI6MTU5NDA3NzI4Mywic3ViIjoidG9tLmhvc29pZW5AZW52aXJvcGFjLm5vIiwiaXNzIjoiYnJpdGViaW4uY29tIiwibmFtZSI6IkVudmlyUGFjIEdyb3VwIEVuZ2luZWVyIiwicm9sZSI6IjQiLCJlbWFpbCI6InRvbS5ob3NvaWVuQGVudmlyb3BhYy5ubyIsInBhcmVudCI6IjYxIiwic3RhdHVzIjoiY29tLnRyYW5kb25zeXN0ZW1zLmJyaXRlYmluLm1vZGVsLlVzZXJTdGF0dXNAN2Y2MmI3MWUiLCJsb2NhbGUiOnsiYWJiciI6ImVuLUlFIiwibmFtZSI6IkVuZ2xpc2ggKElyZWxhbmQpIn0sImV4cCI6MTU5NDA4NDQ4M30.oDIIw4azXJ3N7bwRwuGM3ywj_VOMYw8QZSqWkxsAGk4";
//		log.debug("jwtToken: " + jwtToken);
//
//		UserServices userServices = new UserServices();
//		int actionUserId = userServices.getUserFilterIdFromJwtToken(jwtToken);
//		log.debug("actionUserId: " + actionUserId);
//		testAlertService();
		
//		testSaveData();
		
//		testLogging();
		
//		testInstantFormating();
//		testEnvVariables();
//		testLogin();
//		testJwtToken();
//		getRandomGuid();
//		testPasswordEncryption();

	
//		testUsers();
		
//		testUnitReadings();

	}

}
