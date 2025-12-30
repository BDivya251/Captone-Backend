package com.vehiclemanagement.servicemanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceBayRequest;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceBayResponse;
import com.vehiclemanagement.servicemanagement.service.ServiceBayService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicle/service-bays")
public class ServiceBayController {
	private final ServiceBayService serviceBayService;
	public ResponseEntity<ServiceBayResponse> createServiceBay(@Valid @RequestBody CreateServiceBayRequest request){
		ServiceBayResponse response=serviceBayService.createServiceBay(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
