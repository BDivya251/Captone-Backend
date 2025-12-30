package com. vehiclemanagement.userservice. controller;

import com.vehiclemanagement.userservice.dto. request.LoginRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterCustomerRequest;
import com.vehiclemanagement.userservice.dto. request.RegisterManagerRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterTechnicianRequest;
import com.vehiclemanagement.userservice.dto.response. LoginResponse;
import com.vehiclemanagement.userservice.dto.response.UserResponse;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework. http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Login request received for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register/manager")
    public ResponseEntity<UserResponse> registerManager(@Valid @RequestBody RegisterManagerRequest request) {
        log.info("POST /api/auth/register/manager - Manager registration request received");
        UserResponse response = authService.registerManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/register/technician")
    public ResponseEntity<UserResponse> registerTechnician(@Valid @RequestBody RegisterTechnicianRequest request) {
        log.info("POST /api/auth/register/technician - Technician registration request received");
        UserResponse response = authService.registerTechnician(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/register/customer")
    public ResponseEntity<UserResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        log.info("POST /api/auth/register/customer - Customer registration request received");
        UserResponse response = authService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/user/{id}")
    public User getUserDetails(@PathVariable Long id) {
    	return authService.getByKey(id);
    }
}