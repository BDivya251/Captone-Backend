package com.vehiclemanagement.servicemanagement.controller;

import com.vehiclemanagement. servicemanagement.dto.response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement.service.ServiceRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org. mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api. Assertions.*;
import static org. mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicServiceControllerTest {

    @Mock
    private ServiceRequestService serviceRequestService;

    @Mock
    private Model model;

    @InjectMocks
    private PublicServiceController publicServiceController;

    @Test
    void getServiceDetails_Success() {
        ServiceRequestResponse response = ServiceRequestResponse.builder()
                .id(1L)
                .requestType("REPAIR")
                .status("COMPLETED")
                .build();

        when(serviceRequestService.getServiceRequestById(1L)).thenReturn(response);

        String viewName = publicServiceController.getServiceDetails(1L, model);

        assertEquals("public-invoice", viewName);
        verify(model, atLeastOnce()).addAttribute(anyString(), any());
    }
}