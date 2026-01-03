package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.request.*;
import com.vehiclemanagement.userservice.dto.response. LoginResponse;
import com.vehiclemanagement.userservice.dto.response.RegisterResponse;
import com.vehiclemanagement.userservice.entity.*;
import com.vehiclemanagement.userservice.enums.UserRole;
import com.vehiclemanagement.userservice.enums.UserStatus;
import com.vehiclemanagement. userservice.exception.BadRequestException;
import com.vehiclemanagement.userservice.repository.*;
import com.vehiclemanagement.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final TechnicianRepository technicianRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final EventPublisher eventPublisher;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
//    public RegisterResponse registerCustomer(RegisterRequest request) {
//        log.info("Registering customer:   {}", request.getEmail());
//        
//        // Existing registration logic... 
//        // Save user, save customer
//        
//        // ✅ PUBLISH EVENT TO RABBITMQ
//        try {
//            RegisterCustomerRequest event = RegisterCustomerRequest.builder()
//                    .userId(savedUser.getId())
//                    . email(savedUser.getEmail())
//                    .name(savedCustomer.getName())
//                    . role("CUSTOMER")
//                    .registrationDate(java.time.LocalDateTime.now().toString())
//                    .build();
//            
//            eventPublisher.publishUserRegistration(event);
//        } catch (Exception e) {
//            log.error("Failed to publish registration event: {}", e.getMessage());
//            // Don't fail registration if event publishing fails
//        }
//        
//        return buildRegisterResponse(savedUser, "CUSTOMER", savedCustomer.getId(), "Customer registered successfully");
//    }
    
    /**
     * LOGIN - All roles
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (! userOptional.isPresent()) {
            throw new BadRequestException("Invalid email or password");
        }
        
        User user = userOptional.get();
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active.  Status: " + user.getStatus());
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }
        
        String name = "";
        Long entityId = null;
        
        switch (user.getRole()) {
            case ADMIN:
                Optional<Admin> admin = adminRepository.findByUserId(user.getId());
                name = admin.map(Admin:: getName).orElse("Administrator");
                entityId = admin.map(Admin::getId).orElse(null);
                break;
                
            case MANAGER: 
                Optional<Manager> manager = managerRepository.findByUserId(user.getId());
                name = manager.map(Manager::getName).orElse("Manager");
                entityId = manager.map(Manager::getId).orElse(null);
                break;
                
            case TECHNICIAN: 
                Optional<Technician> technician = technicianRepository. findByUserId(user.getId());
                name = technician. map(Technician::getName).orElse("Technician");
                entityId = technician. map(Technician::getId).orElse(null);
                break;
                
            case CUSTOMER:
                Optional<Customer> customer = customerRepository.findByUserId(user.getId());
                name = customer.map(Customer:: getName).orElse("Customer");
                entityId = customer.map(Customer::getId).orElse(null);
                break;
                
            default:
                name = "User";
                entityId = user.getId();
        }
        
        String token = jwtUtil.generateToken(user. getId(), user.getEmail(), user.getRole().name(), entityId);
        
        log.info("Login successful for user: {} (Role: {})", user.getEmail(), user.getRole());
        
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(name)
                .entityId(entityId)
                .message("Login successful")
                .build();
    }
    
    /**
     * REGISTER ADMIN
     */
    @Transactional
    public RegisterResponse registerAdmin(RegisterAdminRequest request) {
        log.info("Registering new admin with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole. ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        Admin admin = new Admin();
        admin.setUserId(savedUser.getId());
        admin.setName(request.getName());
        admin.setPhone(request.getPhone());
        
        adminRepository.save(admin);
        
        log.info("Admin registered successfully: {}", request. getEmail());
        
        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .status(savedUser.getStatus().name())
                .message("Admin registered successfully")
                .build();
    }
    
    /**
     * REGISTER MANAGER
     */
    @Transactional
    public RegisterResponse registerManager(RegisterManagerRequest request) {
        log.info("Registering new manager with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole. MANAGER);
        user.setStatus(UserStatus.PENDING);
        
        User savedUser = userRepository.save(user);
        
        Manager manager = new Manager();
        manager.setUserId(savedUser.getId());
        manager.setName(request.getName());
        manager.setPhone(request.getPhone());
        
        managerRepository.save(manager);
        
        log.info("Manager registered successfully: {}", request.getEmail());
        
        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser. getEmail())
                .role(savedUser.getRole().name())
                .status(savedUser. getStatus().name())
                .message("Manager registered successfully")
                .build();
    }
    
    /**
     * REGISTER TECHNICIAN
     */
    @Transactional
    public RegisterResponse registerTechnician(RegisterTechnicianRequest request) {
        log.info("Registering new technician with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.TECHNICIAN);
        user.setStatus(UserStatus. PENDING);
        
        User savedUser = userRepository.save(user);
        
        Technician technician = new Technician();
        technician.setUserId(savedUser.getId());
        technician.setName(request.getName());
        technician. setPhone(request.getPhone());
        technician.setSkillSet(request.getSkillSet());
        
        technicianRepository.save(technician);
        
        log.info("Technician registered successfully: {}", request.getEmail());
        
        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser. getEmail())
                .role(savedUser.getRole().name())
                .status(savedUser. getStatus().name())
                .message("Technician registered successfully")
                .build();
    }
    
    /**
     * REGISTER CUSTOMER
     */
    @Transactional
    public RegisterResponse registerCustomer(RegisterCustomerRequest request) {
        log.info("Registering new customer with email:  {}", request.getEmail());
        
        if (userRepository. existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus. ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        Customer customer = new Customer();
        customer.setUserId(savedUser.getId());
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        
        customerRepository.save(customer);
        
        log.info("Customer registered successfully: {}", request.getEmail());
        
        RegisterResponse a= RegisterResponse.builder()
                .userId(savedUser. getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .status(savedUser.getStatus().name())
                .message("Customer registered successfully")
                .build();
        eventPublisher.publishUserRegistration(a);
        return a;
    }

	public User getByKey(Long id) {
		Optional<User> user= userRepository.findById(id);
		if(!user.isPresent()) {
			throw new BadRequestException("can not found the user by id");
		}
		return user.get();
	}
}