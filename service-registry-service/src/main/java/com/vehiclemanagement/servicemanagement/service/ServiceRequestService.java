package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement.client.InventoryServiceClient;
import com.vehiclemanagement.servicemanagement.client.UserServiceClient;
import com.vehiclemanagement.servicemanagement.client.VehicleServiceClient;
import com.vehiclemanagement.servicemanagement.feign.CustomerResponse;
import com.vehiclemanagement.servicemanagement.feign.InventoryItemResponse;
import com.vehiclemanagement.servicemanagement.feign.TechnicianResponse;
import com.vehiclemanagement.servicemanagement.feign.User;
import com.vehiclemanagement.servicemanagement.feign.VehicleResponse;
import com.vehiclemanagement.servicemanagement.dto.request.AddInventoryUsageRequest;
import com.vehiclemanagement.servicemanagement.dto.request.AssignManagerRequest;
import com.vehiclemanagement.servicemanagement.dto.request.AssignTechnicianRequest;
import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceRequestRequest;
import com.vehiclemanagement.servicemanagement.dto.response.InventoryUsageResponse;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceBillResponse;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement.entity.InventoryUsage;
import com.vehiclemanagement.servicemanagement.entity.ServiceBill;
import com.vehiclemanagement.servicemanagement.entity.ServiceImage;
import com.vehiclemanagement.servicemanagement.entity.ServiceRequest;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement.exception.ResourceNotFoundException;
import com.vehiclemanagement.servicemanagement.repository.InventoryUsageRepository;
import com.vehiclemanagement.servicemanagement.repository.ServiceBillRepository;
import com.vehiclemanagement.servicemanagement.repository.ServiceImageRepository;
import com.vehiclemanagement.servicemanagement.repository.ServiceRequestRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceImageRepository serviceImageRepository;
    private final InventoryUsageRepository inventoryUsageRepository;
    private final ServiceBillRepository serviceBillRepository;

    private final UserServiceClient userServiceClient;
    private final VehicleServiceClient vehicleServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final ServiceBayService serviceBayService;
    private final QRCodeService qrCodeService;
    private final PDFService pdfService;
    private final EmailService emailService;

    public Integer getLengthOfServiceRequests() {
        return serviceRequestRepository.findAll().size();
    }

    // Get all assigned tasks for a technician
    public List<ServiceRequestResponse> getAssignedTasksByTechnician(Long technicianId) {
        validateTechnician(technicianId);
        List<ServiceRequest> requests = serviceRequestRepository.findByTechnicianIdAndStatus(technicianId, "ASSIGNED").orElse(null);
        List<ServiceRequestResponse> responseList = new ArrayList<>();
        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }
        return responseList;
    }

    // Get in progress for a technician
    public List<ServiceRequestResponse> getInProgressTasksByTechnician(Long technicianId) {
        validateTechnician(technicianId);
        List<ServiceRequest> requests = serviceRequestRepository.findByTechnicianIdAndStatus(technicianId,
                "IN_PROGRESS").orElse(null);
        List<ServiceRequestResponse> responseList = new ArrayList<>();
        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }
        return responseList;
    }

    // Get Completed by an technician
    public List<ServiceRequestResponse> getCompletedTasksByTechnician(Long technicianId) {
        validateTechnician(technicianId);
        List<ServiceRequest> requests = serviceRequestRepository.findByTechnicianIdAndStatus(technicianId, "COMPLETED").orElse(null);
        List<ServiceRequestResponse> responseList = new ArrayList<>();
        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }
        return responseList;
    }

    /**
     * 1. Create Service Request
     */
    @Transactional
    public ServiceRequestResponse createServiceRequest(CreateServiceRequestRequest request) {
        log.info("Creating service request for customer ID: {}", request.getCustomerId());

        validateCustomer(request.getCustomerId());

        VehicleResponse vehicle = validateVehicle(request.getVehicleId());
        if (!vehicle.getCustomerId().equals(request.getCustomerId())) {
            throw new BadRequestException("Vehicle does not belong to this customer");
        }

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomerId(request.getCustomerId());
        serviceRequest.setVehicleId(request.getVehicleId());
        serviceRequest.setRequestType(request.getRequestType());
        serviceRequest.setDescription(request.getDescription());
        serviceRequest.setStatus("PENDING");

        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);

        log.info("Service request created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * 2. Upload Car Images
     */
    @Transactional
    public void uploadImages(Long serviceRequestId, List<MultipartFile> files) throws IOException {
        log.info("Uploading {} images for service request ID: {}", files.size(), serviceRequestId);

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID: " + serviceRequestId);
        }

        ServiceRequest serviceRequest = requestOptional.get();

        for (MultipartFile file : files) {
            ServiceImage image = new ServiceImage();
            image.setServiceRequest(serviceRequest);
            image.setImageName(file.getOriginalFilename());
            image.setImageData(file.getBytes());
            image.setImageType(file.getContentType());

            serviceImageRepository.save(image);
        }

        log.info("Images uploaded successfully");
    }

    /**
     * 3. Get Image by ID
     */
    public ServiceImage getImage(Long imageId) {
        log.info("Fetching image ID: {}", imageId);

        Optional<ServiceImage> imageOptional = serviceImageRepository.findById(imageId);
        if (!imageOptional.isPresent()) {
            throw new ResourceNotFoundException("Image not found with ID: " + imageId);
        }

        return imageOptional.get();
    }

    public Boolean payBill(Long billId) {
        ServiceBill a = serviceBillRepository.findById(billId)
                .orElseThrow(() -> new BadRequestException("not a valid bill number"));
        a.setPaid(true);
        serviceBillRepository.save(a);
        return a.getPaid();
    }

    /**
     * 4. Assign Manager
     */
    @Transactional
    public ServiceRequestResponse assignManager(Long serviceRequestId, AssignManagerRequest request) {
        log.info("Assigning manager ID: {} to service request ID: {}", request.getManagerId(), serviceRequestId);

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID: " + serviceRequestId);
        }

        ServiceRequest serviceRequest = requestOptional.get();

        if (!"PENDING".equals(serviceRequest.getStatus())) {
            throw new BadRequestException(
                    "Service request is not in PENDING status.  Current status: " + serviceRequest.getStatus());
        }

        serviceRequest.setManagerId(request.getManagerId());
        serviceRequest.setStatus("MANAGER_ASSIGNED");

        ServiceRequest updated = serviceRequestRepository.save(serviceRequest);

        // Update vehicle status to INSERVICE
        try {
            VehicleResponse vehicle = vehicleServiceClient.getVehicleById(serviceRequest.getVehicleId());
            vehicleServiceClient.updateVehicleStatus("IN_SERVICE", vehicle.getRegistrationNumber());
            log.info("Vehicle status updated to IN_SERVICE for registration: {}", vehicle.getRegistrationNumber());
        } catch (Exception e) {
            log.error("Failed to update vehicle status to IN_SERVICE: {}", e.getMessage());
            // We log error but don't fail the transaction as the manager assignment is
            // primary
        }

        log.info("Manager assigned successfully");

        return mapToResponse(updated);
    }

    /**
     * 5. Assign Technician and Bay
     */
    @Transactional
    public ServiceRequestResponse assignTechnician(Long serviceRequestId, AssignTechnicianRequest request) {
        log.info("Assigning technician ID: {} and bay {} to service request ID: {}",
                request.getTechnicianId(), request.getBayNumber(), serviceRequestId);

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID: " + serviceRequestId);
        }

        ServiceRequest serviceRequest = requestOptional.get();
        userServiceClient.assignWork(request.getTechnicianId(), true);
        System.out.println("work assignedd");
        if (serviceRequest.getManagerId() == null) {
            throw new BadRequestException("Manager must be assigned first before assigning technician");
        }

        if (!"MANAGER_ASSIGNED".equals(serviceRequest.getStatus()) && !"PENDING".equals(serviceRequest.getStatus())) {
            throw new BadRequestException("Cannot assign technician.  Current status: " + serviceRequest.getStatus());
        }

        validateTechnician(request.getTechnicianId());

        if (!serviceBayService.isBayAvailable(request.getBayNumber())) {
            throw new BadRequestException("Bay " + request.getBayNumber() + " is not available");
        }

        serviceRequest.setTechnicianId(request.getTechnicianId());
        serviceRequest.setBayNumber(request.getBayNumber());
        serviceRequest.setIsBayAllocated(true);
        serviceRequest.setLaborCost(request.getLaborCost());

        serviceBayService.allocateBay(request.getBayNumber(), serviceRequestId);

        serviceRequest.setStatus("ASSIGNED");

        ServiceRequest updated = serviceRequestRepository.save(serviceRequest);
        userServiceClient.assignWork(request.getTechnicianId(), true);
        log.info("Technician and bay assigned successfully");

        return mapToResponse(updated);
    }

    /**
     * 6. Update Status
     */
    @Transactional
    public ServiceRequestResponse updateStatus(Long serviceRequestId, String newStatus) {
        log.info("Updating status for service request ID: {} to {}", serviceRequestId, newStatus);

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID: " + serviceRequestId);
        }

        ServiceRequest serviceRequest = requestOptional.get();

        String currentStatus = serviceRequest.getStatus();
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        serviceRequest.setStatus(newStatus);
        Long a = serviceRequest.getTechnicianId();
        if ("COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus)) {
            if (serviceRequest.getBayNumber() != null) {

                userServiceClient.assignWork(a, false);
                serviceBayService.releaseBay(serviceRequest.getBayNumber());
            }
        }

        if ("COMPLETED".equals(newStatus)) {
            serviceRequest.setCompletedDate(LocalDateTime.now());
            ServiceBill bill = generateBill(serviceRequest);
            try {
                userServiceClient.assignWork(a, false);
                sendCompletionEmailWithQR(serviceRequest, bill);
                log.info("Email sent");
            } catch (Exception e) {
                log.error("Error sending completion email: {}", e.getMessage(), e);

            }
        }

        ServiceRequest updated = serviceRequestRepository.save(serviceRequest);

        return mapToResponse(updated);
    }

    private void sendCompletionEmailWithQR(ServiceRequest serviceRequest, ServiceBill bill) {
        log.info("Sending completion email for service request ID: {}", serviceRequest.getId());

        try {
            // Get customer details
            CustomerResponse customer = userServiceClient.getCustomerById(serviceRequest.getCustomerId());
            User user = userServiceClient.getUserDetails(customer.getUserId());
            // Get vehicle details
            VehicleResponse vehicle = vehicleServiceClient.getVehicleById(serviceRequest.getVehicleId());
            String vehicleInfo = vehicle.getRegistrationNumber() + " - " + vehicle.getMake() + " " + vehicle.getModel();

            // Get technician name
            String technicianName = "N/A";
            if (serviceRequest.getTechnicianId() != null) {
                try {
                    TechnicianResponse technician = userServiceClient
                            .getTechnicianById(serviceRequest.getTechnicianId());
                    technicianName = technician.getName();
                } catch (Exception e) {
                    log.warn("Could not fetch technician details: {}", e.getMessage());
                }
            }

            // Get parts used
            List<InventoryUsage> partsUsed = inventoryUsageRepository.findByServiceRequestId(serviceRequest.getId());

            // Generate QR code using bill ID (direct PDF download)
            String qrCodeBase64 = qrCodeService.generateQRCodeBase64(bill.getId());
            String detailsUrl = qrCodeService.getDetailsUrl(bill.getId());

            // Send email
            emailService.sendServiceCompletionEmail(
                    user.getEmail(),
                    customer.getName(),
                    serviceRequest,
                    bill,
                    vehicleInfo,
                    technicianName,
                    partsUsed,
                    qrCodeBase64,
                    detailsUrl);

            log.info("Completion email sent successfully to: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to send completion email: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public ServiceRequestResponse updateRemarks(Long serviceRequestId, String remarks) {
        log.info("Updating remarks for service request ID:  {}", serviceRequestId);

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID:  " + serviceRequestId);
        }

        ServiceRequest serviceRequest = requestOptional.get();
        serviceRequest.setRemarks(remarks);

        ServiceRequest updated = serviceRequestRepository.save(serviceRequest);

        return mapToResponse(updated);
    }

    @Transactional
    public void addInventoryUsage(Long serviceRequestId, AddInventoryUsageRequest request) {

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID: " + serviceRequestId);
        }

        ServiceRequest serviceRequest = requestOptional.get();

        InventoryItemResponse item = inventoryServiceClient.getInventoryItemById(request.getInventoryItemId());

        if (item.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock.  Available: " + item.getQuantity());
        }

        InventoryUsage usage = new InventoryUsage();
        usage.setServiceRequest(serviceRequest);
        usage.setInventoryItemId(request.getInventoryItemId());
        usage.setPartName(item.getPartName());
        usage.setQuantity(request.getQuantity());
        usage.setUnitPrice(item.getUnitPrice());

        BigDecimal totalPrice = item.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        usage.setTotalPrice(totalPrice);

        inventoryUsageRepository.save(usage);

        inventoryServiceClient.updateQuantity(request.getInventoryItemId(), item.getQuantity() - request.getQuantity());

    }

    @Transactional
    public ServiceBill generateBill(ServiceRequest serviceRequest) {
        Optional<ServiceBill> existingBill = serviceBillRepository.findByServiceRequestId(serviceRequest.getId());

        List<InventoryUsage> usages = inventoryUsageRepository.findByServiceRequestId(serviceRequest.getId());
        BigDecimal partsCost = usages.stream()
                .map(InventoryUsage::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal laborCost = serviceRequest.getLaborCost() != null
                ? serviceRequest.getLaborCost()
                : new BigDecimal("500.00");

        BigDecimal subtotal = partsCost.add(laborCost);
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal totalAmount = subtotal.add(tax);

        ServiceBill bill = existingBill.orElseGet(ServiceBill::new);
        bill.setServiceRequest(serviceRequest);
        bill.setBillNumber(bill.getBillNumber() != null ? bill.getBillNumber()
                : "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setLaborCost(laborCost);
        bill.setPartsCost(partsCost);
        bill.setTax(tax);
        bill.setPaid(Boolean.TRUE.equals(bill.getPaid()));
        bill.setTotalAmount(totalAmount);
        if (bill.getQrToken() == null || bill.getQrToken().isBlank()) {
            bill.setQrToken(UUID.randomUUID().toString().replace("-", ""));
        }

        Map<String, Object> invoiceData = buildInvoiceData(serviceRequest, bill, usages);
        byte[] pdfBytes = pdfService.generateInvoicePDF(invoiceData);
        bill.setInvoicePdfBase64(Base64.getEncoder().encodeToString(pdfBytes));

        ServiceBill savedBill = serviceBillRepository.save(bill);
        serviceRequest.setTotalAmount(totalAmount);
        serviceRequestRepository.save(serviceRequest);
        return savedBill;
    }

    private Map<String, Object> buildInvoiceData(ServiceRequest serviceRequest, ServiceBill bill,
            List<InventoryUsage> usages) {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceRequestId", serviceRequest.getId());
        data.put("requestType", serviceRequest.getRequestType());
        data.put("status", serviceRequest.getStatus());
        data.put("bayNumber", serviceRequest.getBayNumber());
        data.put("laborCost", serviceRequest.getLaborCost());
        data.put("completedDate", serviceRequest.getCompletedDate());

        Map<String, Object> billMap = new HashMap<>();
        billMap.put("laborCost", bill.getLaborCost());
        billMap.put("partsCost", bill.getPartsCost());
        billMap.put("tax", bill.getTax());
        billMap.put("totalAmount", bill.getTotalAmount());
        data.put("bill", billMap);

        List<Map<String, Object>> partsList = new ArrayList<>();
        for (InventoryUsage usage : usages) {
            Map<String, Object> partData = new HashMap<>();
            partData.put("partName", usage.getPartName());
            partData.put("quantity", usage.getQuantity());
            partData.put("unitPrice", usage.getUnitPrice());
            partsList.add(partData);
        }
        data.put("partsUsed", partsList);

        return data;
    }

    public byte[] getStoredInvoicePdf(Long billId) {
        ServiceBill bill = serviceBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + billId));

        if (bill.getInvoicePdfBase64() != null && !bill.getInvoicePdfBase64().isBlank()) {
            return Base64.getDecoder().decode(bill.getInvoicePdfBase64());
        }

        ServiceRequest serviceRequest = bill.getServiceRequest();
        List<InventoryUsage> usages = inventoryUsageRepository.findByServiceRequestId(serviceRequest.getId());
        Map<String, Object> invoiceData = buildInvoiceData(serviceRequest, bill, usages);
        byte[] pdfBytes = pdfService.generateInvoicePDF(invoiceData);
        bill.setInvoicePdfBase64(Base64.getEncoder().encodeToString(pdfBytes));
        serviceBillRepository.save(bill);
        return pdfBytes;
    }

    public byte[] getInvoicePdfByServiceRequest(Long serviceRequestId) {
        ServiceBill bill = serviceBillRepository.findByServiceRequestId(serviceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found for service request ID: " + serviceRequestId));
        return getStoredInvoicePdf(bill.getId());
    }

    public List<ServiceRequestResponse> getAllServiceRequests() {
        log.info("Fetching all service requests");

        List<ServiceRequest> requests = serviceRequestRepository.findAll();
        List<ServiceRequestResponse> responseList = new ArrayList<>();

        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }

        return responseList;
    }

    public ServiceRequestResponse getServiceRequestById(Long serviceRequestId) {
        log.info("Fetching service request by ID: {}", serviceRequestId);

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID: " + serviceRequestId);
        }

        return mapToResponse(requestOptional.get());
    }

    public List<ServiceRequestResponse> getServiceRequestsByCustomerId(Long customerId) {
        log.info("Fetching service requests for customer ID: {}", customerId);

        List<ServiceRequest> requests = serviceRequestRepository.findByCustomerId(customerId);
        List<ServiceRequestResponse> responseList = new ArrayList<>();

        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }

        return responseList;
    }
    
    public List<ServiceRequestResponse> getServiceRequestsByManagerId(Long managerId) {
        log.info("Fetching service requests for manager ID: {}", managerId);
        List<ServiceRequest> requests = serviceRequestRepository.findByManagerId(managerId);
        List<ServiceRequestResponse> responseList = new ArrayList<>();
        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }
        return responseList;
    }
    

    public List<ServiceRequestResponse> getServiceRequestsByTechnicianId(Long technicianId) {
        log.info("Fetching service requests for technician ID: {}", technicianId);

        List<ServiceRequest> requests = serviceRequestRepository.findByTechnicianId(technicianId);
        List<ServiceRequestResponse> responseList = new ArrayList<>();

        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }

        return responseList;
    }

    public List<ServiceRequestResponse> getServiceRequestsByStatus(String status) {
        log.info("Fetching service requests with status: {}", status);

        List<ServiceRequest> requests = serviceRequestRepository.findByStatus(status);
        List<ServiceRequestResponse> responseList = new ArrayList<>();

        for (ServiceRequest request : requests) {
            responseList.add(mapToResponse(request));
        }

        return responseList;
    }

    @Transactional
    public void deleteServiceRequest(Long serviceRequestId) {
        log.info("Deleting service request ID: {}", serviceRequestId);

        Optional<ServiceRequest> requestOptional = serviceRequestRepository.findById(serviceRequestId);
        if (!requestOptional.isPresent()) {
            throw new ResourceNotFoundException("Service request not found with ID:  " + serviceRequestId);
        }

        serviceRequestRepository.deleteById(serviceRequestId);

        log.info("Service request deleted successfully");
    }

    private void validateCustomer(Long customerId) {
        try {
            CustomerResponse customer = userServiceClient.getCustomerById(customerId);
            log.info("Customer validated:  {}", customer.getName());
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Customer not found with ID: " + customerId);
        }
    }

    private VehicleResponse validateVehicle(Long vehicleId) {
        try {
            return vehicleServiceClient.getVehicleById(vehicleId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Vehicle not found with ID: " + vehicleId);
        }
    }

    private void validateTechnician(Long technicianId) {
        try {
            TechnicianResponse technician = userServiceClient.getTechnicianById(technicianId);
            log.info("Technician validated: {}", technician.getName());
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Technician not found with ID: " + technicianId);
        }
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if ("CANCELLED".equals(newStatus)) {
            return true;
        }

        if ("PENDING".equals(currentStatus) && "MANAGER_ASSIGNED".equals(newStatus)) {
            return true;
        }

        if ("MANAGER_ASSIGNED".equals(currentStatus) && "ASSIGNED".equals(newStatus)) {
            return true;
        }

        if ("ASSIGNED".equals(currentStatus) && "IN_PROGRESS".equals(newStatus)) {
            return true;
        }

        if ("IN_PROGRESS".equals(currentStatus) && "COMPLETED".equals(newStatus)) {
            return true;
        }

        return false;
    }

    private ServiceRequestResponse mapToResponse(ServiceRequest request) {
        List<InventoryUsage> usages = inventoryUsageRepository.findByServiceRequestId(request.getId());
        List<InventoryUsageResponse> usageResponses = new ArrayList<>();

        for (InventoryUsage usage : usages) {
            InventoryUsageResponse usageResponse = InventoryUsageResponse.builder()
                    .id(usage.getId())
                    .inventoryItemId(usage.getInventoryItemId())
                    .partName(usage.getPartName())
                    .quantity(usage.getQuantity())
                    .unitPrice(usage.getUnitPrice())
                    .totalPrice(usage.getTotalPrice())
                    .build();
            usageResponses.add(usageResponse);
        }

        ServiceBillResponse billResponse = null;
        Optional<ServiceBill> billOptional = serviceBillRepository.findByServiceRequestId(request.getId());
        if (billOptional.isPresent()) {
            ServiceBill bill = billOptional.get();
            billResponse = ServiceBillResponse.builder()
                    .id(bill.getId())
                    .billNumber(bill.getBillNumber())
                    .laborCost(bill.getLaborCost())
                    .partsCost(bill.getPartsCost())
                    .tax(bill.getTax())
                    .paid(false)
                    .totalAmount(bill.getTotalAmount())
                    .generatedDate(bill.getGeneratedDate())
                    .build();
        }

        List<ServiceImage> images = serviceImageRepository.findByServiceRequestId(request.getId());
        List<String> imageIds = new ArrayList<>();
        for (ServiceImage image : images) {
            imageIds.add(String.valueOf(image.getId()));
        }

        return ServiceRequestResponse.builder()
                .id(request.getId())
                .customerId(request.getCustomerId())
                .vehicleId(request.getVehicleId())
                .requestType(request.getRequestType())
                .description(request.getDescription())
                .status(request.getStatus())
                .managerId(request.getManagerId())
                .technicianId(request.getTechnicianId())
                .bayNumber(request.getBayNumber())
                .isBayAllocated(request.getIsBayAllocated())
                .remarks(request.getRemarks())
                .totalAmount(request.getTotalAmount())
                .requestDate(request.getRequestDate())
                .laborCost(request.getLaborCost())
                .completedDate(request.getCompletedDate())
                .imageIds(imageIds)
                .inventoryUsages(usageResponses)
                .bill(billResponse)
                .build();
    }
}