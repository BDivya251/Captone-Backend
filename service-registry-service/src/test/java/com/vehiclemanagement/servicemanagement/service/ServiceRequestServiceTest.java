package com.vehiclemanagement.servicemanagement. service;

import com.vehiclemanagement.servicemanagement.client. InventoryServiceClient;
import com.vehiclemanagement.servicemanagement.client.UserServiceClient;
import com.vehiclemanagement.servicemanagement. client.VehicleServiceClient;
import com.vehiclemanagement.servicemanagement.dto.request.*;
import com.vehiclemanagement.servicemanagement.dto.response.*;
import com.vehiclemanagement.servicemanagement.entity.*;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement. exception.ResourceNotFoundException;
import com.vehiclemanagement.servicemanagement.feign.*;
import com.vehiclemanagement.servicemanagement.repository.*;
import feign.FeignException;
import org.junit.jupiter.api. BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org. mockito.Mock;
import org.mockito.junit.jupiter. MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest {

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

    private ServiceRequest testRequest;
    private CustomerResponse testCustomer;
    private VehicleResponse testVehicle;
    private TechnicianResponse testTechnician;
    private User testUser;

    @BeforeEach
    void setUp() {
        testRequest = new ServiceRequest();
        testRequest.setId(1L);
        testRequest.setCustomerId(100L);
        testRequest. setVehicleId(200L);
        testRequest.setRequestType("REPAIR");
        testRequest.setDescription("Engine issue");
        testRequest.setStatus("PENDING");
        testRequest.setRequestDate(LocalDateTime.now());

        testCustomer = new CustomerResponse();
        testCustomer.setId(100L);
        testCustomer.setUserId(1L);
        testCustomer.setName("John Doe");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john@example.com");
//        testUser.setName("John Doe");

        testVehicle = new VehicleResponse();
        testVehicle.setId(200L);
        testVehicle. setCustomerId(100L);
        testVehicle.setRegistrationNumber("ABC123");
        testVehicle. setMake("Honda");
        testVehicle. setModel("Civic");

        testTechnician = new TechnicianResponse();
        testTechnician.setId(300L);
        testTechnician.setName("Tech Mike");
    }

    @Test
    void createServiceRequest_Success() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(100L);
        request.setVehicleId(200L);
        request.setRequestType("REPAIR");
        request.setDescription("Test repair");

        when(userServiceClient.getCustomerById(100L)).thenReturn(testCustomer);
        when(vehicleServiceClient.getVehicleById(200L)).thenReturn(testVehicle);
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(testRequest);
        when(inventoryUsageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());
        when(serviceBillRepository.findByServiceRequestId(1L)).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());

        ServiceRequestResponse response = serviceRequestService.createServiceRequest(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PENDING", response.getStatus());
        
        verify(userServiceClient).getCustomerById(100L);
        verify(vehicleServiceClient).getVehicleById(200L);
        verify(serviceRequestRepository).save(any(ServiceRequest. class));
    }

    @Test
    void createServiceRequest_CustomerNotFound_ThrowsBadRequestException() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(999L);

        when(userServiceClient.getCustomerById(999L))
            .thenThrow(FeignException.NotFound.class);

        assertThrows(BadRequestException.class, () -> {
            serviceRequestService.createServiceRequest(request);
        });
    }

    @Test
    void createServiceRequest_VehicleDoesNotBelongToCustomer_ThrowsBadRequestException() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(100L);
        request.setVehicleId(200L);

        testVehicle.setCustomerId(999L);

        when(userServiceClient.getCustomerById(100L)).thenReturn(testCustomer);
        when(vehicleServiceClient.getVehicleById(200L)).thenReturn(testVehicle);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            serviceRequestService.createServiceRequest(request);
        });

        assertTrue(exception.getMessage().contains("does not belong to this customer"));
    }

    @Test
    void uploadImages_Success() throws IOException {
        List<MultipartFile> files = Arrays.asList(
            new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "test1". getBytes()),
            new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "test2".getBytes())
        );

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional. of(testRequest));
        when(serviceImageRepository.save(any(ServiceImage.class))).thenReturn(new ServiceImage());

        assertDoesNotThrow(() -> {
            serviceRequestService.uploadImages(1L, files);
        });

        verify(serviceRequestRepository).findById(1L);
        verify(serviceImageRepository, times(2)).save(any(ServiceImage.class));
    }

    @Test
    void uploadImages_ServiceRequestNotFound_ThrowsResourceNotFoundException() {
        when(serviceRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceRequestService.uploadImages(999L, Collections.emptyList());
        });
    }

   
    @Test
    void assignManager_Success() {
        AssignManagerRequest request = new AssignManagerRequest();
        request.setManagerId(50L);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(testRequest);
        when(vehicleServiceClient.getVehicleById(200L)).thenReturn(testVehicle);
        lenient().doAnswer(invocation -> null)
            .when(vehicleServiceClient).updateVehicleStatus(anyString(), anyString());
        
        when(inventoryUsageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());
        when(serviceBillRepository.findByServiceRequestId(1L)).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());

        ServiceRequestResponse response = serviceRequestService.assignManager(1L, request);

        assertNotNull(response);
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
    }

    @Test
    void assignManager_NotInPendingStatus_ThrowsBadRequestException() {
        testRequest.setStatus("IN_PROGRESS");
        AssignManagerRequest request = new AssignManagerRequest();

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(BadRequestException.class, () -> {
            serviceRequestService.assignManager(1L, request);
        });
    }

    @Test
    void assignTechnician_Success() {
        testRequest.setManagerId(50L);
        testRequest.setStatus("MANAGER_ASSIGNED");

        AssignTechnicianRequest request = new AssignTechnicianRequest();
        request.setTechnicianId(300L);
        request.setBayNumber("BAY-01");
        request.setLaborCost(new BigDecimal("500.00"));

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userServiceClient.getTechnicianById(300L)).thenReturn(testTechnician);
        when(serviceBayService.isBayAvailable("BAY-01")).thenReturn(true);
        lenient().when(userServiceClient. assignWork(anyLong(), anyBoolean())).thenReturn(null);
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(testRequest);
        when(inventoryUsageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());
        when(serviceBillRepository.findByServiceRequestId(1L)).thenReturn(Optional. empty());
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());

        ServiceRequestResponse response = serviceRequestService. assignTechnician(1L, request);

        assertNotNull(response);
        verify(serviceBayService).allocateBay("BAY-01", 1L);
    }

    @Test
    void assignTechnician_ManagerNotAssigned_ThrowsBadRequestException() {
        testRequest.setManagerId(null);

        AssignTechnicianRequest request = new AssignTechnicianRequest();
        request.setTechnicianId(300L);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(BadRequestException.class, () -> {
            serviceRequestService.assignTechnician(1L, request);
        });
    }

    @Test
    void assignTechnician_BayNotAvailable_ThrowsBadRequestException() {
        testRequest.setManagerId(50L);
        testRequest.setStatus("MANAGER_ASSIGNED");

        AssignTechnicianRequest request = new AssignTechnicianRequest();
        request.setTechnicianId(300L);
        request.setBayNumber("BAY-01");

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userServiceClient.getTechnicianById(300L)).thenReturn(testTechnician);
        when(serviceBayService.isBayAvailable("BAY-01")).thenReturn(false);
