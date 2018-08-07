package com.ajeet.second.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class TokenHelper {

    @Value("${app.name}")
    private String APP_NAME;

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    
    public String getJSessionIdFromToken(String token) {
        String jsessionId;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            jsessionId = (String)claims.get("JSESSIONID");
        } catch (Exception e) {
        	jsessionId = null;
        }
        return jsessionId;
    }

    public String generateToken(String username) {
        	
        return Jwts.builder()
                .setIssuer( APP_NAME )
                .setSubject(username)
                .setIssuedAt(generateCurrentDate())
                .setExpiration(generateExpirationDate())
                .signWith( SIGNATURE_ALGORITHM, SECRET )
                
                .compact();
    }
    
    public String generateToken(String username, String jsessionId){
    	 Claims claims = Jwts.claims();
         claims.put("JSESSIONID", jsessionId);
             	
         return Jwts.builder()
        		 .setClaims(claims)
                 .setIssuer( APP_NAME )
                 .setSubject(username)
                 .setIssuedAt(generateCurrentDate())
                 .setExpiration(generateExpirationDate())
                 .signWith( SIGNATURE_ALGORITHM, SECRET )
                 .compact();
    }

    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private long getCurrentTimeMillis() {
        return new DateTime().getMillis();
    }

    private Date generateCurrentDate() {
        return new Date(getCurrentTimeMillis());
    }

    private Date generateExpirationDate() {
        return new Date(getCurrentTimeMillis() + this.EXPIRES_IN * 1000);
    }
}