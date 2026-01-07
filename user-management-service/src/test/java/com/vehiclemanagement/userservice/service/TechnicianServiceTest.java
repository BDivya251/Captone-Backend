package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.response.TechnicianResponse;
import com.vehiclemanagement.userservice.entity.Technician;
//import com.vehiclemanagement.userservice.enums.VehicleStatus;
import com.vehiclemanagement.userservice.repository.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org. mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers. any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private TechnicianService technicianService;

    private Technician technician;

    @BeforeEach
    void setUp() {
        technician = new Technician();
        technician.setId(1L);
        technician.setUserId(1L);
        technician.setName("Test Technician");
        technician.setPhone("1234567890");
        technician.setSkillSet("Mechanic");
        technician.setWorkAssigned(false);
    }

    @Test
    void getAllTechnicians_Success() {
        when(technicianRepository.findAll()).thenReturn(Arrays.asList(technician));

        List<TechnicianResponse> responses = technicianService.getAllTechnicians();

        assertEquals(1, responses.size());
        assertEquals("Test Technician", responses.get(0).getName());
    }

    @Test
    void getTechnicianById_Success() {
        when(technicianRepository.findById(1L)).thenReturn(Optional.of(technician));

        TechnicianResponse response = technicianService.getTechnicianById(1L);

        assertNotNull(response);
        assertEquals("Test Technician", response. getName());
    }

    @Test
    void getUnassignedTechnicians_Success() {
        when(technicianRepository.findByManagerIsNull()).thenReturn(Arrays.asList(technician));

        List<TechnicianResponse> responses = technicianService.getUnassignedTechnicians();

        assertEquals(1, responses. size());
        verify(technicianRepository).findByManagerIsNull();
    }

    @Test
    void assignedWork_Success() {
        when(technicianRepository.findById(1L)).thenReturn(Optional.of(technician));
        when(technicianRepository. save(any(Technician. class))).thenReturn(technician);

        Boolean result = technicianService.assignedWork(1L, true);

        assertTrue(result);
        verify(technicianRepository).save(any(Technician. class));
    }
}