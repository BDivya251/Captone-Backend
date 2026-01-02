package com.vehiclemanagement.servicemanagement.controller;

import com.vehiclemanagement.servicemanagement.dto.response.ServiceRequestResponse;
import com.vehiclemanagement.servicemanagement.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/service-details")
@RequiredArgsConstructor
@Slf4j
public class PublicServiceController {
    
    private final ServiceRequestService serviceRequestService;
    
    @GetMapping("/{serviceRequestId}")
    public String getServiceDetails(@PathVariable Long serviceRequestId, Model model) {
        log.info("GET /service-details/{} - Public invoice view", serviceRequestId);
        
        try {
            ServiceRequestResponse request = serviceRequestService.getServiceRequestById(serviceRequestId);
            
            model.addAttribute("serviceRequestId", request.getId());
            model.addAttribute("requestType", request.getRequestType());
            model.addAttribute("status", request.getStatus());
            model.addAttribute("bayNumber", request.getBayNumber());
            model.addAttribute("laborCost", request.getLaborCost());
            model.addAttribute("completedDate", request.getCompletedDate());
            model.addAttribute("bill", request.getBill());
            model.addAttribute("partsUsed", request.getInventoryUsages());
            
            return "public-invoice";
        } catch (Exception e) {
            log.error("Error loading service details: {}", serviceRequestId, e);
            model.addAttribute("error", "Service request not found");
            return "error";
        }
    }
}
