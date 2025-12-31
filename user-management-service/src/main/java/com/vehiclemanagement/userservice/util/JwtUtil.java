package com.vehiclemanagement.userservice.util;

import io. jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateToken(Long userId, String email, String role, Long entityId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        
        // Add entity-specific ID for ownership checks
        switch (role) {
            case "CUSTOMER":
                claims.put("customerId", entityId);
                break;
            case "TECHNICIAN":
                claims.put("technicianId", entityId);
                break;
            case "MANAGER":
                claims.put("managerId", entityId);
                break;
        }
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm. HS256)
                .compact();
    }
   
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }
   
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    
   
    public Long extractCustomerId(String token) {
        return extractAllClaims(token).get("customerId", Long.class);
    }
    
 
    public Long extractTechnicianId(String token) {
        return extractAllClaims(token).get("technicianId", Long.class);
    }
    
   
    public Long extractManagerId(String token) {
        return extractAllClaims(token).get("managerId", Long.class);
    }
    
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
  
    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
   
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }
}