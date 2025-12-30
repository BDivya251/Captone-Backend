package com.vehiclemanagement. userservice.controller;

import com.vehiclemanagement.userservice.dto.response.CustomerResponse;
import com.vehiclemanagement.userservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok. extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind. annotation.*;

import java.util. List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
      
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId) {
     
        CustomerResponse customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }
}