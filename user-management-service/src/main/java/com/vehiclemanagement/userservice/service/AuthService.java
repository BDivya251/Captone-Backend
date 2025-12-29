package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.request.LoginRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterCustomerRequest;
import com.vehiclemanagement.userservice.dto.request.RegisterManagerRequest;
import com.vehiclemanagement. userservice.dto.request.RegisterTechnicianRequest;
import com.vehiclemanagement.userservice.dto.response. LoginResponse;
import com.vehiclemanagement.userservice.dto.response.UserResponse;
import com.vehiclemanagement.userservice.entity.Customer;
import com.vehiclemanagement.userservice.entity.Manager;
import com.vehiclemanagement.userservice.entity.Technician;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.enums.UserRole;
import com.vehiclemanagement.userservice.enums.UserStatus;
import com.vehiclemanagement.userservice.exception.BadRequestException;
import com.vehiclemanagement. userservice.exception.ResourceNotFoundException;
import com.vehiclemanagement.userservice.repository.CustomerRepository;
import com.vehiclemanagement.userservice.repository. ManagerRepository;
import com.vehiclemanagement.userservice.repository.TechnicianRepository;
import com.vehiclemanagement. userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework. stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final TechnicianRepository technicianRepository;
    private final CustomerRepository customerRepository;
    
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request. getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        
        // Simple password check (In production, use BCrypt)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("User account is not active.  Status: " + user.getStatus());
        }
        
        log.info("Login successful for user: {}", user. getEmail());
        
        return LoginResponse.builder()
                .userId(user.getId())
                .email(user. getEmail())
                .role(user.getRole().name())
                .token("dummy-jwt-token") // Will be replaced with real JWT later
                .message("Login successful")
                .build();
    }
    
    @Transactional
    public UserResponse registerManager(RegisterManagerRequest request) {
        log.info("Registering manager with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request. getEmail())) {
            throw new BadRequestException("Email already exists:  " + request.getEmail());
        }
        
        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In production, hash with BCrypt
        user.setRole(UserRole.MANAGER);
        user.setStatus(UserStatus.PENDING);
        
        User savedUser = userRepository.save(user);
        
        // Create Manager
        Manager manager = new Manager();
        manager.setUserId(savedUser.getId());
        manager.setName(request. getName());
        manager.setPhone(request.getPhone());
        
        managerRepository.save(manager);
        
        log.info("Manager registered successfully:  {}", savedUser.getEmail());
        
        return UserResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .status(savedUser.getStatus().name())
                .build();
    }
    
    @Transactional
    public UserResponse registerTechnician(RegisterTechnicianRequest request) {
        log.info("Registering technician with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        
        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In production, hash with BCrypt
        user.setRole(UserRole.TECHNICIAN);
        user.setStatus(UserStatus. PENDING);
        
        User savedUser = userRepository.save(user);
        
        // Create Technician
        Technician technician = new Technician();
        technician.setUserId(savedUser.getId());
        technician.setName(request.getName());
        technician.setPhone(request.getPhone());
        technician.setSkillSet(request.getSkillSet());
        
        technicianRepository.save(technician);
        
        log.info("Technician registered successfully: {}", savedUser.getEmail());
        
        return UserResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .status(savedUser.getStatus().name())
                .build();
    }
    
    @Transactional
    public UserResponse registerCustomer(RegisterCustomerRequest request) {
        log.info("Registering customer with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        
        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In production, hash with BCrypt
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE); // Customers are active by default
        
        User savedUser = userRepository.save(user);
        
        // Create Customer
        Customer customer = new Customer();
        customer.setUserId(savedUser.getId());
        customer.setName(request. getName());
        customer.setPhone(request.getPhone());
        
        customerRepository.save(customer);
        
        log.info("Customer registered successfully: {}", savedUser.getEmail());
        
        return UserResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .status(savedUser.getStatus().name())
                .build();
    }
}