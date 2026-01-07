package com.vehiclemanagement.userservice.controller;

import com.vehiclemanagement.userservice.dto.request.*;
import com.vehiclemanagement.userservice.dto.response.*;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.enums.UserRole;
import com.vehiclemanagement.userservice.service. AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org. mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api. Assertions.*;
import static org. mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        LoginResponse response = LoginResponse.builder()
                .token("token123")
                .email("test@test.com")
                .role(UserRole.CUSTOMER)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        ResponseEntity<LoginResponse> result = authController.login(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("token123", result.getBody().getToken());
    }

    @Test
    void registerCustomer_Success() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setEmail("customer@test.com");
        request.setPassword("password");

        RegisterResponse response = RegisterResponse.builder()
                .userId(1L)
                .email("customer@test.com")
                .build();

        when(authService.registerCustomer(any())).thenReturn(response);

        ResponseEntity<Void> result = authController.registerCustomer(request);

        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void registerManager_Success() {
        RegisterManagerRequest request = new RegisterManagerRequest();
        request.setEmail("manager@test.com");
        request.setPassword("password");

        RegisterResponse response = RegisterResponse.builder()
                .userId(1L)
                .build();

        when(authService. registerManager(any())).thenReturn(response);

        ResponseEntity<RegisterResponse> result = authController.registerManager(request);

        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void registerTechnician_Success() {
        RegisterTechnicianRequest request = new RegisterTechnicianRequest();
        request.setEmail("tech@test.com");
        request.setPassword("password");

        RegisterResponse response = RegisterResponse.builder()
                .userId(1L)
                .build();

        when(authService.registerTechnician(any())).thenReturn(response);

        ResponseEntity<RegisterResponse> result = authController. registerTechnician(request);

        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    void getUserDetails_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        when(authService.getByKey(1L)).thenReturn(user);

        User result = authController.getUserDetails(1L);

        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getEmail());
    }
}