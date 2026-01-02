package com.vehiclemanagement.servicemanagement.controller;

import com.vehiclemanagement.servicemanagement.dto.request.AddInventoryUsageRequest;
import com.vehiclemanagement.servicemanagement.dto.request.AssignManagerRequest;
import com.vehiclemanagement.servicemanagement.dto.request.AssignTechnicianRequest;
import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceRequestRequest;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement.entity.ServiceImage;
import com.vehiclemanagement.servicemanagement.service.PDFService;
import com.vehiclemanagement.servicemanagement.service.PriorityAnalysisService;
import com.vehiclemanagement.servicemanagement.service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/vehicle/service-requests")
@RequiredArgsConstructor
@Slf4j
public class ServiceRequestController {
    private final PriorityAnalysisService priorityAnalysisService;
    private final PDFService pdfService;
    private final ServiceRequestService serviceRequestService;
    // public endpoint

    @PostMapping("/{serviceRequestId}/analyze-priority")
    public ResponseEntity<?> analyzePriority(@PathVariable Long serviceRequestId) {
        log.info("POST /vehicle/service-requests/{}/analyze-priority", serviceRequestId);

        ServiceRequestResponse request = serviceRequestService.getServiceRequestById(serviceRequestId);
        String description = request.getDescription();

        // ServiceRequestResponse request = requestOpt.get();
        PriorityAnalysisService.PriorityAnalysisResponse analysis = priorityAnalysisService
                .analyzePriority(description);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/{serviceRequestId}/public-details")
    public ResponseEntity<Map<String, Object>> getPublicServiceDetails(@PathVariable Long serviceRequestId) {

        ServiceRequestResponse request = serviceRequestService.getServiceRequestById(serviceRequestId);

        Map<String, Object> details = new HashMap<>();
        details.put("serviceRequestId", request.getId());
        details.put("requestType", request.getRequestType());
        details.put("status", request.getStatus());
        details.put("bayNumber", request.getBayNumber());
        details.put("laborCost", request.getLaborCost());
        details.put("completedDate", request.getCompletedDate());
        details.put("bill", request.getBill());
        details.put("partsUsed", request.getInventoryUsages());

        return ResponseEntity.ok(details);
    }

    @GetMapping("/bills/{billId}/download")
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable Long billId) {
        log.info("GET /vehicle/service-requests/bills/{}/download", billId);

        byte[] pdfContent = serviceRequestService.getStoredInvoicePdf(billId);
        String filename = "Invoice_BILL_" + billId + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(pdfContent);
    }

    @GetMapping("/{serviceRequestId}/download-invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long serviceRequestId) {
        log.info("GET /vehicle/service-requests/{}/download-invoice", serviceRequestId);

        ServiceRequestResponse request = serviceRequestService.getServiceRequestById(serviceRequestId);

        Map<String, Object> details = new HashMap<>();
        details.put("serviceRequestId", request.getId());
        details.put("requestType", request.getRequestType());
        details.put("status", request.getStatus());
        details.put("bayNumber", request.getBayNumber());
        details.put("laborCost", request.getLaborCost());
        details.put("completedDate", request.getCompletedDate());
        details.put("bill", request.getBill());
        details.put("partsUsed", request.getInventoryUsages());

        byte[] pdfContent = pdfService.generateInvoicePDF(details);
        String filename = "Invoice_SR_" + serviceRequestId + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(pdfContent);
    }

    @PostMapping
    public ResponseEntity<Void> createServiceRequest(
            @Valid @RequestBody CreateServiceRequestRequest request) {
        log.info("POST /vehicle/service-requests - Create service request");
        ServiceRequestResponse response = serviceRequestService.createServiceRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{serviceRequestId}/images")
    public ResponseEntity<Map<String, String>> uploadImages(
            @PathVariable Long serviceRequestId,
            @RequestParam("images") List<MultipartFile> images) throws IOException {
        log.info("POST /vehicle/service-requests/{}/images - Upload {} images", serviceRequestId, images.size());

        serviceRequestService.uploadImages(serviceRequestId, images);

        // Map<String, String> response = new HashMap<>();
        // response.put("message", "Images uploaded successfully");
        // response.put("count", String.valueOf(images.size()));
        //
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        log.info("GET /vehicle/service-requests/images/{} - Fetch image", imageId);

        ServiceImage image = serviceRequestService.getImage(imageId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getImageType()));
        headers.setContentDispositionFormData("inline", image.getImageName());

        return new ResponseEntity<>(image.getImageData(), headers, HttpStatus.OK);
    }

