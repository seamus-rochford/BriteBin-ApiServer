package com.trandonsystems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.auth.JsonWebToken;
import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.database.UserDAL;
import com.trandonsystems.britebin.model.UnitReading;
import com.trandonsystems.britebin.model.User;
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

import org.apache.log4j.Logger;


public class Test {

	static ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
	static Logger log = Logger.getLogger(Test.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
	}
	

	public static void testJwtToken() {
		User user = new User();
		user.email = "tommy@pelmfg.com";
		user.password = "1234566789766";
		
		UserServices userServices = new UserServices();
		int errorCode = userServices.loginUser(user);
		System.out.println(errorCode);
		
		user = userServices.getUser("tommy@pelmfg.com");
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
		UserServices us = new UserServices();
		
		User user = new User();
		user.email = "tommy@pelmfg.com";
		user.password = "tommy";
		
		int result = us.loginUser(user);
		log.info("Login result: " + result);
		log.info(gson.toJson(user));
	}
	
	private static void testPasswordEncryption() {
		String passwordEncrypted = passwordEncryptor.encryptPassword("colm");
		System.out.println(passwordEncrypted);
		boolean result = UserDAL.passwordMatch("seamus", passwordEncrypted);
		System.out.println("Password Matches: " + result);
	}
	
	private static void testUnitReadings() {
		
		UnitServices us = new UnitServices();
		List<UnitReading> list = us.getUnitReadings("0861075021004552");
		
		log.info("Readings: ");
		log.info(gson.toJson(list));
	}
	
	public static void main(String[] args) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

//		testInstantFormating();
//		testEnvVariables();
//		testLogin();
//		testJwtToken();
		testPasswordEncryption();
//	
//		testUnitReadings();

	}

}
