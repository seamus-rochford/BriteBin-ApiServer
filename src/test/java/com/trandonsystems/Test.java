package com.trandonsystems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JsonWebToken;
import com.trandonsystems.britebin.database.SystemDAL;
import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.database.UserDAL;
import com.trandonsystems.britebin.database.Util;
import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.Damage;
import com.trandonsystems.britebin.model.KeyValue;
import com.trandonsystems.britebin.model.RawData;
import com.trandonsystems.britebin.model.SigfoxBody;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.model.User;
import com.trandonsystems.britebin.resources.SystemResources;
import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.DamageServices;
import com.trandonsystems.britebin.services.Hex;
import com.trandonsystems.britebin.services.SigfoxServices;
import com.trandonsystems.britebin.services.UnitServices;
import com.trandonsystems.britebin.services.UserServices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.jasypt.util.password.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Base64;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class Test {

	static ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
	static Logger log = Logger.getLogger(Test.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	static UserServices us = new UserServices();
	static DamageServices damageServices = new DamageServices();

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
		List<User> users = us.getUsers(1, false);
		
		log.info("Users: ");
		log.info(gson.toJson(users));
	}
	
	private static void testUnitReadings() {
		
		try {
		UnitServices us = new UnitServices();
		List<UnitReading> list = us.getUnitReadings(1, "0861075021004551", -1);
		
		log.info("Readings: ");
		log.info(gson.toJson(list));
		} catch(Exception ex) {
			log.error(ex.getMessage());
		}
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
	
	private static void processRawData() {
		UnitServices unitServices = new UnitServices();
		SigfoxServices sigfoxServices = new SigfoxServices();
		
		String source = ""; 
		List<RawData> readings = new ArrayList<RawData>();
		
		try {
			source = "Sigfox";
//    		readings = unitServices.getUnprocessedRawData(source);
    		
//    		for (int i = 0; i < readings.size(); i++) {
//    			int rawDataId = readings.get(i).id;
//    			Instant readingDateTime = readings.get(i).insertAt;
//    			
//    			String sigfoxStr = new String(readings.get(i).rawData, "UTF-8");
//    			SigfoxBody sigfoxBody = gson.fromJson(sigfoxStr, SigfoxBody.class);
//
//    			sigfoxServices.saveRawDataOnly(rawDataId, readingDateTime, sigfoxBody);
//    		}
			
			source = "NB-IoT BB";
			readings = unitServices.getUnprocessedRawData(source);
			log.info(gson.toJson(readings));
			
    		for (int i = 0; i < readings.size(); i++) {
    			int rawDataId = readings.get(i).id;
    			Instant readingDateTime = readings.get(i).insertAt;
    			byte[] reading = readings.get(i).rawData;
    			
    			unitServices.processBriteBinDataOnly(source, rawDataId, readingDateTime, reading);
    		}
    		
			source = "NB-IoT Tek";
    		readings = unitServices.getUnprocessedRawData(source);
			log.info(gson.toJson(readings));
    		
    		for (int i = 0; i < readings.size(); i++) {
    			int rawDataId = readings.get(i).id;
    			Instant readingDateTime = readings.get(i).insertAt;
    			byte[] reading = readings.get(i).rawData;
    			
    			unitServices.processTekelekDataOnly(source, rawDataId, readingDateTime, reading);
    		}    		
			 
		} catch (Exception ex) {
			 log.error(ex.getMessage());
		}
		
	}
	
	public static String getBase64(String inStr) {
		
		byte[] bytesEncoded = inStr.getBytes(StandardCharsets.UTF_8);
		
		String base64 = new String(Base64.getEncoder().encodeToString(bytesEncoded));
		
		return base64;
	}
	
	public static void testInstantDiff() {
		int hour = 20;
		int minute = 30;
		int second = 48;
		
		Instant now = Instant.now();
		System.out.println(now);
		
		Instant unitTime = now.atZone(ZoneOffset.UTC).withHour(hour).withMinute(minute).withSecond(second).toInstant();
		System.out.println(unitTime);
		
		long diff = unitTime.getEpochSecond() - now.getEpochSecond();
		System.out.println("Offset difference: " + diff);
		
		return;
	}
	
	private static void sendSMS() {
		
    	PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    	CloseableHttpClient httpClient = HttpClients.custom()
    			.setConnectionManager(connManager)
    			.build();
    	
    	String url = "http://multi.mobile-gw.com:9000/v1/omni/message";
    	HttpPost httpPost = new HttpPost(url);
    	httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Basic ZGVtbzc3NzduOj83Jm1OYnE2");
    	httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    	httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
    	
    	JsonArray channels = Json.createArrayBuilder()
    			.add("SMS")
    			.build();
    	
    	JsonObject phoneNo = Json.createObjectBuilder()
    			.add("phoneNumber", "35387264637")
    			.build();
    	JsonArray destinations = Json.createArrayBuilder()
    			.add(phoneNo)
    			.build();
    	
    	JsonObject smsSender = Json.createObjectBuilder()
    			.add("sender", "BriteBin")
    			.add("text", "test message from britebin")
    			.build();
    	
    	JsonObject reqBody = Json.createObjectBuilder()
    			.add("channels", channels)
    			.add("destination", destinations)
    			.add("transactionId", "1782")
    			.add("dir", true)
    			.add("dlrUrl", "http://10.253.40.99:8080/BriteBin/api/sms")
    			.add("tag", "bin full alert")
    			.add("sms", smsSender)
    			.build();
    	System.out.println("\nRequest Body: " + reqBody.toString());
    	
    	httpPost.setEntity(new StringEntity(reqBody.toString(), ContentType.APPLICATION_JSON));
    	
//    	try {
//    		HttpResponse response = httpClient.execute(httpPost);
//    		
//    		System.out.println("\nHttp Response: " + response.toString());
//    		
//    		int respStatus = response.getStatusLine().getStatusCode();
//    		System.out.println("\nResponse Code: " + respStatus);
//    		
//    		String resultStr = EntityUtils.toString(response.getEntity());
//    		System.out.println("\nResponse Body: " + resultStr);
//    		
//    	} catch (ClientProtocolException ex) {
//    		log.error("HTTP Client Protocol Error: " + ex.getMessage());
//    	} catch (IOException exIO) {
//    		log.error("HTTP IO Error: " + exIO.getMessage());
//    	} 		
	}
		
	private static void testLoadImage() {
		
		String uploadImageFileName = "D:\\temp\\betty_license_back.jpg";
		
		File file = new File("D:\\temp\\betty_license_back.jpg");
		System.out.println("File Size : " + file.length());
		
		try {
			Damage damage = damageServices.report(3, 1, "test comment", 1, true, uploadImageFileName);
			log.info("Damage Reported: " + gson.toJson(damage));
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		
	}

	public static void testSchedulingTask() {
	    TimerTask repeatedTask = new TimerTask() {
	        public void run() {
	            System.out.println("Task performed on " + new Date());
	        }
	    };
	    Timer timer = new Timer("Timer");
	    
	    long delay = 1000L;
	    long period = 1000L * 5L;
	    timer.scheduleAtFixedRate(repeatedTask, delay, period);
	}
	
	
	public static void main(String[] args) {

		int number = 78;
		System.out.println("Number: "+ number + "   Hex (2 digit): " + Hex.IntToHex(78, 2));

		BinType tekBinType = new BinType();
		tekBinType.id = 1;
		
		int percent = 95;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 90;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 85;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 80;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 75;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		
		tekBinType.id = 2;
		percent = 95;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 90;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 85;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 80;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		percent = 75;
		System.out.println("Tekelek bin Type: " + tekBinType + " percentage: " + percent + "% = value: " + UnitDAL.computeReadingFromPercentTekelek(tekBinType, percent));
		
		int value = 60;
		BinType binType = new BinType();
		binType.id = 2;
		
		int level = UnitDAL.computePercentageTekelek(binType, value);
		System.out.println("Level " + value + " for binType " + binType + ": " + level + "%");
		
		value = 34;
		level = UnitDAL.computePercentageTekelek(binType, value);
		System.out.println("Level " + value + " for binType " + binType + ": " + level + "%");
		
		
		value = 76;
		binType.id = 2;
		level = UnitDAL.computePercentageTekelek(binType, value);
		System.out.println("Level " + value + " for binType " + binType + ": " + level + "%");
		
		value = 0;
		level = UnitDAL.computePercentageTekelek(binType, value);
		System.out.println("Level " + value + " for binType " + binType + ": " + level + "%");
				
//		String msg = BuildJson();
//		System.out.println(msg);

//		testSchedulingTask();
		
//		try {
//			String result = PushNotificationHelper.sendPushNotification("00353872646379");
//			System.out.println("Result: " + result);
//		} catch(Exception ex) {
//			System.out.println("ERROR: " + ex.getMessage());
//		}
		
//		testLoadImage();
		
//		String str = "demo7777n" + ":" + "?7&mNbq6";
//		String result = getBase64(str);
//		System.out.println(result);
		
//		sendSMS();
		
//		testInstantDiff();
		
//		String encodedBase64 = getBase64("demo7777n:?7&mNbq6");
//		System.out.println(encodedBase64);
		
//		processRawData();
		
		log.info("");
		log.info("=================================================================================================");
		log.info("Pel Bin - BinType 1");
		log.info("");
		
		binType.id = 1;
		percent = UnitDAL.computePercentagePelBin(binType, 28);
		log.info("Level 28: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 29);
		log.info("Level 29: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 35);
		log.info("Level 35: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 52);
		log.info("Level 52: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 53);
		log.info("Level 53: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 54);
		log.info("Level 54: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 100);
		log.info("Level 100: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 156);
		log.info("Level 156: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 160);
		log.info("Level 160: " + percent + " %");

		log.info("");
		log.info("=================================================================================================");
		log.info("Pel Bin - BinType 2 & 3");
		log.info("");		

		binType.id = 2;
		percent = UnitDAL.computePercentagePelBin(binType, 145);
		log.info("Level 145: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 140);
		log.info("Level 140: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 90);
		log.info("Level 90: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 42);
		log.info("Level 42: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 30);
		log.info("Level 30: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 21);
		log.info("Level 21: " + percent + " %");

		percent = UnitDAL.computePercentagePelBin(binType, 18);
		log.info("Level 18: " + percent + " %");

		
		log.info("");
		log.info("=================================================================================================");
		log.info("Tekelek - BinType 1");
		log.info("");
		
		binType.id = 1;
		percent = UnitDAL.computePercentageTekelek(binType, 150);
		log.info("Level 150: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 100);
		log.info("Level 100: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 75);
		log.info("Level 75: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 73);
		log.info("Level 73: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 72);
		log.info("Level 72: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 60);
		log.info("Level 72: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 45);
		log.info("Level 45: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 34);
		log.info("Level 18: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 18);
		log.info("Level 18: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 16);
		log.info("Level 16: " + percent + " %");
		
		
		log.info("");
		log.info("=================================================================================================");
		log.info("Tekelek - BinType 2 ");
		log.info("");
		
		binType.id = 2; 
		percent = UnitDAL.computePercentageTekelek(binType, 150);
		log.info("Level 150: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 100);
		log.info("Level 100: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 75);
		log.info("Level 75: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 73);
		log.info("Level 73: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 72);
		log.info("Level 72: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 45);
		log.info("Level 45: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 18);
		log.info("Level 18: " + percent + " %");

		percent = UnitDAL.computePercentageTekelek(binType, 16);
		log.info("Level 16: " + percent + " %");

		
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
