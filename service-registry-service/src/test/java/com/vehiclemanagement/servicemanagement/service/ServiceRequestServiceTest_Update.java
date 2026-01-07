package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement.client.*;
import com.vehiclemanagement.servicemanagement.dto.request.*;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement.entity.*;
import com. vehiclemanagement.servicemanagement.exception.*;
import com.vehiclemanagement.servicemanagement.feign.*;
import com.vehiclemanagement.servicemanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api. Test;
import org.junit. jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit. jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest_Update {

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
    private VehicleResponse vehicleResponse;
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
        serviceRequest.setLaborCost(new BigDecimal("500"));

        vehicleResponse = new VehicleResponse();
        vehicleResponse.setId(200L);
        vehicleResponse. setCustomerId(100L);
        vehicleResponse.setRegistrationNumber("ABC123");

        technicianResponse = new TechnicianResponse();
        technicianResponse.setId(60L);
        technicianResponse.setName("Tech Mike");
    }

    @Test
    void assignManager_Success() {
        AssignManagerRequest request = new AssignManagerRequest();
        request.setManagerId(50L);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestRepository.save(any())).thenReturn(serviceRequest);
        when(vehicleServiceClient.getVehicleById(200L)).thenReturn(vehicleResponse);
        when(vehicleServiceClient.updateVehicleStatus(anyString(), anyString())).thenReturn("OK");

        ServiceRequestResponse response = serviceRequestService.assignManager(1L, request);

        assertNotNull(response);
        verify(vehicleServiceClient).updateVehicleStatus("IN_SERVICE", "ABC123");
    }

    @Test
    void assignManager_NotFound() {
        AssignManagerRequest request = new AssignManagerRequest();
        request.setManagerId(50L);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceRequestService.assignManager(1L, request));
    }

    @Test
    void assignManager_InvalidStatus() {
        AssignManagerRequest request = new AssignManagerRequest();
        request.setManagerId(50L);

        serviceRequest.setStatus("COMPLETED");
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));

        assertThrows(BadRequestException.class,
            () -> serviceRequestService.assignManager(1L, request));
    }

    @Test
    void assignTechnician_Success() {
        serviceRequest.setManagerId(50L);
        serviceRequest.setStatus("MANAGER_ASSIGNED");

        AssignTechnicianRequest request = new AssignTechnicianRequest();
        request.setTechnicianId(60L);
        request.setBayNumber("BAY-01");
        request.setLaborCost(new BigDecimal("500"));

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        when(userServiceClient.getTechnicianById(60L)).thenReturn(technicianResponse);
        when(serviceBayService.isBayAvailable("BAY-01")).thenReturn(true);
        when(serviceRequestRepository.save(any())).thenReturn(serviceRequest);
        when(userServiceClient.assignWork(anyLong(), anyBoolean())).thenReturn(true);

        ServiceRequestResponse response = serviceRequestService.assignTechnician(1L, request);

        assertNotNull(response);
        verify(serviceBayService).allocateBay("BAY-01", 1L);
    }

    @Test
    void assignTechnician_NoManagerAssigned() {
        AssignTechnicianRequest request = new AssignTechnicianRequest();
        request.setTechnicianId(60L);

        serviceRequest.setManagerId(null);
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));

        assertThrows(BadRequestException. class,
            () -> serviceRequestService.assignTechnician(1L, request));
    }

    @Test
    void assignTechnician_BayNotAvailable() {
        serviceRequest.setManagerId(50L);
        serviceRequest.setStatus("MANAGER_ASSIGNED");

        AssignTechnicianRequest request = new AssignTechnicianRequest();
        request.setTechnicianId(60L);
        request.setBayNumber("BAY-01");

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        when(userServiceClient.getTechnicianById(60L)).thenReturn(technicianResponse);
        when(serviceBayService.isBayAvailable("BAY-01")).thenReturn(false);

        assertThrows(BadRequestException.class,
            () -> serviceRequestService.assignTechnician(1L, request));
    }

    @Test
    void updateStatus_Success() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestRepository.save(any())).thenReturn(serviceRequest);

        ServiceRequestResponse response = serviceRequestService.updateStatus(1L, "MANAGER_ASSIGNED");

        assertNotNull(response);
    }

    @Test
    void updateStatus_InvalidTransition() {
        serviceRequest.setStatus("PENDING");
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));

        assertThrows(BadRequestException.class,
            () -> serviceRequestService.updateStatus(1L, "COMPLETED"));
    }

    @Test
    void updateRemarks_Success() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestRepository.save(any())).thenReturn(serviceRequest);
        when(inventoryUsageRepository.findByServiceRequestId(1L)).thenReturn(Arrays.asList());
        when(serviceBillRepository.findByServiceRequestId(1L)).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Arrays.asList());

        ServiceRequestResponse response = serviceRequestService.updateRemarks(1L, "Updated remarks");

        assertNotNull(response);
    }

    @Test
    void deleteServiceRequest_Success() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        doNothing().when(serviceRequestRepository).deleteById(1L);

        serviceRequestService.deleteServiceRequest(1L);

        verify(serviceRequestRepository).deleteById(1L);
    }

    @Test
    void deleteServiceRequest_NotFound() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceRequestService.deleteServiceRequest(1L));
    }
}