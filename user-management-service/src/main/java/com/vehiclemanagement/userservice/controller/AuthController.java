package com. vehiclemanagement.userservice. controller;

import org.springframework. http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind. annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation. RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehiclemanagement.userservice.dto.request.LoginRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterAdminRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterCustomerRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterManagerRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterTechnicianRequest;
import com.vehiclemanagement.userservice.dto.response.LoginResponse;
import com.vehiclemanagement.userservice.dto. response.RegisterResponse;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Login request received for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register/manager")
    public ResponseEntity<RegisterResponse> registerManager(@Valid @RequestBody RegisterManagerRequest request) {
        
        RegisterResponse response = authService.registerManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/register/technician")
    public ResponseEntity<RegisterResponse> registerTechnician(@Valid @RequestBody RegisterTechnicianRequest request) {
        
        RegisterResponse response = authService.registerTechnician(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/register/admin")
    public ResponseEntity<RegisterResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        
        RegisterResponse response = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    
    @PostMapping("/register/customer")
    public ResponseEntity<RegisterResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        RegisterResponse response = authService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/user/{id}")
    public User getUserDetails(@PathVariable Long id) {
    	return authService.getByKey(id);
    }
}