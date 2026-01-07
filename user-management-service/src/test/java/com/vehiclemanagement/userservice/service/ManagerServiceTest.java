package com.vehiclemanagement.userservice.service;

import com. vehiclemanagement.userservice. dto.response.ManagerResponse;
import com.vehiclemanagement.userservice.dto.response. TechnicianResponse;
import com.vehiclemanagement.userservice.entity.Manager;
import com.vehiclemanagement.userservice.entity.Technician;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.enums.UserStatus;
import com.vehiclemanagement.userservice.repository. ManagerRepository;
import com. vehiclemanagement.userservice. repository.TechnicianRepository;
import com.vehiclemanagement.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter. api.Test;
import org. junit.jupiter.api.extension. ExtendWith;
import org. mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito. junit.jupiter.MockitoExtension;

import java.util. Arrays;
import java.util. List;
import java.util. Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito. ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private TechnicianRepository technicianRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ManagerService managerService;

    private Manager manager;
    private Technician technician;
    private User managerUser;
    private User technicianUser;

    @BeforeEach
    void setUp() {
        manager = new Manager();
        manager.setId(1L);
        manager.setUserId(1L);
        manager.setName("Test Manager");
        manager.setPhone("1234567890");

        technician = new Technician();
        technician.setId(1L);
        technician.setUserId(2L);
        technician.setName("Test Technician");

        managerUser = new User();
        managerUser. setId(1L);
        managerUser.setStatus(UserStatus.PENDING);

        technicianUser = new User();
        technicianUser.setId(2L);
        technicianUser.setStatus(UserStatus.PENDING);
    }

    @Test
    void getAllManagers_Success() {
        when(managerRepository.findAll()).thenReturn(Arrays.asList(manager));

        List<ManagerResponse> responses = managerService.getAllManagers();

        assertEquals(1, responses.size());
        assertEquals("Test Manager", responses. get(0).getName());
    }

    @Test
    void getManagerById_Success() {
        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));

        ManagerResponse response = managerService.getManagerById(1L);

        assertNotNull(response);
        assertEquals("Test Manager", response.getName());
    }

    @Test
    void assignTechnicianToManager_Success() {
        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(technicianRepository.findById(1L)).thenReturn(Optional.of(technician));
        
        // Mock different users for manager and technician
        when(userRepository.findById(1L)).thenReturn(Optional. of(managerUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(technicianUser));
        
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);
        when(userRepository.save(any(User.class))).thenReturn(managerUser);

        managerService.assignTechnicianToManager(1L, 1L);

        verify(technicianRepository).save(any(Technician.class));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void getTechniciansByManagerId_Success() {
        manager.setTechnicians(Arrays. asList(technician));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));

        List<TechnicianResponse> responses = managerService.getTechniciansByManagerId(1L);

        assertEquals(1, responses.size());
        assertEquals("Test Technician", responses.get(0).getName());
    }
}