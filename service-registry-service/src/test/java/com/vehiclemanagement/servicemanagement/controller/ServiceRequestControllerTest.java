package com.vehiclemanagement.servicemanagement.controller;

import com.vehiclemanagement.servicemanagement. dto.request.CreateServiceRequestRequest;
import com.vehiclemanagement.servicemanagement.dto. response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement.service.ServiceRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito. Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org. mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestControllerTest {

    @Mock
    private ServiceRequestService serviceRequestService;

    @InjectMocks
    private ServiceRequestController serviceRequestController;

    @Test
    void createServiceRequest_Success() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(1L);
        request.setVehicleId(1L);

        ServiceRequestResponse response = ServiceRequestResponse.builder()
                .id(1L)
                .build();

        when(serviceRequestService.createServiceRequest(any())).thenReturn(response);

        ResponseEntity<Void> result = serviceRequestController.createServiceRequest(request);

        assertTrue(result. getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllServiceRequests_Success() {
        ServiceRequestResponse response = ServiceRequestResponse. builder()
                .id(1L)
                .status("PENDING")
                .build();

        when(serviceRequestService.getAllServiceRequests()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getAllServiceRequests();

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(1, result. getBody().size());
    }

    @Test
    void getServiceRequestById_Success() {
        ServiceRequestResponse response = ServiceRequestResponse. builder()
                .id(1L)
                .build();

        when(serviceRequestService.getServiceRequestById(1L)).thenReturn(response);

        ResponseEntity<ServiceRequestResponse> result = serviceRequestController.getServiceRequestById(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getTotalLength_Success() {
        when(serviceRequestService.getLengthOfServiceRequests()).thenReturn(10);

        Integer result = serviceRequestController.getTotalLength();

        assertEquals(10, result);
    }
}