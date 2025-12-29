package com.vehiclemanagement.userservice.serviceInterface;

import java.util.List;

import com.vehiclemanagement.userservice.entity.Customer;
import com.vehiclemanagement.userservice.entity.User;

public interface CustomerService {
    Customer createCustomer(User user, String address);
    Customer getCustomerByUserId(Long userId);
    List<Customer> getAllCustomers();
}
