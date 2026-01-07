package com.vehiclemanagement.servicemanagement.controller;

import com.vehiclemanagement.servicemanagement.dto.request.AddInventoryUsageRequest;
import com.vehiclemanagement.servicemanagement.dto.request.AssignManagerRequest;
import com. vehiclemanagement.servicemanagement.dto.request.AssignTechnicianRequest;
import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceRequestRequest;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement. entity.ServiceImage;
import com. vehiclemanagement.servicemanagement.entity.ServiceRequest;
import com.vehiclemanagement.servicemanagement.service.PriorityAnalysisService;
import com.vehiclemanagement.servicemanagement.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit. jupiter.api.Test;
import org.junit.jupiter.api. extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework. http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension. class)
class ServiceRequestControllerTest {

    @Mock
    private ServiceRequestService serviceRequestService;

    @Mock
    private PriorityAnalysisService priorityAnalysisService;

    @InjectMocks
    private ServiceRequestController serviceRequestController;

    private ServiceRequestResponse testResponse;
    private ServiceRequest testRequest;

    @BeforeEach
    void setUp() {
        testResponse = ServiceRequestResponse.builder()
                .id(1L)
                .customerId(1L)
                .vehicleId(1L)
                .requestType("REPAIR")
                .status("PENDING")
                .description("Test repair")
                .requestDate(LocalDateTime.now())
                .build();

        testRequest = new ServiceRequest();
        testRequest. setId(1L);
        testRequest.setStatus("PENDING");
    }


    @Test
    void createServiceRequest_Success() {
        CreateServiceRequestRequest request = new CreateServiceRequestRequest();
        request.setCustomerId(1L);
        request.setVehicleId(1L);

        when(serviceRequestService.createServiceRequest(any())).thenReturn(testResponse);

        ResponseEntity<Void> result = serviceRequestController.createServiceRequest(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(serviceRequestService).createServiceRequest(request);
    }

    @Test
    void uploadImages_Success() throws IOException {
        List<MultipartFile> files = Arrays.asList(
                new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "test1". getBytes()),
                new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "test2".getBytes())
        );

        doNothing().when(serviceRequestService).uploadImages(1L, files);

