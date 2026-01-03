package com.vehiclemanagement.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class RoleAuthorizationFilter implements GlobalFilter, Ordered {

    @Value("${api.public.endpoints}")
    private String publicEndpoints;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        HttpMethod method = request.getMethod();

        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String role = request.getHeaders().getFirst("X-User-Role");
        String customerIdStr = request.getHeaders().getFirst("X-Customer-Id");
        String technicianIdStr = request.getHeaders().getFirst("X-Technician-Id");
        String userIdStr = request.getHeaders().getFirst("X-User-Id");

        if (role == null) {
            log.error("Missing X-User-Role header");
            return onError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        log.info(" Authorization Check:  Role={}, Path={}, Method={}", role, path, method);

        if ("ADMIN".equals(role)) {
            log.info("Admin access granted");
            return chain.filter(exchange);
        }

        boolean hasAccess = checkAccess(path, method, role, customerIdStr, technicianIdStr, userIdStr);

        if (hasAccess) {
            log.info("Access granted");
            return chain.filter(exchange);
        } else {
            log.warn("Access denied");
            return onError(exchange, "Forbidden - Insufficient permissions", HttpStatus.FORBIDDEN);
        }
    }

    private boolean checkAccess(String path, HttpMethod method, String role, String customerIdStr,
            String technicianIdStr, String userIdStr) {

        if ("MANAGER".equals(role)) {

            if (path.startsWith("/user-service/api/") ||
                    path.startsWith("/vehicle-service/api/vehicles") ||
                    path.startsWith("/inventory-service/api/inventory") ||
                    path.startsWith("/service-management-service/vehicle/")) {
                return true;
            }
            return false;
        }

        if ("TECHNICIAN".equals(role)) {
            Long technicianId = parseLong(technicianIdStr);

            if (path.matches("/user-service/api/technicians/" + technicianId)) {
                return true;
            }

            if ((path.startsWith("/vehicle-service/api/vehicles") ||
                    path.startsWith("/inventory-service/api/inventory") ||
                    path.startsWith("/service-management-service/vehicle/service-bays")) &&
                    method == HttpMethod.GET) {
                return true;
            }

            if (path.matches(
                    "/service-management-service/vehicle/service-requests/technician/" + technicianId + "/.*")) {
                return true;
            }

            if (path.matches(
                    "/service-management-service/vehicle/service-requests/\\d+/(status|remarks|inventory-usage|images)")) {
                return true;
            }

            if (path.matches("/service-management-service/vehicle/service-requests/(\\d+|images/\\d+)")
                    && method == HttpMethod.GET) {
                return true;
            }

            return false;
        }

        if ("CUSTOMER".equals(role)) {
            Long customerId = parseLong(customerIdStr);
            Long userId = parseLong(userIdStr);

            if (path.matches("/user-service/api/customers/" + customerId)) {
                return true;
            }

            if (path.matches("/user-service/api/customers/user/" + userId)) {
                log.info("Allowed access to user details for userId: {}", userId);
                return true;
            } else {
                log.info("Denied access to user details. Path: {}, Expected: /user-service/api/customers/user/{}", path,
                        userId);
            }

            if (path.equals("/service-management-service/vehicle/service-requests") && method == HttpMethod.POST) {
                return true;
            }

            if (path.matches("/vehicle-service/api/vehicles(/customer/" + customerId + "|/\\d+)?")) {
                return true;
            }

            // Manage own vehicles
            if (path.matches("/vehicle-service/api/vehicles(/\\d+)?") &&
                    (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE)) {
                return true;
            }

            // View ONLY own service requests
            if (path.matches(
                    "/service-management-service/vehicle/service-requests(/customer/" + customerId + "|/\\d+)?")
                    && method == HttpMethod.GET) {
                return true;
            }

            // Upload images for own requests
            if (path.matches("/service-management-service/vehicle/service-requests/\\d+/images")) {
                return true;
            }

            // View images
            if (path.startsWith("/service-management-service/vehicle/service-requests/images/")
                    && method == HttpMethod.GET) {
                return true;
            }

            return false;
        }

        return false;
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
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
                java.time.LocalDateTime.now());

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    @Override
    public int getOrder() {
        return -99;
    }
}