package com.ajeet.first.jwtutils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.ajeet.first.jwtmodel.JwtUser;

@Component
public class JwtGenerator {


    public String generate(JwtUser jwtUser) {

    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.MINUTE, 1);
        Claims claims = Jwts.claims()
                .setSubject(jwtUser.getUserName());
        claims.put("userId", String.valueOf(jwtUser.getId()));
        claims.put("role", jwtUser.getRole());
        claims.setExpiration(cal.getTime());


        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, "youtube")
                .compact();
    }
}
