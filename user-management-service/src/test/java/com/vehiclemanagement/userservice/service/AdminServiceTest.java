package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.response.AdminResponse;
import com. vehiclemanagement.userservice. entity.Admin;
import com. vehiclemanagement.userservice. entity.User;
import com.vehiclemanagement.userservice.repository.AdminRepository;
import com. vehiclemanagement.userservice. repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private Admin admin;
    private User user;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setId(1L);
        admin.setUserId(1L);
        admin.setName("Admin User");
        admin.setPhone("1234567890");

        user = new User();
        user.setId(1L);
        user.setEmail("admin@test.com");
    }

    @Test
    void getAllAdmins_Success() {
        when(adminRepository.findAll()).thenReturn(Arrays.asList(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<AdminResponse> responses = adminService.getAllAdmins();

        assertEquals(1, responses.size());
        assertEquals("Admin User", responses.get(0).getName());
        verify(adminRepository).findAll();
    }

    @Test
    void getAdminById_Success() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AdminResponse response = adminService.getAdminById(1L);

        assertNotNull(response);
        assertEquals("Admin User", response.getName());
        assertEquals("admin@test.com", response.getEmail());
    }
}