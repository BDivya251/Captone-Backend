package com.vehiclemanagement.userservice.controller;

import com. vehiclemanagement.userservice. dto.response.TechnicianResponse;
import com.vehiclemanagement.userservice.service. TechnicianService;
import org.junit.jupiter.api. Test;
import org.junit. jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util. Arrays;
import java.util. List;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianControllerTest {

    @Mock
    private TechnicianService technicianService;

    @InjectMocks
    private TechnicianController technicianController;

    @Test
    void getAllTechnicians_Success() {
        TechnicianResponse response = TechnicianResponse.builder()
                .id(1L)
                .name("Technician")
                .build();

        when(technicianService.getAllTechnicians()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<TechnicianResponse>> result = technicianController.getAllTechnicians();

        assertTrue(result.getStatusCode().is2xxSuccessful());  // CHANGED
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getUnassignedTechnicians_Success() {
        TechnicianResponse response = TechnicianResponse.builder()
                .id(1L)
                .name("Technician")
                .build();

        when(technicianService.getUnassignedTechnicians()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<TechnicianResponse>> result = technicianController.getUnassignedTechnicians();

        assertTrue(result.getStatusCode().is2xxSuccessful());  // CHANGED
        assertEquals(1, result. getBody().size());
    }

    @Test
    void getTechnicianById_Success() {
        TechnicianResponse response = TechnicianResponse.builder()
                .id(1L)
                .name("Technician")
                .build();

        when(technicianService. getTechnicianById(1L)).thenReturn(response);

        ResponseEntity<TechnicianResponse> result = technicianController. getTechnicianById(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());  // CHANGED
        assertEquals("Technician", result. getBody().getName());
    }

    @Test
    void assignWork_Success() {
        when(technicianService.assignedWork(1L, true)).thenReturn(true);

        ResponseEntity<Boolean> result = technicianController.assignWork(1L, true);

        assertTrue(result.getStatusCode().is2xxSuccessful());  // CHANGED
        assertTrue(result.getBody());
    }
}