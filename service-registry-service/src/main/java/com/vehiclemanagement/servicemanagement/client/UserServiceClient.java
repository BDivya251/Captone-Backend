package com.vehiclemanagement.servicemanagement.client;

import com.vehiclemanagement.servicemanagement.feign.CustomerResponse;
import com.vehiclemanagement.servicemanagement.feign.TechnicianResponse;
import com.vehiclemanagement.servicemanagement.feign.User;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web. bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/customers/{customerId}")
    CustomerResponse getCustomerById(@PathVariable("customerId") Long customerId);
    
    @GetMapping("/api/technicians/{technicianId}")
    TechnicianResponse getTechnicianById(@PathVariable("technicianId") Long technicianId);
    

    @GetMapping("api/auth/user/{id}")
    User getUserDetails(@PathVariable Long id);
}