package com. vehiclemanagement.userservice. service;

import com.vehiclemanagement.userservice.dto. response.CustomerResponse;
import com.vehiclemanagement.userservice.entity.Customer;
import com.vehiclemanagement.userservice.exception.ResourceNotFoundException;
import com.vehiclemanagement.userservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern. slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public List<CustomerResponse> getAllCustomers() {
        log.info("Fetching all customers");
        
        List<Customer> customers = customerRepository.findAll();
        List<CustomerResponse> responseList = new ArrayList<>();
        
        for (Customer customer : customers) {
            CustomerResponse response = mapToResponse(customer);
            responseList.add(response);
        }
        
        return responseList;
    }
    
    public CustomerResponse getCustomerById(Long customerId) {
        log.info("Fetching customer by ID: {}", customerId);
        
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        
        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer not found with ID:  " + customerId);
        }
        
        Customer customer = customerOptional.get();
        return mapToResponse(customer);
    }
    
    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse. builder()
                .id(customer.getId())
                .userId(customer.getUserId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .build();
    }
}