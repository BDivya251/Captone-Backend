package com.vehiclemanagement.userservice. service;

import com.vehiclemanagement.userservice.dto. request.*;
import com.vehiclemanagement.userservice.dto.response.*;
import com.vehiclemanagement.userservice.entity.*;
import com.vehiclemanagement.userservice.enums.UserRole;
import com.vehiclemanagement.userservice.enums.UserStatus;
import com.vehiclemanagement.userservice.repository.*;
import com.vehiclemanagement.userservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito. Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension. class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private TechnicianRepository technicianRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Inject mocked passwordEncoder and jwtUtil using ReflectionTestUtils
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "jwtUtil", jwtUtil);
        
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus. ACTIVE);

        customer = new Customer();
        customer.setId(1L);
        customer.setUserId(1L);
        customer.setName("Test Customer");
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(customer));
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString(), anyLong())).thenReturn("token123");

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals("Test Customer", response.getName());
    }

    @Test
    void registerAdmin_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User. class))).thenReturn(user);
        when(adminRepository. save(any(Admin.class))).thenReturn(new Admin());

        RegisterAdminRequest request = new RegisterAdminRequest();
        request.setEmail("admin@test.com");
        request.setPassword("password");
        request.setName("Admin");
        request.setPhone("1234567890");

        RegisterResponse response = authService.registerAdmin(request);

        assertNotNull(response);
        verify(userRepository).save(any(User.class));
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void registerManager_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(managerRepository.save(any(Manager.class))).thenReturn(new Manager());

        RegisterManagerRequest request = new RegisterManagerRequest();
        request.setEmail("manager@test.com");
        request.setPassword("password");
        request.setName("Manager");
        request.setPhone("1234567890");

        RegisterResponse response = authService.registerManager(request);

        assertNotNull(response);
        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void registerTechnician_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(technicianRepository.save(any(Technician.class))).thenReturn(new Technician());

        RegisterTechnicianRequest request = new RegisterTechnicianRequest();
        request.setEmail("tech@test.com");
        request.setPassword("password");
        request.setName("Technician");
        request.setPhone("1234567890");
        request.setSkillSet("Mechanic");

        RegisterResponse response = authService.registerTechnician(request);

        assertNotNull(response);
        verify(technicianRepository).save(any(Technician.class));
    }

    @Test
    void registerCustomer_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(eventPublisher).publishUserRegistration(any());

        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setEmail("customer@test.com");
        request.setPassword("password");
        request.setName("Customer");
        request.setPhone("1234567890");

        RegisterResponse response = authService.registerCustomer(request);

        assertNotNull(response);
        verify(eventPublisher).publishUserRegistration(any());
    }

    @Test
    void getByKey_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = authService.getByKey(1L);

        assertNotNull(result);
        assertEquals("test@test.com", result. getEmail());
    }
}