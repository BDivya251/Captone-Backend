package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement. entity.ServiceRequest;
import com.vehiclemanagement.servicemanagement.repository.*;
import org.junit.jupiter. api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;
    @Mock
    private ServiceImageRepository serviceImageRepository;
    @Mock
    private InventoryUsageRepository inventoryUsageRepository;
    @Mock
    private ServiceBillRepository serviceBillRepository;

    @InjectMocks
    private ServiceRequestService serviceRequestService;

    @Test
    void getLengthOfServiceRequests_Success() {
        ServiceRequest sr1 = new ServiceRequest();
        ServiceRequest sr2 = new ServiceRequest();

        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(sr1, sr2));

        Integer length = serviceRequestService.getLengthOfServiceRequests();

        assertEquals(2, length);
    }
}