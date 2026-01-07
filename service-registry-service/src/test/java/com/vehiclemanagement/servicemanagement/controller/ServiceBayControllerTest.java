package com.vehiclemanagement.servicemanagement.controller;

import com.vehiclemanagement.servicemanagement.dto. request.CreateServiceBayRequest;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceBayResponse;
import com.vehiclemanagement.servicemanagement.service.ServiceBayService;
import org.junit.jupiter.api. Test;
import org.junit. jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit. jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java. util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBayControllerTest {

    @Mock
    private ServiceBayService serviceBayService;

    @InjectMocks
    private ServiceBayController serviceBayController;

    @Test
    void createServiceBay_Success() {
        CreateServiceBayRequest request = new CreateServiceBayRequest();
        request.setBayNumber("BAY-1");
        request.setBayName("Bay One");

        ServiceBayResponse response = ServiceBayResponse.builder()
                .id(1L)
                .bayNumber("BAY-1")
                .build();

        when(serviceBayService.createServiceBay(any())).thenReturn(response);

        ResponseEntity<Void> result = serviceBayController.createServiceBay(request);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllServiceBays_Success() {
        ServiceBayResponse response = ServiceBayResponse.builder()
                .id(1L)
                .bayNumber("BAY-1")
                .build();

        when(serviceBayService.getAllServiceBays()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<ServiceBayResponse>> result = serviceBayController.getAllServiceBays();

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getAvailableServiceBays_Success() {
        ServiceBayResponse response = ServiceBayResponse.builder()
                .id(1L)
                .isAvailable(true)
                .build();

        when(serviceBayService.getAvailableServiceBays()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<ServiceBayResponse>> result = serviceBayController.getAvailableServiceBays();

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deleteServiceBay_Success() {
        doNothing().when(serviceBayService).deleteServiceBay(1L);

        ResponseEntity<Map<String, String>> result = serviceBayController. deleteServiceBay(1L);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertTrue(result.getBody().containsKey("message"));
    }
}