    @PostMapping("/{serviceRequestId}/assign-manager")
    public ResponseEntity<ServiceRequestResponse> assignManager(
            @PathVariable Long serviceRequestId,
            @Valid @RequestBody AssignManagerRequest request) {
        log.info("POST /vehicle/service-requests/{}/assign-manager", serviceRequestId);
        ServiceRequestResponse response = serviceRequestService.assignManager(serviceRequestId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{serviceRequestId}/assign-technician")
    public ResponseEntity<ServiceRequestResponse> assignTechnician(
            @PathVariable Long serviceRequestId,
            @Valid @RequestBody AssignTechnicianRequest request) {
        log.info("POST /vehicle/service-requests/{}/assign-technician", serviceRequestId);
        ServiceRequestResponse response = serviceRequestService.assignTechnician(serviceRequestId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{serviceRequestId}/status")
    public ResponseEntity<ServiceRequestResponse> updateStatus(
            @PathVariable Long serviceRequestId,
            @RequestParam String status) {
        log.info("PATCH /vehicle/service-requests/{}/status? status={}", serviceRequestId, status);
        ServiceRequestResponse response = serviceRequestService.updateStatus(serviceRequestId, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{serviceRequestId}/remarks")
    public ResponseEntity<ServiceRequestResponse> updateRemarks(
            @PathVariable Long serviceRequestId,
            @RequestBody Map<String, String> body) {
        log.info("PATCH /vehicle/service-requests/{}/remarks", serviceRequestId);
        String remarks = body.get("remarks");
        ServiceRequestResponse response = serviceRequestService.updateRemarks(serviceRequestId, remarks);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{serviceRequestId}/inventory-usage")
    public ResponseEntity<Map<String, String>> addInventoryUsage(
            @PathVariable Long serviceRequestId,
            @Valid @RequestBody AddInventoryUsageRequest request) {
        log.info("POST /vehicle/service-requests/{}/inventory-usage", serviceRequestId);

        serviceRequestService.addInventoryUsage(serviceRequestId, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Inventory usage added successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("billStatus")
    public ResponseEntity<Boolean> payBill(@RequestParam Long billId) {
        Boolean a = serviceRequestService.payBill(billId);
        return ResponseEntity.ok(a);
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequestResponse>> getAllServiceRequests() {
        log.info("GET /vehicle/service-requests - Fetch all service requests");
        List<ServiceRequestResponse> requests = serviceRequestService.getAllServiceRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{serviceRequestId}")
    public ResponseEntity<ServiceRequestResponse> getServiceRequestById(@PathVariable Long serviceRequestId) {
        log.info("GET /vehicle/service-requests/{}", serviceRequestId);
        ServiceRequestResponse request = serviceRequestService.getServiceRequestById(serviceRequestId);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ServiceRequestResponse>> getServiceRequestsByCustomerId(@PathVariable Long customerId) {
        log.info("GET /vehicle/service-requests/customer/{}", customerId);
        List<ServiceRequestResponse> requests = serviceRequestService.getServiceRequestsByCustomerId(customerId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<ServiceRequestResponse>> getServiceRequestsByTechnicianId(
            @PathVariable Long technicianId) {
        log.info("GET /vehicle/service-requests/technician/{}", technicianId);
        List<ServiceRequestResponse> requests = serviceRequestService.getServiceRequestsByTechnicianId(technicianId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ServiceRequestResponse>> getServiceRequestsByStatus(@PathVariable String status) {
        log.info("GET /vehicle/service-requests/status/{}", status);
        List<ServiceRequestResponse> requests = serviceRequestService.getServiceRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/{serviceRequestId}")
    public ResponseEntity<Map<String, String>> deleteServiceRequest(@PathVariable Long serviceRequestId) {
        log.info("DELETE /vehicle/service-requests/{}", serviceRequestId);

        serviceRequestService.deleteServiceRequest(serviceRequestId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Service request deleted successfully");
        response.put("serviceRequestId", serviceRequestId.toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/technician/{technicianId}/assigned")
    public ResponseEntity<List<ServiceRequestResponse>> getAssignedTasksByTechnician(
            @PathVariable Long technicianId) {
        List<ServiceRequestResponse> requests = serviceRequestService.getAssignedTasksByTechnician(technicianId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/technician/{technicianId}/in-progress")
    public ResponseEntity<List<ServiceRequestResponse>> getInProgressTasksByTechnician(
            @PathVariable Long technicianId) {
        List<ServiceRequestResponse> requests = serviceRequestService.getInProgressTasksByTechnician(technicianId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/technician/{technicianId}/completed")
    public ResponseEntity<List<ServiceRequestResponse>> getCompletedTasksByTechnician(
            @PathVariable Long technicianId) {
        List<ServiceRequestResponse> requests = serviceRequestService.getCompletedTasksByTechnician(technicianId);
        return ResponseEntity.ok(requests);
    }
}