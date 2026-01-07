package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement.client.*;
import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceRequestRequest;
import com. vehiclemanagement.servicemanagement.entity. ServiceRequest;
import com.vehiclemanagement.servicemanagement. exception.BadRequestException;
import com.vehiclemanagement.servicemanagement.feign.*;
import com.vehiclemanagement.servicemanagement.repository.*;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit. jupiter.api.Test;
import org.junit.jupiter.api. extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org. springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension. class)
class ServiceRequestServiceTest_Create {

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
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1L);
        serviceRequest.setCustomerId(100L);
        serviceRequest.setVehicleId(200L);
        serviceRequest. setStatus("PENDING");
        serviceRequest.setRequestType("REPAIR");
        serviceRequest.setDescription("Test");
        serviceRequest.setLaborCost(new BigDecimal("500"));

        vehicleResponse = new VehicleResponse();
        vehicleResponse.setId(200L);
        vehicleResponse.setCustomerId(100L);
        vehicleResponse.setRegistrationNumber("ABC123");
        vehicleResponse.setMake("Toyota");
        vehicleResponse.setModel("Camry");

        customerResponse = new CustomerResponse();
        customerResponse.setUserId(1L);
        customerResponse.setName("John Doe");
    }

    @Test
    void createServiceRequest_Success() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(100L);
        request.setVehicleId(200L);
        request.setRequestType("REPAIR");
        request.setDescription("Test");

        when(userServiceClient.getCustomerById(100L)).thenReturn(customerResponse);
        when(vehicleServiceClient.getVehicleById(200L)).thenReturn(vehicleResponse);
        when(serviceRequestRepository. save(any())).thenReturn(serviceRequest);

        assertNotNull(serviceRequestService.createServiceRequest(request));
        verify(serviceRequestRepository).save(any());
    }

    @Test
    void createServiceRequest_CustomerNotFound() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(100L);
        request.setVehicleId(200L);

        when(userServiceClient.getCustomerById(100L))
            .thenThrow(mock(FeignException.NotFound.class));

        assertThrows(BadRequestException.class, 
            () -> serviceRequestService. createServiceRequest(request));
    }

    @Test
    void createServiceRequest_VehicleNotFound() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(100L);
        request.setVehicleId(200L);

        when(userServiceClient.getCustomerById(100L)).thenReturn(customerResponse);
        when(vehicleServiceClient.getVehicleById(200L))
            .thenThrow(mock(FeignException.NotFound.class));

        assertThrows(BadRequestException.class, 
            () -> serviceRequestService. createServiceRequest(request));
    }

    @Test
    void createServiceRequest_VehicleNotBelongToCustomer() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(100L);
        request.setVehicleId(200L);

        vehicleResponse.setCustomerId(999L);

        when(userServiceClient.getCustomerById(100L)).thenReturn(customerResponse);
        when(vehicleServiceClient.getVehicleById(200L)).thenReturn(vehicleResponse);

        assertThrows(BadRequestException.class, 
            () -> serviceRequestService.createServiceRequest(request));
    }

    @Test
    void uploadImages_Success() throws Exception {
        MultipartFile file1 = mock(MultipartFile. class);
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file1.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(file1.getContentType()).thenReturn("image/jpeg");

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));

        serviceRequestService.uploadImages(1L, Arrays.asList(file1));

        verify(serviceImageRepository, times(1)).save(any());
    }

    @Test
    void uploadImages_MultipleFiles() throws Exception {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file1.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(file1.getContentType()).thenReturn("image/jpeg");
        
        when(file2.getOriginalFilename()).thenReturn("image2.jpg");
        when(file2.getBytes()).thenReturn(new byte[]{4, 5, 6});
        when(file2.getContentType()).thenReturn("image/jpeg");

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));

        serviceRequestService.uploadImages(1L, Arrays.asList(file1, file2));

        verify(serviceImageRepository, times(2)).save(any());
    }

    @Test
    void uploadImages_ServiceRequestNotFound() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class,
            () -> serviceRequestService. uploadImages(1L, Arrays. asList()));
    }

    @Test
    void getLengthOfServiceRequests() {
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(serviceRequest));
        assertEquals(1, serviceRequestService.getLengthOfServiceRequests());
    }

    @Test
    void getServiceRequestByVehicleId() {
        when(serviceRequestRepository.findByVehicleId(200L))
            .thenReturn(Arrays.asList(serviceRequest));
        
        assertEquals(1, serviceRequestService.getServiceRequestByVehicleid(200L).size());
    }
}