        ResponseEntity<Map<String, String>> result = serviceRequestController.uploadImages(1L, files);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(serviceRequestService).uploadImages(1L, files);
    }

    @Test
    void getImage_Success() {
        ServiceImage image = new ServiceImage();
        image.setId(1L);
        image.setImageName("test.jpg");
        image.setImageType("image/jpeg");
        image.setImageData("test data".getBytes());

        when(serviceRequestService.getImage(1L)).thenReturn(image);

        ResponseEntity<byte[]> result = serviceRequestController. getImage(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MediaType.parseMediaType("image/jpeg"), result.getHeaders().getContentType());
        assertArrayEquals("test data".getBytes(), result.getBody());
        verify(serviceRequestService).getImage(1L);
    }

    @Test
    void assignManager_Success() {
        AssignManagerRequest request = new AssignManagerRequest();
        request.setManagerId(2L);

        when(serviceRequestService.assignManager(1L, request)).thenReturn(testResponse);

        ResponseEntity<ServiceRequestResponse> result = serviceRequestController.assignManager(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testResponse, result.getBody());
        verify(serviceRequestService).assignManager(1L, request);
    }

    @Test
    void assignTechnician_Success() {
        AssignTechnicianRequest request = new AssignTechnicianRequest();
        request.setTechnicianId(3L);

        when(serviceRequestService.assignTechnician(1L, request)).thenReturn(testResponse);

        ResponseEntity<ServiceRequestResponse> result = serviceRequestController.assignTechnician(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testResponse, result.getBody());
        verify(serviceRequestService).assignTechnician(1L, request);
    }

    // ============================================
    // UPDATE TESTS
    // ============================================

    @Test
    void updateStatus_Success() {
        when(serviceRequestService.updateStatus(1L, "IN_PROGRESS")).thenReturn(testResponse);

        ResponseEntity<ServiceRequestResponse> result = serviceRequestController.updateStatus(1L, "IN_PROGRESS");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testResponse, result.getBody());
        verify(serviceRequestService).updateStatus(1L, "IN_PROGRESS");
    }

    @Test
    void updateRemarks_Success() {
        Map<String, String> body = new HashMap<>();
        body.put("remarks", "Test remarks");

        when(serviceRequestService.updateRemarks(1L, "Test remarks")).thenReturn(testResponse);

        ResponseEntity<ServiceRequestResponse> result = serviceRequestController.updateRemarks(1L, body);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testResponse, result.getBody());
        verify(serviceRequestService).updateRemarks(1L, "Test remarks");
    }


    @Test
    void payBill_Success() {
        when(serviceRequestService.payBill(1L)).thenReturn(true);

        ResponseEntity<Boolean> result = serviceRequestController.payBill(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
        verify(serviceRequestService).payBill(1L);
    }

    // ============================================
    // PRIORITY ANALYSIS TEST
    // ============================================

  
    @Test
    void downloadBillPdf_Success() {
        byte[] pdfContent = "test pdf content".getBytes();
        when(serviceRequestService.getStoredInvoicePdf(1L)).thenReturn(pdfContent);

        ResponseEntity<byte[]> result = serviceRequestController.downloadBillPdf(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertArrayEquals(pdfContent, result.getBody());
        assertTrue(result.getHeaders().getContentDisposition().toString().contains("Invoice_BILL_1.pdf"));
        verify(serviceRequestService).getStoredInvoicePdf(1L);
    }

    @Test
    void downloadInvoice_Success() {
        byte[] pdfContent = "test invoice content".getBytes();
        when(serviceRequestService.getInvoicePdfByServiceRequest(1L)).thenReturn(pdfContent);

        ResponseEntity<byte[]> result = serviceRequestController.downloadInvoice(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertArrayEquals(pdfContent, result.getBody());
        assertTrue(result.getHeaders().getContentDisposition().toString().contains("Invoice_SR_1.pdf"));
        verify(serviceRequestService).getInvoicePdfByServiceRequest(1L);
    }

    @Test
    void getPublicServiceDetails_Success() {
        when(serviceRequestService.getServiceRequestById(1L)).thenReturn(testResponse);

        ResponseEntity<Map<String, Object>> result = serviceRequestController.getPublicServiceDetails(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().get("serviceRequestId"));
        assertEquals("REPAIR", result.getBody().get("requestType"));
        assertEquals("PENDING", result.getBody().get("status"));
        verify(serviceRequestService).getServiceRequestById(1L);
    }

    // ============================================
    // RETRIEVE TESTS
    // ============================================

    @Test
    void getAllServiceRequests_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService. getAllServiceRequests()).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getAllServiceRequests();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result. getBody().size());
        verify(serviceRequestService).getAllServiceRequests();
    }

    @Test
    void getServiceRequestById_Success() {
        when(serviceRequestService.getServiceRequestById(1L)).thenReturn(testResponse);

        ResponseEntity<ServiceRequestResponse> result = serviceRequestController.getServiceRequestById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testResponse, result.getBody());
        verify(serviceRequestService).getServiceRequestById(1L);
    }

    @Test
    void getAllServiceRequestsByVehicle_Success() {
        List<ServiceRequest> requests = Arrays.asList(testRequest);
        when(serviceRequestService.getServiceRequestByVehicleid(1L)).thenReturn(requests);

        ResponseEntity<List<ServiceRequest>> result = serviceRequestController.getAllServiceRequestsByVehicle(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(serviceRequestService).getServiceRequestByVehicleid(1L);
    }

    @Test
    void getServiceRequestsByCustomerId_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService.getServiceRequestsByCustomerId(1L)).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getServiceRequestsByCustomerId(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(serviceRequestService).getServiceRequestsByCustomerId(1L);
    }

    @Test
    void getServiceRequestsByManagerId_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService.getServiceRequestsByManagerId(2L)).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getServiceRequestsByManagerId(2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(serviceRequestService).getServiceRequestsByManagerId(2L);
    }

    @Test
    void getServiceRequestsByTechnicianId_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService.getServiceRequestsByTechnicianId(3L)).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getServiceRequestsByTechnicianId(3L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(serviceRequestService).getServiceRequestsByTechnicianId(3L);
    }

    @Test
    void getServiceRequestsByStatus_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService.getServiceRequestsByStatus("PENDING")).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getServiceRequestsByStatus("PENDING");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(serviceRequestService).getServiceRequestsByStatus("PENDING");
    }

    @Test
    void getTotalLength_Success() {
        when(serviceRequestService.getLengthOfServiceRequests()).thenReturn(10);

        Integer result = serviceRequestController.getTotalLength();

        assertEquals(10, result);
        verify(serviceRequestService).getLengthOfServiceRequests();
    }

    // ============================================
    // TECHNICIAN SPECIFIC TESTS
    // ============================================

    @Test
    void getAssignedTasksByTechnician_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService.getAssignedTasksByTechnician(3L)).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getAssignedTasksByTechnician(3L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(serviceRequestService).getAssignedTasksByTechnician(3L);
    }

    @Test
    void getInProgressTasksByTechnician_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService.getInProgressTasksByTechnician(3L)).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getInProgressTasksByTechnician(3L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result. getBody().size());
        verify(serviceRequestService).getInProgressTasksByTechnician(3L);
    }

    @Test
    void getCompletedTasksByTechnician_Success() {
        List<ServiceRequestResponse> responses = Arrays.asList(testResponse);
        when(serviceRequestService.getCompletedTasksByTechnician(3L)).thenReturn(responses);

        ResponseEntity<List<ServiceRequestResponse>> result = serviceRequestController.getCompletedTasksByTechnician(3L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(serviceRequestService).getCompletedTasksByTechnician(3L);
    }


    @Test
    void deleteServiceRequest_Success() {
        doNothing().when(serviceRequestService).deleteServiceRequest(1L);

        ResponseEntity<Map<String, String>> result = serviceRequestController.deleteServiceRequest(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Service request deleted successfully", result.getBody().get("message"));
        assertEquals("1", result.getBody().get("serviceRequestId"));
        verify(serviceRequestService).deleteServiceRequest(1L);
    }
}