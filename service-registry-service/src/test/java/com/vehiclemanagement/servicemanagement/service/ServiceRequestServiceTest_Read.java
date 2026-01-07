package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement.client.*;
import com. vehiclemanagement.servicemanagement.dto.response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement.entity.*;
import com.vehiclemanagement.servicemanagement.exception.ResourceNotFoundException;
import com.vehiclemanagement.servicemanagement.feign.*;
import com.vehiclemanagement.servicemanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org. mockito.Mock;
import org.mockito.junit.jupiter. MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest_Read {

    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private ServiceImageRepository serviceImageRepository;
    @Mock private InventoryUsageRepository inventoryUsageRepository;
    @Mock private ServiceBillRepository serviceBillRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private VehicleServiceClient vehicleServiceClient;
    @Mock private InventoryServiceClient inventoryServiceClient;
    @Mock private ServiceBayService serviceBayService;
    @Mock private PDFService pdfService;
    @Mock private EmailService emailService;

    @InjectMocks
    private ServiceRequestService serviceRequestService;

    private ServiceRequest serviceRequest;
    private TechnicianResponse technicianResponse;

    @BeforeEach
    void setUp() {
        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1L);
        serviceRequest.setCustomerId(100L);
        serviceRequest.setVehicleId(200L);
        serviceRequest.setStatus("PENDING");
        serviceRequest.setRequestType("REPAIR");
        serviceRequest.setDescription("Test");
        serviceRequest. setLaborCost(new BigDecimal("500"));

        technicianResponse = new TechnicianResponse();
        technicianResponse.setId(60L);
        technicianResponse.setName("Tech Mike");
    }

    @Test
    void getAllServiceRequests_Success() {
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(serviceRequest));
        when(inventoryUsageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        List<ServiceRequestResponse> result = serviceRequestService.getAllServiceRequests();

        assertEquals(1, result.size());
    }

    @Test
    void getAllServiceRequests_Empty() {
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList());

        List<ServiceRequestResponse> result = serviceRequestService.getAllServiceRequests();

        assertEquals(0, result.size());
    }

    @Test
    void getServiceRequestById_Success() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        when(inventoryUsageRepository.findByServiceRequestId(1L)).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(1L)).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Arrays.asList());

        ServiceRequestResponse response = serviceRequestService.getServiceRequestById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getServiceRequestById_NotFound() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceRequestService.getServiceRequestById(1L));
    }

    @Test
    void getServiceRequestsByCustomerId() {
        when(serviceRequestRepository.findByCustomerId(100L)).thenReturn(Arrays.asList(serviceRequest));
        when(inventoryUsageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        List<ServiceRequestResponse> result = serviceRequestService.getServiceRequestsByCustomerId(100L);

        assertEquals(1, result.size());
    }

    @Test
    void getServiceRequestsByManagerId() {
        when(serviceRequestRepository.findByManagerId(50L)).thenReturn(Arrays.asList(serviceRequest));
        when(inventoryUsageRepository. findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional.empty());
        when(serviceImageRepository. findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        List<ServiceRequestResponse> result = serviceRequestService.getServiceRequestsByManagerId(50L);

        assertEquals(1, result.size());
    }

    @Test
    void getServiceRequestsByTechnicianId() {
        when(serviceRequestRepository.findByTechnicianId(60L)).thenReturn(Arrays.asList(serviceRequest));
        when(inventoryUsageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        List<ServiceRequestResponse> result = serviceRequestService.getServiceRequestsByTechnicianId(60L);

        assertEquals(1, result.size());
    }

    @Test
    void getServiceRequestsByStatus() {
        when(serviceRequestRepository.findByStatus("PENDING")).thenReturn(Arrays.asList(serviceRequest));
        when(inventoryUsageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        List<ServiceRequestResponse> result = serviceRequestService. getServiceRequestsByStatus("PENDING");

        assertEquals(1, result.size());
    }

    @Test
    void getAssignedTasksByTechnician() {
        when(userServiceClient.getTechnicianById(60L)).thenReturn(technicianResponse);
        when(serviceRequestRepository.findByTechnicianIdAndStatus(60L, "ASSIGNED"))
            .thenReturn(Optional.of(Arrays.asList(serviceRequest)));
        when(inventoryUsageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        assertNotNull(serviceRequestService.getAssignedTasksByTechnician(60L));
    }

    @Test
    void getInProgressTasksByTechnician() {
        when(userServiceClient. getTechnicianById(60L)).thenReturn(technicianResponse);
        when(serviceRequestRepository.findByTechnicianIdAndStatus(60L, "IN_PROGRESS"))
            .thenReturn(Optional.of(Arrays.asList(serviceRequest)));
        when(inventoryUsageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional. empty());
        when(serviceImageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        assertNotNull(serviceRequestService. getInProgressTasksByTechnician(60L));
    }

    @Test
    void getCompletedTasksByTechnician() {
        when(userServiceClient.getTechnicianById(60L)).thenReturn(technicianResponse);
        when(serviceRequestRepository.findByTechnicianIdAndStatus(60L, "COMPLETED"))
            .thenReturn(Optional.of(Arrays.asList(serviceRequest)));
        when(inventoryUsageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(anyLong())).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(anyLong())).thenReturn(Arrays.asList());

        assertNotNull(serviceRequestService.getCompletedTasksByTechnician(60L));
    }

    @Test
    void getImage_Success() {
        ServiceImage image = new ServiceImage();
        image.setId(1L);
        when(serviceImageRepository.findById(1L)).thenReturn(Optional.of(image));

        ServiceImage result = serviceRequestService.getImage(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getImage_NotFound() {
        when(serviceImageRepository. findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceRequestService.getImage(1L));
    }
}