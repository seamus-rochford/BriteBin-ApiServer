package com.trandonsystems.britebin.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;


import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.User;

// JWTs can be cryptographically signed (making it a JWS) or encrypted (making it a JWE)
// This is what we return from createJWT

public class JsonWebToken {

	static Logger log = Logger.getLogger(JsonWebToken.class);

    //The JWT signature algorithm we will be using to sign the token
    private static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	private static String SECRET_KEY = "BriteBin-PEL-20200229";
	private static String ISSUER = "britebin.com";
	
	private static long DEFAULT_TIMEOUT_MILLI_SECONDS = 6 * 20 * 60 * 1000;  // 20 minutes
    
    public JsonWebToken() {
    }
    
    private static Key getSigningKey() {
    	
        // Sign our JWT token with our ApiKey secret
    	log.info("getSigningKey");
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        
    	log.info("getSigningKey - apiKeySecretBytes created");
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }
    
    public static String createJWT(String id, String subject, long ttlMillis) {
    	  
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        Key signingKey = getSigningKey();

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .signWith(signatureAlgorithm, signingKey);
      
        // if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }  
      
        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }    

    public static String createJWT(User user) {
  	  
        long nowMillis = System.currentTimeMillis();
        Date now = new Date();
        System.out.println("Now: " + now);

        // Create Signing Key - We will sign our JWT with our ApiKey secret
        Key signingKey = getSigningKey();
      
        JwtBuilder builder = Jwts.builder()
        		.setId(Integer.toString(user.id))
                .setIssuedAt(now)
                .setSubject(user.email)
                .setIssuer(ISSUER)
                .claim("name", user.name)
                .claim("role", String.valueOf(user.role.id))
                .claim("email", user.email)
                .claim("parent", String.valueOf(user.parent.id))
                .claim("status", String.valueOf(user.status))
                .claim("locale", user.locale)
                .signWith(signatureAlgorithm, signingKey);
      
        // Add default timeout to JWT
        long expMillis = nowMillis + DEFAULT_TIMEOUT_MILLI_SECONDS;
        Date exp = new Date(expMillis);
        System.out.println("Exp: " + exp);
        builder.setExpiration(exp);
      
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }    

    public static Claims decodeJWT(String jwt) throws JwtException {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        
        return claims;
    }
    
    public static String verify(String token) {
    	
    	log.debug("verify - start");
    	
        long nowMillis = System.currentTimeMillis();
        Date now = new Date();
        System.out.println("Now: " + now);

        // Create Signing Key - We will sign our JWT with our ApiKey secret
        Key signingKey = getSigningKey();

    	Claims claims = decodeJWT(token);
    	
        JwtBuilder builder = Jwts.builder()
        		.setId(claims.getId())
                .setIssuedAt(now)
                .setSubject(claims.getSubject())
                .setIssuer(ISSUER)
                .claim("name", claims.get("name"))
                .claim("role", claims.get("role"))
                .claim("email", claims.get("email"))
                .claim("parent", claims.get("parent"))
                .claim("status", claims.get("status"))
                .claim("locale", claims.get("locale"))
                .signWith(signatureAlgorithm, signingKey);
      
        // Add default timeout to JWT
        long expMillis = nowMillis + DEFAULT_TIMEOUT_MILLI_SECONDS;
        Date exp = new Date(expMillis);
        System.out.println("Exp: " + exp);
        builder.setExpiration(exp);
      
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
}

