package com.vehiclemanagement.vehicle.client;

import com.vehiclemanagement.vehicle.dto.feign.CustomerResponse;
import org.springframework. cloud.openfeign.FeignClient;
import org.springframework. web.bind.annotation.GetMapping;
import org.springframework.web. bind.annotation.PathVariable;

@FeignClient(name = "user-service")  
public interface UserServiceClient {
    
    @GetMapping("/api/customers/{customerId}")
    CustomerResponse getCustomerById(@PathVariable("customerId") Long customerId);
}