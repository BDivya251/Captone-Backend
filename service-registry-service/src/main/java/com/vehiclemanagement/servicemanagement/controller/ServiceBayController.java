package com.vehiclemanagement.servicemanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceBayRequest;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceBayResponse;
import com.vehiclemanagement.servicemanagement.service.ServiceBayService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/vehicle/service-bays")
public class ServiceBayController {
	private final ServiceBayService serviceBayService;
	@PostMapping
	public ResponseEntity<Void> createServiceBay(@Valid @RequestBody CreateServiceBayRequest request){
		ServiceBayResponse response=serviceBayService.createServiceBay(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	 @GetMapping
	    public ResponseEntity<List<ServiceBayResponse>> getAllServiceBays() {
	        List<ServiceBayResponse> bays = serviceBayService.getAllServiceBays();
	        return ResponseEntity.ok(bays);
	    }
	    @GetMapping("/available")
	    public ResponseEntity<List<ServiceBayResponse>> getAvailableServiceBays() {
	        List<ServiceBayResponse> bays = serviceBayService.getAvailableServiceBays();
	        return ResponseEntity.ok(bays);
	    }
	    @DeleteMapping("/{bayId}")
	    public ResponseEntity<Map<String, String>> deleteServiceBay(@PathVariable Long bayId) {
	        serviceBayService.deleteServiceBay(bayId);
	        Map<String, String> response = new HashMap<>();
	        response.put("message", "Service bay deleted successfully");
	        response.put("bayId", bayId.toString());
	        return ResponseEntity.ok(response);
	    }
}
