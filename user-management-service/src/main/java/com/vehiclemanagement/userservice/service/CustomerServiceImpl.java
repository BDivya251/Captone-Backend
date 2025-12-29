package com.vehiclemanagement.userservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vehiclemanagement.userservice.entity.Customer;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.repository.CustomerRepository;
import com.vehiclemanagement.userservice.serviceInterface.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(User user, String address) {
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setAddress(address);
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerByUserId(Long userId) {
    	
        return customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