//         doNothing().when(userServiceClient).assignWork(anyLong(), anyBoolean());
        lenient().when(userServiceClient. assignWork(anyLong(), anyBoolean())).thenReturn(null);
        assertThrows(BadRequestException.class, () -> {
            serviceRequestService.assignTechnician(1L, request);
        });
    }

    @Test
    void updateStatus_ToCompleted_Success() {
        testRequest.setStatus("IN_PROGRESS");
        testRequest.setBayNumber("BAY-01");
        testRequest.setTechnicianId(300L);
        testRequest.setLaborCost(new BigDecimal("500"));

        ServiceBill bill = new ServiceBill();
        bill.setId(1L);
        bill.setBillNumber("BILL-123");
        bill.setServiceRequest(testRequest);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
//        doNothing().when(serviceBayService).releaseBay(anyString());
//        doNothing().when(userServiceClient).assignWork(anyLong(), anyBoolean());
        lenient().when(userServiceClient. assignWork(anyLong(), anyBoolean())).thenReturn(null);
        when(inventoryUsageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());
        when(serviceBillRepository.findByServiceRequestId(1L)).thenReturn(Optional. empty());
        when(serviceBillRepository.save(any(ServiceBill.class))).thenReturn(bill);
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(testRequest);
        when(userServiceClient.getCustomerById(100L)).thenReturn(testCustomer);
        when(userServiceClient.getUserDetails(1L)).thenReturn(testUser);
        when(vehicleServiceClient.getVehicleById(200L)).thenReturn(testVehicle);
        when(userServiceClient.getTechnicianById(300L)).thenReturn(testTechnician);
        lenient().when(userServiceClient. assignWork(anyLong(), anyBoolean())).thenReturn(null);
//        doNothing().when(emailService).sendServiceCompletionEmail(
//            anyString(), anyString(), any(), any(), anyString(), anyString(), anyList());
        lenient().doAnswer(invocation -> null)
            .when(vehicleServiceClient).updateVehicleStatus(anyString(), anyString());
        
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());

        ServiceRequestResponse response = serviceRequestService.updateStatus(1L, "COMPLETED");

        assertNotNull(response);
        verify(serviceBayService).releaseBay("BAY-01");
    }

    @Test
    void updateStatus_InvalidTransition_ThrowsBadRequestException() {
        testRequest.setStatus("PENDING");

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(BadRequestException. class, () -> {
            serviceRequestService.updateStatus(1L, "COMPLETED");
        });
    }

    @Test
    void addInventoryUsage_Success() {
        AddInventoryUsageRequest request = new AddInventoryUsageRequest();
        request.setInventoryItemId(10L);
        request.setQuantity(2);

        InventoryItemResponse item = new InventoryItemResponse();
        item.setId(10L);
        item.setPartName("Oil Filter");
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("100.00"));

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(inventoryServiceClient.getInventoryItemById(10L)).thenReturn(item);
        when(inventoryUsageRepository.save(any(InventoryUsage.class))).thenReturn(new InventoryUsage());
        
         lenient().doAnswer(invocation -> null)
            .when(inventoryServiceClient).updateQuantity(anyLong(), anyInt());

        assertDoesNotThrow(() -> {
            serviceRequestService.addInventoryUsage(1L, request);
        });

        verify(inventoryUsageRepository).save(any(InventoryUsage.class));
    }

    @Test
    void addInventoryUsage_InsufficientStock_ThrowsBadRequestException() {
        AddInventoryUsageRequest request = new AddInventoryUsageRequest();
        request.setInventoryItemId(10L);
        request.setQuantity(20);

        InventoryItemResponse item = new InventoryItemResponse();
        item.setQuantity(5);

        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(inventoryServiceClient.getInventoryItemById(10L)).thenReturn(item);

        assertThrows(BadRequestException.class, () -> {
            serviceRequestService.addInventoryUsage(1L, request);
        });
    }
    @Test
    void getAllServiceRequests_Success() {
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(testRequest));
        when(inventoryUsageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());
        when(serviceBillRepository. findByServiceRequestId(1L)).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Collections. emptyList());

        List<ServiceRequestResponse> responses = serviceRequestService.getAllServiceRequests();

        assertEquals(1, responses.size());
        verify(serviceRequestRepository).findAll();
    }

    @Test
    void getServiceRequestById_Success() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(inventoryUsageRepository. findByServiceRequestId(1L)).thenReturn(Collections.emptyList());
        when(serviceBillRepository.findByServiceRequestId(1L)).thenReturn(Optional.empty());
        when(serviceImageRepository.findByServiceRequestId(1L)).thenReturn(Collections.emptyList());

        ServiceRequestResponse response = serviceRequestService.getServiceRequestById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getServiceRequestById_NotFound_ThrowsResourceNotFoundException() {
        when(serviceRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceRequestService.getServiceRequestById(999L);
        });
    }

    @Test
    void getLengthOfServiceRequests_Success() {
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(testRequest, testRequest));

        Integer length = serviceRequestService.getLengthOfServiceRequests();

        assertEquals(2, length);
    }

    @Test
    void deleteServiceRequest_Success() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
//        doNothing().when(serviceRequestRepository).deleteById(1L);
        lenient().when(userServiceClient. assignWork(anyLong(), anyBoolean())).thenReturn(null);
        assertDoesNotThrow(() -> {
            serviceRequestService.deleteServiceRequest(1L);
        });

        verify(serviceRequestRepository).deleteById(1L);
    }
    @Test
    void payBill_Success() {
        ServiceBill bill = new ServiceBill();
        bill.setId(1L);
        bill.setPaid(false);

        when(serviceBillRepository.findById(1L)).thenReturn(Optional. of(bill));
        when(serviceBillRepository.save(any(ServiceBill.class))).thenReturn(bill);

        Boolean result = serviceRequestService.payBill(1L);

        assertTrue(result);
        verify(serviceBillRepository).save(bill);
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
}