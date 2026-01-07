package com.vehiclemanagement.userservice.controller;

import com. vehiclemanagement.userservice. dto.response.ManagerResponse;
import com.vehiclemanagement.userservice.dto.response.TechnicianResponse;
import com.vehiclemanagement.userservice.service. ManagerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org. mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {

    @Mock
    private ManagerService managerService;

    @InjectMocks
    private ManagerController managerController;

    @Test
    void getAllManagers_Success() {
        ManagerResponse response = ManagerResponse.builder()
                .id(1L)
                .name("Manager")
                .build();

        when(managerService.getAllManagers()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<ManagerResponse>> result = managerController.getAllManagers();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getManagerById_Success() {
        ManagerResponse response = ManagerResponse.builder()
                .id(1L)
                .name("Manager")
                .build();

        when(managerService.getManagerById(1L)).thenReturn(response);

        ResponseEntity<ManagerResponse> result = managerController.getManagerById(1L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Manager", result.getBody().getName());
    }

    @Test
    void assignTechnicianToManager_Success() {
        doNothing().when(managerService).assignTechnicianToManager(1L, 1L);

        ResponseEntity<Map<String, String>> result = managerController.assignTechnicianToManager(1L, 1L);

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().containsKey("message"));
    }

    @Test
    void getTechniciansByManager_Success() {
        TechnicianResponse response = TechnicianResponse.builder()
                .id(1L)
                .name("Technician")
                .build();

        when(managerService.getTechniciansByManagerId(1L)).thenReturn(Arrays.asList(response));

        ResponseEntity<List<TechnicianResponse>> result = managerController.getTechniciansByManager(1L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void activateManager_Success() {
        doNothing().when(managerService).activateManagerStatus(1L);

        ResponseEntity<Map<String, String>> result = managerController.activateManager(1L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Manager status activated successfully", result.getBody().get("message"));
    }
}