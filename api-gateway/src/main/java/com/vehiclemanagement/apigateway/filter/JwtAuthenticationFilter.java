package com.vehiclemanagement.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework. beans.factory.annotation.Value;
import org.springframework.cloud. gateway.filter.GatewayFilterChain;
import org. springframework.cloud.gateway.filter. GlobalFilter;
import org.springframework.core.Ordered;
import org. springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core. publisher.Mono;

import java.security.Key;
import java.util.Arrays;
import java.util.List;


@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${api.public.endpoints}")
    private String publicEndpoints;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        
        log.info("Request: {} {}", method, path);
        
        if (isPublicEndpoint(path)) {
            log.info(" Public endpoint - No authentication required");
            return chain. filter(exchange);
        }
        
        // Get Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Validate token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // Extract user info
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            Long userId = claims.get("userId", Long.class);
            Long adminId = claims.get("adminId", Long.class);
            Long customerId = claims.get("customerId", Long.class);
            Long technicianId = claims.get("technicianId", Long.class);
            Long managerId = claims.get("managerId", Long.class);
            
            log.info("Token valid - User: {}, Role: {}", email, role);
            
            // Add user info to request headers
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .header("X-User-Id", userId. toString())
                    .header("X-Admin-Id", adminId != null ? adminId.toString() : "")
                    .header("X-Customer-Id", customerId != null ?  customerId.toString() : "")
                    .header("X-Technician-Id", technicianId != null ? technicianId.toString() : "")
                    .header("X-Manager-Id", managerId != null ?  managerId.toString() : "")
                    .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
            
        } catch (Exception e) {
            log.error("Token validation failed:  {}", e.getMessage());
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }
    
    private boolean isPublicEndpoint(String path) {
        List<String> publicPaths = Arrays.asList(publicEndpoints.split(","));
        
        for (String publicPath : publicPaths) {
            if (publicPath.contains(".*")) {
                if (path.matches(publicPath)) {
                    return true;
                }
            } else if (path.startsWith(publicPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorBody = String.format(
            "{\"error\": \"%s\", \"status\": %d, \"timestamp\": \"%s\"}", 
            message, 
            status.value(),
            java.time.LocalDateTime.now()
        );
        
        return response.writeWith(Mono. just(response.bufferFactory().wrap(errorBody.getBytes())));
    }
    
    @Override
    public int getOrder() {
        return -100;
    }
}